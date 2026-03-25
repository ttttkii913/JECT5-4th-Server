package com.sossbar.global.common.template;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.code.SuccessCode;
import lombok.*;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ApiResTemplate<T> {

    private final int status;       // HTTTP 상태 코드
    private final String code;      // 응답 코드 (AUTH-001)
    private final String message;   // 응답 메시지
    private T data;                 // 응답 데이터

    // 데이터 없는 성공 응답
    public static ApiResTemplate successWithNoContent(SuccessCode successCode) {
        return new ApiResTemplate<>(successCode.getHttpStatusCode(), successCode.getCode(), successCode.getMessage());
    }

    // 데이터 포함한 성공 응답
    public static <T> ApiResTemplate<T> successResponse(SuccessCode successCode, T data) {
        return new ApiResTemplate<>(successCode.getHttpStatusCode(), successCode.getCode(), successCode.getMessage(), data);
    }

    // 에러 응답 (커스텀 메시지 포함)
    public static ApiResTemplate errorResponse(ErrorCode errorCode, String customMessage) {
        return new ApiResTemplate<>(errorCode.getHttpStatusCode(), errorCode.getCode() , customMessage);
    }
}
