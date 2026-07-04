package com.weave.user.service;

import com.weave.user.model.dto.RelationDto;

import java.util.List;

public interface RelationService {
    void addRecord(RelationDto dto);

    void deleteRecord(RelationDto dto);

    List<Long> getRecord(RelationDto dto, int page, int size);

    void cacheUserRelation(Long userId);
}
