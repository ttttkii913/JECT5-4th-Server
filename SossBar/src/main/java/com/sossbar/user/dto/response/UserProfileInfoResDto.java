package com.sossbar.user.dto.response;

import com.sossbar.user.entity.User;
import com.sossbar.user.entity.UserPosition;
import lombok.Builder;

import java.util.List;

@Builder
public record UserProfileInfoResDto(
        Long userId,
        String username,
        String bio,
        String profileImageUrl,
        UserPosition defaultPosition,
        String defaultDetailPosition,
        List<UserLinkResDto> links
) {
    public static UserProfileInfoResDto from(User user) {
        return UserProfileInfoResDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .defaultPosition(user.getDefaultPosition())
                .defaultDetailPosition(user.getDefaultDetailPosition())
                .links(user.getLinks().stream().map(UserLinkResDto::from).toList())
                .build();
    }
}
