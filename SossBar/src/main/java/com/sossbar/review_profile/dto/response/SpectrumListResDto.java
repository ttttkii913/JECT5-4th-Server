package com.sossbar.review_profile.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record SpectrumListResDto(
        Long totalCount,
        List<SpectrumInfoResDto> spectrumInfoResDtos,
        LocalDateTime modifiedAt
) {
    public static SpectrumListResDto from(Long totalCount, List<SpectrumInfoResDto> spectrumInfoResDtos, LocalDateTime modifiedAt) {
        return new SpectrumListResDto(totalCount, spectrumInfoResDtos, modifiedAt);
    }
}
