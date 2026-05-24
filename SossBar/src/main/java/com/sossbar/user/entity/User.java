package com.sossbar.user.entity;

import com.sossbar.global.common.template.BaseTimeEntity;
import com.sossbar.user.dto.request.UserInfoUpdateReqDto;
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

    @Column(unique = true, nullable = false)
    private String email;
    private String bio;
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private UserType userType;
    private String refreshToken;

    @Column(name = "user_info_delete")
    private boolean isDeleted = false;

    @Builder
    public User(String username, String email, String bio, String profileImageUrl, UserType userType, String refreshToken) {
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.userType = userType;
        this.refreshToken = refreshToken;
    }

    public void updateUserInfo(UserInfoUpdateReqDto userInfoUpdateReqDto, String profileImageUrl) {
        if (userInfoUpdateReqDto.username() != null) {
            this.username = userInfoUpdateReqDto.username();
        }

        if (userInfoUpdateReqDto.bio() != null) {
            this.bio = userInfoUpdateReqDto.bio();
        }

        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void saveRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void deleteUser() {
        this.username = "탈퇴한 회원";
        this.email = "deleted_user" + this.id;
        this.bio = null;
        this.profileImageUrl = null;
        this.userType = null;
        this.refreshToken = null;
        this.isDeleted = true;
    }
}
