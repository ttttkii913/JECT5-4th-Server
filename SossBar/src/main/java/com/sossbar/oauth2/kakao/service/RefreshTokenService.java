package com.sossbar.oauth2.kakao.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.oauth2.jwt.JwtTokenProvider;
import com.sossbar.oauth2.kakao.dto.LoginInfoResDto;
import com.sossbar.user.entity.User;
import com.sossbar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.sameSite}")
    private String cookieSameSite;

    // refreshToken 저장
    public ResponseEntity<ApiResTemplate<LoginInfoResDto>> loginSuccess(User user) {

        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // DB 저장
        user.saveRefreshToken(refreshToken);
        userRepository.save(user);

        // HttpOnly 쿠키로 accessToken 전달
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite(cookieSameSite)
                .build();

        // HttpOnly 쿠키로 refreshToken 전달
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Long.parseLong(jwtTokenProvider.getRefreshTokenExpireTime()) / 1000)
                .sameSite(cookieSameSite)
                .build();

        System.out.println("cookieSecure = " + cookieSecure);
        System.out.println("cookieSameSite = " + cookieSameSite);

        LoginInfoResDto loginInfoResDto = new LoginInfoResDto(user.getId());

        // 헤더에 쿠키 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResTemplate.successResponse(SuccessCode.SUCCESS, loginInfoResDto));
    }

    // accessToken 재발급
    public ResponseEntity<ApiResTemplate<LoginInfoResDto>> getAccessTokenByRefreshToken(String refreshToken) {

        // 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.JWT_EXPIRED,
                    "리프레시 토큰이 만료되었습니다.");
        }

        // userId 추출 및 엔티티 조회
        Long userId = jwtTokenProvider.getUserId(refreshToken);

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage()
                ));

        // DB에 저장된 refreshToken과 비교
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN,
                    ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
        }

        // 새로운 accessToken 발급
        String newAccessToken = jwtTokenProvider.generateToken(user);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite(cookieSameSite)
                .build();

        LoginInfoResDto loginInfoResDto = new LoginInfoResDto(user.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(ApiResTemplate.successResponse(SuccessCode.SUCCESS, loginInfoResDto));

    }

    // 쿠키 삭제
    public ResponseEntity<ApiResTemplate<String>> logout(String refreshToken) {

        // DB에 저장된 refreshToken 제거
        if (refreshToken != null) {
            try {
                Long userId = jwtTokenProvider.getUserId(refreshToken);

                userRepository.findById(userId)
                        .ifPresent(user -> {
                            user.saveRefreshToken(null);
                            userRepository.save(user);
                        });

            } catch (Exception e) {
                log.warn("로그아웃 중 refreshToken 처리 실패");
            }
        }

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResTemplate.successResponse(
                        SuccessCode.SUCCESS,
                        "로그아웃 완료"
                ));
    }
}
