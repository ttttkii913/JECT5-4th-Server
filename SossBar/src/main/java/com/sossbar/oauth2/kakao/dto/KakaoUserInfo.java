package com.sossbar.oauth2.kakao.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class KakaoUserInfo {
    private Long id;
    @SerializedName("kakao_account")
    private KakaoAccount kakaoAccount;
    private Properties properties;

    @Data
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Data
        public static class Profile {
            @SerializedName("profile_image_url")
            private String profileImageUrl;
        }
    }

    @Data
    public static class Properties {
        private String nickname;
    }
}
