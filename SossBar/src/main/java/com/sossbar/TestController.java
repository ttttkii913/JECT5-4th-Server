package com.sossbar;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.global.common.template.SwaggerApiResTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Tag(name = "TEST API", description = "test API")
@SwaggerApiResTemplate
public class TestController {

    // 공통 응답, 스웨거 어노테이션 확인용 테스트 컨트롤러 - 추후 삭제
    @Operation(summary = "성공 응답 테스트", description = "테스트용 성공 응답 컨트롤러입니다.")
    @GetMapping("/success")
    public ApiResTemplate<String> test() {
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS,
                "테스트용 성공 응답입니다.");
    }

    @Operation(summary = "에러 응답 테스트", description = "테스트용 에러 응답 컨트롤러입니다.")
    @GetMapping("/error")
    public ApiResTemplate<String> testError() {
        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                "테스트용 에러 응답입니다.");
    }
}
