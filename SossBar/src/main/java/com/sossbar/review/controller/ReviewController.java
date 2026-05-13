package com.sossbar.review.controller;

import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.review.dto.request.ReviewCreateReqDto;
import com.sossbar.review.dto.response.CommonReviewResDto;
import com.sossbar.review.dto.response.ReviewCreateResDto;
import com.sossbar.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

    @Operation(summary = "전체 후기 조회", description = "특정 사용자에 대한 전체 후기를 조회할 수 있습니다.")
    @GetMapping("/api/v1/users/{userId}/reviews")
    public ApiResTemplate<List<CommonReviewResDto>> getReviews(
            Principal principal,
            @PathVariable Long userId
    ) {
        List<CommonReviewResDto> reviews = reviewService.getReviews(principal, userId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, reviews);
    }

}
