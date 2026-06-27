package com.sossbar.user.dto.response;

import com.sossbar.user.entity.User;
import com.sossbar.user.entity.UserPosition;
import com.sossbar.user.entity.UserType;
import lombok.Builder;

import java.util.List;

@Builder
public record UserInfoResDto(
        Long userId,
        String username,
        String email,
        String bio,
        String profileImageUrl,
        UserType userType,
        UserPosition defaultPosition,
        String defaultDetailPosition,
        List<UserLinkResDto> links,
        boolean marketingAgree

) {
    public static UserInfoResDto from(User user) {
        return UserInfoResDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .userType(user.getUserType())
                .defaultPosition(user.getDefaultPosition())
                .defaultDetailPosition(user.getDefaultDetailPosition())
                .links(user.getLinks().stream().map(UserLinkResDto::from).toList())
                .marketingAgree(user.isMarketingAgree())
                .build();
    }
}
