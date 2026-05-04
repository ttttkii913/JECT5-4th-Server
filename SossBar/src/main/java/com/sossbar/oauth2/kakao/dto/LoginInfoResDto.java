    package com.sossbar.oauth2.kakao.dto;

    public record LoginInfoResDto(
            String accessToken,
            Long userId
    ) {
    }
