package org.example.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.dto.PostDetailVo;
import org.example.feign.PostFeignClient;
import org.example.model.dto.SearchResultDto;
import org.example.service.ConversionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ConversionServiceImpl implements ConversionService {

    @Resource
    private PostFeignClient postFeignClient;

    @Override
    public List<PostDetailVo> convertToPostDetailVo(List<SearchResultDto> results) {
        if (results == null || results.isEmpty()) {
            return List.of();
        }

        // 1. 提取帖子ID列表
        List<Long> postIds = results.stream()
                .map(SearchResultDto::getId)
                .toList();

        // 2. 批量获取帖子信息
        List<PostDetailVo> postList = postFeignClient.getPostsByIds(postIds);
        if (postList == null || postList.isEmpty()) {
            return List.of();
        }

        //构造map
        Map<Long, PostDetailVo> postMap = postList.stream()
                .collect(Collectors.toMap(PostDetailVo::getId, post -> post));

        // 3. 按照搜索结果顺序组装，并设置搜索分数
        List<PostDetailVo> postVoList = new ArrayList<>();
        for (SearchResultDto result : results) {
            PostDetailVo post = postMap.get(result.getId());
            if (post != null) {
                post.setScore(result.getScore());
                postVoList.add(post);
            }
        }
        return postVoList;
    }
}
