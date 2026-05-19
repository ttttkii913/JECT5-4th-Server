package com.sossbar.review_profile.controller;

import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.global.common.template.SwaggerApiResTemplate;
import com.sossbar.review_profile.dto.response.TagListResDto;
import com.sossbar.review_profile.service.ReviewProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Review Profile API", description = "내 프로필 후기 관련 API")
@RestController
@RequiredArgsConstructor
@SwaggerApiResTemplate
@RequestMapping("/api/v1/reviews")
public class ReviewProfileController {

    private final ReviewProfileService reviewProfileService;

    @Operation(summary = "받은 태그 전체 조회", description = "사용자는 로그인 후 프로필 페이지에서 받은 태그 정보를 전체 조회합니다."
            + "동료가 뽑은 TOP3 + 전체 태그(많이 받은 순)")
    @GetMapping("/tags/{userId}")
    public ApiResTemplate<TagListResDto> getAllTags(@PathVariable("userId") Long userId) {
        TagListResDto tagListResDto = reviewProfileService.getAllTags(userId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, tagListResDto);
    }

    @Operation(summary = "받은 태그 프로젝트별 조회", description = "사용자는 로그인 후 프로필 페이지에서 받은 태그 정보를 프로젝트별로 조회합니다."
            + "동료가 뽑은 TOP3 + 전체 태그(많이 받은 순), 전체 태그가 기준이 아닌 프로젝트별 태그")
    @GetMapping("/tags/{userId}/{projectId}")
    public ApiResTemplate<TagListResDto> getTagsByProject(@PathVariable("userId") Long userId,
                                                          @PathVariable("projectId") Long projectId) {
        TagListResDto tagListResDto = reviewProfileService.getTagsByProject(userId, projectId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, tagListResDto);
    }
}
