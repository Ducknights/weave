package com.weave.draft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weave.draft.model.dto.DraftDto;
import com.weave.draft.model.entity.Draft;
import com.weave.draft.model.vo.DraftVo;

import java.util.List;

public interface DraftService extends IService<Draft> {

    /** 保存草稿 */
    Long saveDraft(Long userId, DraftDto draftDto);

    /** 更新草稿 */
    void updateDraft(Long draftId, Long userId, DraftDto draftDto);

    /** 删除草稿 */
    void deleteDraft(Long draftId, Long userId);

    /** 提交审核（草稿 -> 审核中） */
    void submitForReview(Long draftId, Long userId);

    /** 审核通过（审核中 -> 审核通过），并发送发布消息给 post-service */
    void approve(Long draftId, Long reviewerId, String remark);

    /** 审核驳回（审核中 -> 审核驳回） */
    void reject(Long draftId, Long reviewerId, String remark);

    /** 获取草稿详情 */
    DraftVo getDraftDetail(Long draftId, Long userId);

    /** 获取当前用户的草稿列表 */
    List<DraftVo> getMyDrafts(Long userId);

    /** 获取当前用户待审核的草稿列表 */
    List<DraftVo> getMyPendingDrafts(Long userId);

    /** 获取所有待审核的草稿列表（审核员/管理员） */
    List<DraftVo> getAllPendingDrafts();
}
