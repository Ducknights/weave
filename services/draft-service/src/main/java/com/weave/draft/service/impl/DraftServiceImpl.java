package com.weave.draft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weave.draft.exception.BusinessException;
import com.weave.draft.mapper.DraftMapper;
import com.weave.draft.mapper.DraftResourceMapper;
import com.weave.draft.model.dto.DraftDto;
import com.weave.draft.model.entity.Draft;
import com.weave.draft.model.entity.DraftResource;
import com.weave.draft.model.enums.DraftApiStatus;
import com.weave.draft.model.enums.DraftStateEvent;
import com.weave.draft.model.enums.DraftStatus;
import com.weave.draft.model.vo.DraftVo;
import com.weave.draft.service.DraftService;
import com.weave.draft.service.DraftStateMachineService;
import com.weave.model.model.dto.DraftPublishMessageDto;
import com.weave.rabbitmq.util.MQUtil;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
public class DraftServiceImpl extends ServiceImpl<DraftMapper, Draft> implements DraftService {

    @Resource
    private DraftMapper draftMapper;
    @Resource
    private DraftResourceMapper draftResourceMapper;
    @Resource
    private DraftStateMachineService stateMachineService;
    @Resource
    private MQUtil mqUtil;

    @Override
    public Long saveDraft(Long userId, DraftDto draftDto) {
        Draft draft = Draft.builder()
                .userId(userId)
                .clubId(draftDto.getClubId())
                .title(draftDto.getTitle())
                .content(draftDto.getContent())
                .status(DraftStatus.DRAFT)
                .build();
        draftMapper.insert(draft);
        saveResources(draft.getDraftId(), draftDto.getCoverImage());
        return draft.getDraftId();
    }

    @Override
    public void updateDraft(Long draftId, Long userId, DraftDto draftDto) {
        Draft draft = requireOwnedDraft(draftId, userId);
        // 审核中的草稿不允许修改
        if (draft.getStatus() == DraftStatus.PENDING) {
            throw new BusinessException(DraftApiStatus.PERMISSION_DENIED);
        }
        draft.setTitle(draftDto.getTitle());
        draft.setContent(draftDto.getContent());
        draft.setClubId(draftDto.getClubId());
        // 被驳回后重新编辑，回到草稿状态
        if (draft.getStatus() == DraftStatus.REJECTED) {
            draft.setStatus(DraftStatus.DRAFT);
            draft.setReviewRemark(null);
        }
        draftMapper.updateById(draft);
        // 覆盖式更新资源
        replaceResources(draftId, draftDto.getCoverImage());
    }

    @Override
    public void deleteDraft(Long draftId, Long userId) {
        Draft draft = requireOwnedDraft(draftId, userId);
        draftMapper.deleteById(draft.getDraftId());
        draftResourceMapper.selectByDraftId(draftId)
                .forEach(r -> draftResourceMapper.deleteById(r.getId()));
    }

    @Override
    public void submitForReview(Long draftId, Long userId) {
        Draft draft = requireOwnedDraft(draftId, userId);
        DraftStatus newStatus = stateMachineService.sendEvent(draft, DraftStateEvent.SUBMIT);
        draft.setStatus(newStatus);
        draftMapper.updateById(draft);
    }

    @Override
    public void approve(Long draftId, Long reviewerId, String remark) {
        Draft draft = requireDraft(draftId);
        DraftStatus newStatus = stateMachineService.sendEvent(draft, DraftStateEvent.APPROVE);
        draft.setStatus(newStatus);
        draft.setReviewerId(reviewerId);
        draft.setReviewRemark(remark);
        draftMapper.updateById(draft);
        // 发送发布消息给 post-service
        sendPublishMessage(draft);
    }

    @Override
    public void reject(Long draftId, Long reviewerId, String remark) {
        Draft draft = requireDraft(draftId);
        DraftStatus newStatus = stateMachineService.sendEvent(draft, DraftStateEvent.REJECT);
        draft.setStatus(newStatus);
        draft.setReviewerId(reviewerId);
        draft.setReviewRemark(remark);
        draftMapper.updateById(draft);
    }

