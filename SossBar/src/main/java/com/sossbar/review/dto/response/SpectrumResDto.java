package com.sossbar.review.dto.response;

import lombok.Builder;

@Builder
public record SpectrumResDto (
        Long spectrumAxisId,
        Integer spectrumStrength
) {
}
