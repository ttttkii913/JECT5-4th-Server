package com.sossbar.review.dto.request;

import lombok.Getter;

import java.util.List;

// 통합 DTO
@Getter
public class ReviewCreateReqDto {
    private ReviewReqDto reviewReqDto;
    private List<SpectrumReqDto> spectrumReqDtos;
}
