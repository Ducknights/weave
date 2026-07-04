package com.weave.recommend.service;

import com.weave.recommend.model.dto.ActionDto;

public interface ActionService {
    void addRecord(ActionDto dto);

    void deleteRecord(ActionDto dto);
}