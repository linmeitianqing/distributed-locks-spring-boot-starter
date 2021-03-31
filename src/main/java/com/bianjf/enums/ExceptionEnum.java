package com.bianjf.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {
    NOT_CONFIG(1, "配置不存在"),

    LOCK_OCCUPIED(2, "锁被占用");

    private Integer code;

    private String desc;

    ExceptionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
