package com.sossbar.user.controller;

import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.global.common.template.SwaggerApiResTemplate;
import com.sossbar.user.dto.request.UserInfoUpdateReqDto;
import com.sossbar.user.dto.response.UserInfoResDto;
import com.sossbar.user.dto.response.UserProfileInfoResDto;
import com.sossbar.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@SwaggerApiResTemplate
@Tag(name = "User Profile API", description = "사용자 프로필 관련 API")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "사용자 프로필 정보 조회", description = "로그인한 사용자가 userId로 사용자 프로필 정보(실명, 프로필 사진, 한 줄 소개)를 조회합니다.")
    @GetMapping("/profile/{userId}")
    public ApiResTemplate<UserProfileInfoResDto> getUserProfile(@PathVariable("userId") Long userId) {
        UserProfileInfoResDto userProfileInfoResDto = userProfileService.getUserProfile(userId);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, userProfileInfoResDto);
    }

    @Operation(summary = "내 프로필 수정", description = "로그인한 사용자가 자신의 프로필 정보를 수정합니다. (실명, 한 줄 소개, 프로필 이미지)" +
            "<br> 이미지를 빈 값으로 보낼 시 기존 프로필 이미지 유지")
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResTemplate<UserInfoResDto> updateUserInfo(Principal principal,
                                                         @Valid @RequestPart("info") UserInfoUpdateReqDto userInfoUpdateReqDto,
                                                         @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        UserInfoResDto userInfoResDto = userProfileService.updateUserInfo(principal, userInfoUpdateReqDto, profileImage);
        return ApiResTemplate.successResponse(SuccessCode.UPDATE_SUCCESS, userInfoResDto);
    }
}
