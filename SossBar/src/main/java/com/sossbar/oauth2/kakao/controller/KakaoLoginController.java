package com.sossbar.oauth2.kakao.controller;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.global.common.template.SwaggerApiResTemplate;
import com.sossbar.oauth2.kakao.dto.LoginInfoResDto;
import com.sossbar.oauth2.kakao.service.KakaoLoginService;
import com.sossbar.oauth2.kakao.service.RefreshTokenService;
import com.sossbar.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
@Tag(name = "Login API", description = "로그인 관련 API")
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "카카오 로그인", description = "카카오 로그인 콜백 api입니다.")
    @GetMapping("/kakao")
    public ResponseEntity<ApiResTemplate<LoginInfoResDto>> kakaoCallback(@RequestParam String code) {
        User user = kakaoLoginService.processLogin(code);
        return refreshTokenService.loginSuccess(user);
    }

    @Operation(summary = "리프레시 토큰으로 액세스 토큰 재발급", description = "HttpOnly 쿠키에 있는 refreshToken으로 accessToken을 재발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<ApiResTemplate<LoginInfoResDto>> getAccessTokenByRefreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "리프레시 토큰이 없습니다.");
        }
        return refreshTokenService.getAccessTokenByRefreshToken(refreshToken);
    }

    @PostMapping("/test-account")
    public ResponseEntity<ApiResTemplate<LoginInfoResDto>> testLogin() {
        return kakaoLoginService.testLogin();
    }

    @Operation(summary = "쿠키 토큰 삭제", description = "쿠키에 들어있는 토큰 삭제로 로그아웃시 사용 가능")
    @PostMapping("/logout")
    public ResponseEntity<ApiResTemplate<String>> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        return refreshTokenService.logout(refreshToken);
    }
}
