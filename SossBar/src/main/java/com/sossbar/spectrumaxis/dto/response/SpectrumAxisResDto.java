package com.sossbar.spectrumaxis.dto.response;

public record SpectrumAxisResDto(
        Long spectrumAxisId,
        String spectrumAxisName,
        String leftLabel,
        String rightLabel
) {
}
