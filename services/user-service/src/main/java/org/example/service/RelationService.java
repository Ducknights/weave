package org.example.service;

import org.example.model.dto.RelationDto;

import java.util.Set;

public interface RelationService {
    void addRecord(RelationDto dto);

    void deleteRecord(RelationDto dto);

    Set<Long> getRecord(RelationDto dto, int page, int size);
}
