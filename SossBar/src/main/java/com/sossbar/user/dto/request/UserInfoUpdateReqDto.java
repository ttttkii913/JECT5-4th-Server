package com.sossbar.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserInfoUpdateReqDto(
        @NotBlank(message = "이름은 필수입니다.")
        @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해 주세요.")
        String username,

        @NotBlank
        @Size(max = 100, message = "한 줄 소개는 100자 이하로 입력해 주세요.")
        String bio
) {
}
