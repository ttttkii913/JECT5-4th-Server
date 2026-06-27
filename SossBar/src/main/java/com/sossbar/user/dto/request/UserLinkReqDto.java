package com.sossbar.user.dto.request;

import com.sossbar.user.entity.User;
import com.sossbar.user.entity.UserLink;
import com.sossbar.user.entity.UserLinkType;

public record UserLinkReqDto(
        UserLinkType userLinkType,
        String userLink
) {
    public static UserLink createLink(User user, UserLinkReqDto userLinkReqDto) {
        return UserLink.builder()
                .user(user)
                .userLinkType(userLinkReqDto.userLinkType())
                .userLink(userLinkReqDto.userLink())
                .build();
    }
}
