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
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.", "AUTH-007"),

    // IMAGE FILE
    FILE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "파일이 없습니다.", "FILE-001"),
    FILE_UPLOAD_FAIL_EXCEPTION(HttpStatus.BAD_GATEWAY, "파일 업로드에 실패하였습니다.", "FILE-002"),
    FILE_SIZE_EXCEEDED_EXCEPTION(HttpStatus.BAD_GATEWAY, "파일 크기가 너무 큽니다. (최대 5MB)", "FILE-003"),
    INVALID_FILE_TYPE_EXCEPTION(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다. (jpg, jpeg, png, gif만 허용)", "FILE-004"),

    // USER
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 사용자가 없습니다. userId = ", "USER-001"),

    // PROJECT
    PROJECT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 프로젝트가 없습니다. projectId = ", "PROJECT-001"),
    PROJECT_CREATE_ROLLBACK_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "프로젝트 생성 중 DB 오류가 발생하여 업로드된 이미지를 롤백했습니다. imageUrl = ", "PROJECT-002"),
    PROJECT_UPDATE_ROLLBACK_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "프로젝트 수정 중 DB 오류가 발생하여 업로드된 이미지를 롤백했습니다. imageUrl = ", "PROJECT-003"),
    PROJECT_MEMBER_ALREADY_EXISTS_EXCEPTION(HttpStatus.CONFLICT, "이미 해당 프로젝트에 추가된 사용자입니다. userId = ", "PROJECT-004"),
    PROJECT_MEMBER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 프로젝트에 참여 중인 사용자가 아닙니다.", "PROJECT-005"),
    UNAUTHORIZED_MEMBER_DELETION_EXCEPTION(HttpStatus.FORBIDDEN, "팀장만 멤버를 삭제할 수 있습니다.", "PROJECT-006"),
    UNAUTHORIZED_MEMBER_CONFIRMATION_EXCEPTION(HttpStatus.FORBIDDEN,"팀장만 팀 멤버를 확정할 수 있습니다.","PROJECT-007"),
    INVALID_PROJECT_STATUS_EXCEPTION(HttpStatus.BAD_REQUEST,"현재 활성화된 프로젝트만 확정할 수 있습니다.","PROJECT-008"),

    // REVIEW
    DUPLICATE_REVIEW_EXCEPTION(HttpStatus.CONFLICT,"이미 해당 사용자에게 후기를 남겼습니다. revieweeId = ","REVIEW-001"),
    SELF_REVIEW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신에게 후기를 남길 수 없습니다.", "REVIEW-002"),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "일부 태그가 존재하지 않습니다.", "REVIEW-003"),
    SPECTRUM_NOT_FOUND(HttpStatus.NOT_FOUND, "일부 스펙트럼이 존재하지 않습니다.", "REVIEW-004");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
