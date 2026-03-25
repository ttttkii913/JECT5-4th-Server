package com.sossbar.global.common.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {

    // COMMON
    SUCCESS(HttpStatus.OK, "요청이 성공했습니다.", "COMMON-200"),
    GET_SUCCESS(HttpStatus.OK, "성공적으로 조회했습니다.", "COMMON-200"),
    UPDATE_SUCCESS(HttpStatus.OK, "성공적으로 수정하였습니다.", "COMMON-200"),
    CREATE_SUCCESS(HttpStatus.CREATED, "성공적으로 생성하였습니다.", "COMMON-201"),
    DELETE_SUCCESS(HttpStatus.NO_CONTENT, "성공적으로 삭제하였습니다.", "COMMON-204");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
