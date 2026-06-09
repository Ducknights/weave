package org.example.service;

import org.example.model.dto.ActionDto;

public interface ActionService {
    void addRecord(ActionDto dto);

    void deleteRecord(ActionDto dto);
}