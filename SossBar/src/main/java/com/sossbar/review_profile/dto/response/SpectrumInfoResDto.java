package com.sossbar.review_profile.dto.response;

public record SpectrumInfoResDto(
        Long spectrumAxisId,
        String axisName,

        // 평균 점수 분포
        Double averageStrength,

        // 왼쪽 성향 인원 수(강도 1~3)
        Long leftStrengthCount,

        // 오른쪽 성향 인원 수(강도 4~6)
        Long rightStrengthCount
) {
    // 2.85 -> 3, 2.35 -> 2
    public Integer getAverageStrength() {
        return averageStrength != null
                ? (int) Math.round(averageStrength)
                : null;
    }
}
