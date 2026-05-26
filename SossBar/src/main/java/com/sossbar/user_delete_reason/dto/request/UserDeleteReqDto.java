package com.sossbar.user_delete_reason.dto.request;

import com.sossbar.user_delete_reason.entity.UserDeleteReasonEnum;
import jakarta.validation.constraints.NotNull;

public record UserDeleteReqDto(
        @NotNull
        UserDeleteReasonEnum userDeleteReasonEnum,
        String detail
) {
}
