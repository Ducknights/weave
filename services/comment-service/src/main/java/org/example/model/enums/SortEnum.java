package org.example.model.enums;

import lombok.Getter;

@Getter
public enum SortEnum {
    TIME("time"),
    HOT("hot");

    private final String value;
    SortEnum(String value) {
        this.value = value;
    }
}
