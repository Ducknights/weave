package org.example.service;

import org.example.dto.ActionDto;
import org.example.dto.RelationDto;

import java.util.Set;

public interface ActionService {
    void addRecord(ActionDto dto);

    void deleteRecord(ActionDto dto);

    Set<Long> getRecord(ActionDto dto, int page, int size);
}
