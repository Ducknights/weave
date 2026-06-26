package org.example.service;

import org.example.model.dto.ActionDto;

import java.util.List;

public interface ActionService {
    void addRecord(ActionDto dto);

    void deleteRecord(ActionDto dto);

    List<Long> getRecord(ActionDto dto, int page, int size);

    void cacheUserAction(Long id);
}
