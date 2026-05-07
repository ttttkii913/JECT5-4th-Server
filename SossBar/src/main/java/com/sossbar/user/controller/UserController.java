package com.sossbar.user.controller;

import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.global.common.template.SwaggerApiResTemplate;
import com.sossbar.user.dto.request.UserNameUpdateReqDto;
import com.sossbar.user.dto.request.UserOnboardingReqDto;
import com.sossbar.user.dto.response.UserInfoResDto;
import com.sossbar.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@SwaggerApiResTemplate
@Tag(name = "User API", description = "사용자 관련 API - MyPage")
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 정보 추가 입력", description = "카카오 로그인을 완료한 사용자가 최초 1회 추가 정보(이름, 한 줄 소개, 프로필 이미지)를 입력합니다." +
            "<br> 이미지를 빈 값으로 보낼 시 url은 null로 저장됨")
    @PostMapping(value = "/onboarding", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResTemplate<UserInfoResDto> saveUserInfo(Principal principal,
                                                       @Valid @RequestPart("onboarding") UserOnboardingReqDto userOnboardingReqDto,
                                                       @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        UserInfoResDto userInfoResDto = userService.onboarding(principal, userOnboardingReqDto, profileImage);
        return ApiResTemplate.successResponse(SuccessCode.SUCCESS, userInfoResDto);
    }

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자가 자신의 계정 정보를 조회합니다.")
    @GetMapping("/profile")
    public ApiResTemplate<UserInfoResDto> getUserInfo(Principal principal) {
        UserInfoResDto userInfoResDto = userService.getUserInfo(principal);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, userInfoResDto);
    }

    @Operation(summary = "내 닉네임 수정", description = "로그인한 사용자가 자신의 닉네임을 수정합니다.")
    @PatchMapping("/nickname")
    public ApiResTemplate<UserInfoResDto> updateUserNickname(Principal principal,
                                                             @Valid @RequestBody UserNameUpdateReqDto userNameUpdateReqDto) {
        UserInfoResDto userInfoResDto = userService.updateUserNickname(principal, userNameUpdateReqDto);

        return ApiResTemplate.successResponse(SuccessCode.SUCCESS, userInfoResDto);
    }
}
