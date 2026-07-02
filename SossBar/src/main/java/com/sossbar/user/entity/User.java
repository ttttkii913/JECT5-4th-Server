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
import java.util.UUID;

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
    private UserPosition defaultPosition1;
    @Enumerated(EnumType.STRING)
    private UserPosition defaultPosition2;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserLink> links = new ArrayList<>();

    private String refreshToken;

    @Column(name = "user_info_delete")
    private boolean isDeleted = false;

    private boolean marketingAgree = true;

    @Column(unique = true)
    private String userLink;

    @Builder
    public User(String username, String email, String bio, String profileImageUrl, UserType userType, UserPosition defaultPosition1, UserPosition defaultPosition2, String refreshToken, boolean marketingAgree) {
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.userType = userType;
        this.defaultPosition1 = defaultPosition1;
        this.defaultPosition2 = defaultPosition2;
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

        if (userInfoUpdateReqDto.defaultPositions() != null) {
            updateDefaultPositions(userInfoUpdateReqDto.defaultPositions());
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

    public List<UserPosition> getDefaultPositions() {
        List<UserPosition> positions = new ArrayList<>();

        if (defaultPosition1 != null) {
            positions.add(defaultPosition1);
        }

        if (defaultPosition2 != null) {
            positions.add(defaultPosition2);
        }

        return positions;
    }

    public void updateMarketingAgree(boolean marketingAgree) {
        this.marketingAgree = marketingAgree;
    }

    public void updateDefaultPositions(List<UserPosition> positions) {
        this.defaultPosition1 = positions.get(0);
        this.defaultPosition2 = positions.size() > 1 ? positions.get(1) : null;
    }

    // 사용자만의 고유 uuid 생성
    @PrePersist
    public void generateUserLink() {
        if (this.userLink == null) {
            this.userLink = UUID.randomUUID().toString();
        }
    }
}
