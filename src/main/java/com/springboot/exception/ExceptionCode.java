package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    COFFEE_NOT_FOUND(404, "Coffee not found"),
    COFFEE_CODE_EXISTS(409, "Coffee Code exists"),
    MEMBER_NOT_FOUND(404, "Member Not found"),
    MEMBER_EXISTS(409, "Member exists"),
    ORDER_NOT_FOUND(404, "Member not found"),
    CANNOT_CHANGE_ORDER(403,  "Order can not change"),
    NOT_IMPLEMENTATION(501, "Not Implementation"),
    INVALID_MEMBER_STATUS(400, "Invalid member status");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}
