package com.weave.model.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.weave.model.model.dto.SearchDocumentDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSyncMessage {
    private String operation;
    private SearchDocumentDto data;
}
