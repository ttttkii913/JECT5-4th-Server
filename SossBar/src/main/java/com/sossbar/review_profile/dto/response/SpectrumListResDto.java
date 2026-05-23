package com.sossbar.review_profile.dto.response;

import java.util.List;

public record SpectrumListResDto(
        Long totalCount,
        List<SpectrumInfoResDto> spectrumInfoResDtos
) {
    public static SpectrumListResDto from(Long totalCount, List<SpectrumInfoResDto> spectrumInfoResDtos) {
        return new SpectrumListResDto(totalCount, spectrumInfoResDtos);
    }
}
