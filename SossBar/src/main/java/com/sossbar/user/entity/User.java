package com.sossbar.user.entity;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.global.common.template.BaseTimeEntity;
import com.sossbar.user.dto.request.UserInfoUpdateReqDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    private UserPosition defaultPosition;
    private String defaultDetailPosition;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserLink> links = new ArrayList<>();

    private String refreshToken;

    @Column(name = "user_info_delete")
    private boolean isDeleted = false;

    private boolean marketingAgree = false;

    @Builder
    public User(String username, String email, String bio, String profileImageUrl, UserType userType, UserPosition defaultPosition, String defaultDetailPosition, String refreshToken, boolean marketingAgree) {
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.userType = userType;
        this.defaultPosition = defaultPosition;
        this.defaultDetailPosition = defaultDetailPosition;
        this.refreshToken = refreshToken;
        this.marketingAgree = marketingAgree;
    }

    public void updateUserInfo(UserInfoUpdateReqDto userInfoUpdateReqDto, String profileImageUrl, List<UserLink> newLinks) {
        if (userInfoUpdateReqDto.username() != null) {
            this.username = userInfoUpdateReqDto.username();
        }

        if (userInfoUpdateReqDto.bio() != null) {
            this.bio = userInfoUpdateReqDto.bio();
        }

        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }

        if (userInfoUpdateReqDto.defaultPosition() != null) {
            this.defaultPosition = userInfoUpdateReqDto.defaultPosition();
            this.defaultDetailPosition =
                    userInfoUpdateReqDto.defaultPosition() == UserPosition.ETC
                            ? userInfoUpdateReqDto.defaultDetailPosition()
                            : null;
        }

        if (newLinks != null) {
            this.links.clear();
            this.links.addAll(newLinks);
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

    public void updateMarketingAgree(boolean marketingAgree) {
        this.marketingAgree = marketingAgree;
    }
}
