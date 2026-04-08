package com.sossbar.user.entity;

import com.sossbar.global.common.template.BaseTimeEntity;
import com.sossbar.user.dto.request.UserInfoUpdateReqDto;
import com.sossbar.user.dto.request.UserOnboardingReqDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String username;
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;
    private String bio;
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Builder
    public User(String username, String nickname, String email, String bio, String profileImageUrl, UserType userType) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.userType = userType;
    }

    public void onboarding(UserOnboardingReqDto userInfoSaveReqDto, String profileImageUrl) {
        this.username = userInfoSaveReqDto.username();
        this.bio = userInfoSaveReqDto.bio();
        this.profileImageUrl = profileImageUrl;
    }

    public void updateUserInfo(UserInfoUpdateReqDto userInfoUpdateReqDto, String profileImageUrl) {
        this.nickname = userInfoUpdateReqDto.nickname();
        this.bio = userInfoUpdateReqDto.bio();
        this.profileImageUrl = profileImageUrl;
    }
}
