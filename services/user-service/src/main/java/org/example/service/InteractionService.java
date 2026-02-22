package org.example.service;

import org.example.dto.UserInteractionDto;

import java.util.List;

public interface InteractionService {
    void addRecord(UserInteractionDto dto);

    void deleteRecord(UserInteractionDto dto);

    List<Long> getRecord(UserInteractionDto dto);
}
