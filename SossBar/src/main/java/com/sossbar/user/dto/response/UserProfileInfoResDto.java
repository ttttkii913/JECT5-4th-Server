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
        List<UserPosition> defaultPositions,
        List<UserLinkResDto> links
) {
    public static UserProfileInfoResDto from(User user) {
        return UserProfileInfoResDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .defaultPositions(user.getDefaultPositions())
                .links(user.getLinks().stream().map(UserLinkResDto::from).toList())
                .build();
    }
}
