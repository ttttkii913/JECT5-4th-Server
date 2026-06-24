package com.sossbar.user.dto.response;

import com.sossbar.user.entity.UserLink;
import com.sossbar.user.entity.UserLinkType;
import lombok.Builder;

@Builder
public record UserLinkResDto(
        Long linkId,
        UserLinkType userLinkType,
        String userLink
) {
    public static UserLinkResDto from(UserLink link) {
        return UserLinkResDto.builder()
                .linkId(link.getId())
                .userLinkType(link.getUserLinkType())
                .userLink(link.getUserLink())
                .build();
    }
}
