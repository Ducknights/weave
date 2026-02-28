package org.example.model;


import lombok.Getter;

@Getter
public enum AudioStatus {
    PENDING(0,"待审核"), // 待审核
    APPROVED(1,"审核通过"), // 审核通过
    REJECTED(2,"审核拒绝"), // 审核拒绝
    HIDDEN(3,"隐藏"), // 隐藏
    DELETED(4,"删除"); // 删除

    private final int code;
    private final String description;

    AudioStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
