package com.sossbar.user.dto.response;

import com.sossbar.user.entity.User;
import lombok.Builder;

@Builder
public record UserProfileInfoResDto(
        Long userId,
        String username,
        String bio,
        String profileImageUrl
) {
    public static UserProfileInfoResDto from(User user) {
        return UserProfileInfoResDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
