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

    // JWT
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT가 만료되었습니다.", "JWT-001"),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT입니다.", "JWT-002"),
    JWT_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "JWT 서명 검증에 실패했습니다.", "JWT-003"),
    JWT_EMPTY(HttpStatus.UNAUTHORIZED, "JWT가 비어있거나 잘못되었습니다.", "JWT-004"),

    // AUTH
    UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.", "AUTH-001"),
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", "AUTH-002"),
    KAKAO_TOKEN_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "카카오 토큰 요청 실패", "AUTH-003"),
    KAKAO_USER_INFO_FAILED(HttpStatus.BAD_GATEWAY, "카카오 사용자 정보 조회 실패", "AUTH-004"),
    KAKAO_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "카카오 계정에 이메일이 없습니다.", "AUTH-005"),
    KAKAO_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "카카오 로그인에 실패했습니다.", "AUTH-006"),

    // USER
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 사용자가 없습니다. userId = ", "USER-001");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
