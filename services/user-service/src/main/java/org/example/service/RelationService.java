package org.example.service;

import org.example.model.dto.RelationDto;

import java.util.List;

public interface RelationService {
    void addRecord(RelationDto dto);

    void deleteRecord(RelationDto dto);

    List<Long> getRecord(RelationDto dto, int page, int size);
}
