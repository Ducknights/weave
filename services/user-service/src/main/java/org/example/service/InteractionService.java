package org.example.service;

import org.example.dto.InteractionDto;

import java.util.List;
import java.util.Set;

public interface InteractionService {
    void addRecord(InteractionDto dto);

    void deleteRecord(InteractionDto dto);

    Set<Long> getRecord(InteractionDto dto, int page, int size);
}
