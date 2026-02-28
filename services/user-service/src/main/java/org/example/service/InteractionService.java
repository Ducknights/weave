package org.example.service;

import org.example.dto.InteractionDto;

import java.util.List;

public interface InteractionService {
    void addRecord(InteractionDto dto);

    void deleteRecord(InteractionDto dto);

    List<Long> getRecord(InteractionDto dto);
}
