package com.sossbar.user.dto.request;

import com.sossbar.user.entity.UserPosition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserInfoUpdateReqDto(
        @NotBlank(message = "실명만 입력해 주세요.")
        @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해 주세요.")
        String username,

        @Size(max = 100, message = "한 줄 소개는 100자 이하로 입력해 주세요.")
        String bio,

        // 직군
        @NotNull
        @Size(min = 1, max = 2, message = "직군은 최대 2개만 선택할 수 있습니다.")
        List<UserPosition> defaultPositions,

        // 하드 스킬 링크
        @Size(max = 3, message = "링크는 최대 3개까지 등록 가능합니다.")
        List<UserLinkReqDto> links,

        // 필수 동의 항목
        boolean requiredAgree,

        // 마케팅 동의 항목 => 알림 동의 항목으로 기본 true
        boolean marketingAgree
) {
}
