package com.sossbar.review_profile.dto.response;

public record SpectrumInfoResDto(
        Long spectrumAxisId,
        String axisName,

        // 평균 점수 분포
        Integer averageStrength,

        // 왼쪽 성향 인원 수(강도 1~3)
        Long leftStrengthCount,

        // 오른쪽 성향 인원 수(강도 4~6)
        Long rightStrengthCount
) {
}
