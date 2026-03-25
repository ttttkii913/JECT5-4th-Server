package com.sossbar.global.common.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    // COMMON
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "유효성 검사에 실패하였습니다.", "COMMON-001"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 에러가 발생했습니다.", "COMMON-002"),

    // AUTH
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT가 만료되었습니다.", "AUTH-001"),
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", "AUTH-002"),

    // USER
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 사용자가 없습니다. userId = ", "USER-001");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
