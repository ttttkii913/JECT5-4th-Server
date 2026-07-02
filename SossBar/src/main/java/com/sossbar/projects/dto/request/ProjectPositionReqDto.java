package com.sossbar.projects.dto.request;

import com.sossbar.user.entity.UserPosition;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProjectPositionReqDto(
        @NotNull
        @Size(min = 1, max = 2)
        List<UserPosition> projectPositions
) {
}
