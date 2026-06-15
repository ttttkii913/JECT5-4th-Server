package com.sossbar.oauth2.kakao.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class KakaoToken {
    @SerializedName("access_token")
    private String accessToken;
}
