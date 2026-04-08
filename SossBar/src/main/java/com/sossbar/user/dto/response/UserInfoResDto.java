package com.sossbar.user.dto.response;

import com.sossbar.user.entity.User;
import com.sossbar.user.entity.UserType;
import lombok.Builder;

@Builder
public record UserInfoResDto(
        Long userId,
        String username,
        String nickname,
        String email,
        String bio,
        String profileImageUrl,
        UserType userType
) {
    public static UserInfoResDto from(User user) {
        return UserInfoResDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .userType(user.getUserType())
                .build();
    }
}
