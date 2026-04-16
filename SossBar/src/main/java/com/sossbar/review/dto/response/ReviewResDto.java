package com.sossbar.review.dto.response;

import com.sossbar.spectrumaxis.dto.response.SpectrumAxisResDto;
import com.sossbar.tag.dto.response.TagResDto;

import java.util.List;

public record ReviewResDto (
        String positiveFeedback,
        String negativeFeedback,
        Long revieweeId,
        Long projectId,
        List<TagResDto> tags,
        List<SpectrumAxisResDto> spectrumAxes
) {
}
