package com.sossbar.oauth2.kakao.controller;

import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.global.common.template.SwaggerApiResTemplate;
import com.sossbar.oauth2.kakao.dto.KakaoToken;
import com.sossbar.oauth2.kakao.service.KakaoLoginService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
@SwaggerApiResTemplate
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @Operation(summary = "카카오 로그인", description = "카카오 로그인 콜백 api입니다.")
    @GetMapping("/kakao")
    public ApiResTemplate<KakaoToken> kakaoCallback(@RequestParam String code) {
        KakaoToken kakaoToken = kakaoLoginService.processLogin(code);
        return ApiResTemplate.successResponse(SuccessCode.SUCCESS, kakaoToken);
    }
}
