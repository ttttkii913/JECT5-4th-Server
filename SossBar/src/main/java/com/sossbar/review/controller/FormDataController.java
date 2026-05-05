package com.sossbar.review.controller;

import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.review.dto.response.FormDataResDto;
import com.sossbar.review.service.FormDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "FormData API", description = "폼 데이터 반환 API")
@RestController
@RequiredArgsConstructor
public class FormDataController {

    private final FormDataService formDataService;

    @Operation(summary = "폼 데이터 반환", description = "리뷰와 스펙트럼 항목을 작성하기 위한 폼 데이터를 반환하는 API입니다.")
    @GetMapping ("/api/v1/form-data")
    public ApiResTemplate<FormDataResDto> getFormData() {
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, formDataService.getFormData());
    }
}
