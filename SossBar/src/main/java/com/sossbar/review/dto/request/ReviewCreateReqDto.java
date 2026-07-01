package com.sossbar.review.dto.request;

import jakarta.validation.Valid;
import lombok.Getter;

import java.util.List;

// 통합 DTO
@Getter
public class ReviewCreateReqDto {
    @Valid
    private ReviewReqDto reviewReqDto;
    @Valid
    private List<SpectrumReqDto> spectrumReqDtos;
}
