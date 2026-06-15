package com.sossbar.review.dto.request;


import lombok.Getter;


// 스펙트럼 항목 하나 당 DTO
@Getter
public class SpectrumReqDto {
        Long spectrumAxisId;
        Integer spectrumStrength;
}
