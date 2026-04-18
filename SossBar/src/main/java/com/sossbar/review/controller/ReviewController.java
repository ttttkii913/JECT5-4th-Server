package com.sossbar.review.controller;

import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.review.dto.request.ReviewCreateReqDto;
import com.sossbar.review.dto.response.ReviewCreateResDto;
import com.sossbar.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "Review API", description = "후기 관련 API")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "후기 작성", description = "사용자는 로그인 후 다른 사용자에 대한 후기를 남길 수 있습니다.")
    @PostMapping("/api/v1/reviews")
    public ApiResTemplate<ReviewCreateResDto> createReview(
            @RequestBody @Valid ReviewCreateReqDto reviewCreateReqDto,
            Principal principal
    ) {
        ReviewCreateResDto reviewCreateResDto = reviewService.createReview(principal, reviewCreateReqDto);

        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, reviewCreateResDto);
    }
}