    @Override
    public DraftVo getDraftDetail(Long draftId, Long userId) {
        Draft draft = requireOwnedDraft(draftId, userId);
        return toVo(draft, loadResources(draftId));
    }

    @Override
    public List<DraftVo> getMyDrafts(Long userId) {
        return toVoList(draftMapper.selectByUserId(userId));
    }

    @Override
    public List<DraftVo> getMyPendingDrafts(Long userId) {
        return toVoList(draftMapper.selectByUserIdAndStatus(userId, DraftStatus.PENDING));
    }

    @Override
    public List<DraftVo> getAllPendingDrafts() {
        return toVoList(draftMapper.selectByStatus(DraftStatus.PENDING));
    }

    // ============ 私有辅助方法 ============

    private Draft requireDraft(Long draftId) {
        Draft draft = draftMapper.selectById(draftId);
        if (draft == null) {
            throw new BusinessException(DraftApiStatus.DRAFT_NOT_FOUND);
        }
        return draft;
    }

    private Draft requireOwnedDraft(Long draftId, Long userId) {
        Draft draft = requireDraft(draftId);
        if (!draft.getUserId().equals(userId)) {
            throw new BusinessException(DraftApiStatus.PERMISSION_DENIED);
        }
        return draft;
    }

    private void saveResources(Long draftId, List<String> resources) {
        if (CollectionUtils.isEmpty(resources)) {
            return;
        }
        for (String path : resources) {
            DraftResource resource = DraftResource.builder()
                    .draftId(draftId)
                    .resourcePath(path)
                    .build();
            draftResourceMapper.insert(resource);
        }
    }

    private void replaceResources(Long draftId, List<String> resources) {
        draftResourceMapper.selectByDraftId(draftId)
                .forEach(r -> draftResourceMapper.deleteById(r.getId()));
        saveResources(draftId, resources);
    }

    private List<String> loadResources(Long draftId) {
        return draftResourceMapper.selectByDraftId(draftId).stream()
                .map(DraftResource::getResourcePath)
                .collect(Collectors.toList());
    }

    private void sendPublishMessage(Draft draft) {
        DraftPublishMessageDto message = DraftPublishMessageDto.builder()
                .draftId(draft.getDraftId())
                .userId(draft.getUserId())
                .clubId(draft.getClubId())
                .title(draft.getTitle())
                .content(draft.getContent())
                .resources(loadResources(draft.getDraftId()))
                .build();
        log.info("发送草稿发布消息: draftId={}, userId={}", draft.getDraftId(), draft.getUserId());
        mqUtil.sendDraftPublish(message);
    }

    private List<DraftVo> toVoList(List<Draft> drafts) {
        if (CollectionUtils.isEmpty(drafts)) {
            return List.of();
        }
        List<Long> draftIds = drafts.stream().map(Draft::getDraftId).toList();
        var resourceMap = draftResourceMapper.selectByDraftIds(draftIds).stream()
                .collect(Collectors.groupingBy(
                        DraftResource::getDraftId,
                        Collectors.mapping(DraftResource::getResourcePath, Collectors.toList())));
        return drafts.stream()
                .map(draft -> toVo(draft, resourceMap.getOrDefault(draft.getDraftId(), List.of())))
                .toList();
    }

    private DraftVo toVo(Draft draft, List<String> resources) {
        return DraftVo.builder()
                .draftId(draft.getDraftId())
                .userId(draft.getUserId())
                .clubId(draft.getClubId())
                .title(draft.getTitle())
                .content(draft.getContent())
                .status(draft.getStatus())
                .reviewRemark(draft.getReviewRemark())
                .publishedPostId(draft.getPublishedPostId())
                .resources(resources)
                .createdTime(draft.getCreatedTime())
                .updatedTime(draft.getUpdatedTime())
                .build();
    }
}
