package com.sossbar.oauth2.kakao.service;

import com.google.gson.Gson;
import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.oauth2.jwt.JwtTokenProvider;
import com.sossbar.oauth2.kakao.dto.KakaoToken;
import com.sossbar.oauth2.kakao.dto.KakaoUserInfo;
import com.sossbar.user.entity.User;
import com.sossbar.user.entity.UserType;
import com.sossbar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    @Value("${kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    // 카카오에서 전달해 준 인가 코드로 액세스 토큰 리다이렉트
    public String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_TOKEN_URL, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BusinessException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED
                    , ErrorCode.KAKAO_TOKEN_REQUEST_FAILED.getMessage());
        }

        try {
            Gson gson = new Gson();
            KakaoToken tokenResponse = gson.fromJson(response.getBody(), KakaoToken.class);
            return tokenResponse.getAccessToken();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.KAKAO_LOGIN_FAILED
                    , ErrorCode.KAKAO_LOGIN_FAILED.getMessage());
        }
    }

    // 카카오 사용자 정보 조회
    public KakaoUserInfo getUserInfoFromKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new BusinessException(ErrorCode.KAKAO_USER_INFO_FAILED,
                    ErrorCode.KAKAO_USER_INFO_FAILED.getMessage());
        }

        try {
            Gson gson = new Gson();
            return gson.fromJson(responseEntity.getBody(), KakaoUserInfo.class);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.KAKAO_LOGIN_FAILED,
                    ErrorCode.KAKAO_LOGIN_FAILED.getMessage());
        }
    }

    // 전체 프로세스
    public KakaoToken processLogin(String code) {
        String accessToken = getKakaoAccessToken(code);
        KakaoUserInfo kakaoUserInfo = getUserInfoFromKakao(accessToken);

        if (kakaoUserInfo.getKakaoAccount() == null ||
                kakaoUserInfo.getKakaoAccount().getEmail() == null) {
            throw new BusinessException(ErrorCode.KAKAO_EMAIL_NOT_FOUND,
                    ErrorCode.KAKAO_EMAIL_NOT_FOUND.getMessage());
        }

        User user = userRepository.findByEmail(
                kakaoUserInfo.getKakaoAccount().getEmail()
        ).orElseGet(() ->
                userRepository.save(User.builder()
                        .email(kakaoUserInfo.getKakaoAccount().getEmail())
                        .nickname(kakaoUserInfo.getProperties().getNickname())
                        .profileImageUrl(
                                kakaoUserInfo.getKakaoAccount()
                                        .getProfile()
                                        .getProfileImageUrl()
                        )
                        .userType(UserType.KAKAO)
                        .build())
        );

        String jwt = jwtTokenProvider.generateToken(user);

        return new KakaoToken(jwt);
    }
}
