package com.sossbar.user.controller;

import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.global.common.template.SwaggerApiResTemplate;
import com.sossbar.user.dto.request.UserInfoUpdateReqDto;
import com.sossbar.user.dto.response.UserInfoResDto;
import com.sossbar.user.service.UserService;
import com.sossbar.user_delete_reason.dto.request.UserDeleteReqDto;
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

    @Operation(summary = "사용자 정보 추가 입력", description = "카카오 로그인을 완료한 사용자가 최초 1회 추가 정보(실명, 한 줄 소개, 프로필 이미지, 직군, url, 동의 항목 여부)를 입력합니다." +
            "<br> 이미지를 빈 값으로 보낼 시 url은 null로 저장됨")
    @PostMapping(value = "/onboarding", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResTemplate<UserInfoResDto> saveUserInfo(Principal principal,
                                                       @Valid @RequestPart("onboarding") UserInfoUpdateReqDto userInfoUpdateReqDto,
                                                       @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        UserInfoResDto userInfoResDto = userService.onboarding(principal, userInfoUpdateReqDto, profileImage);
        return ApiResTemplate.successResponse(SuccessCode.SUCCESS, userInfoResDto);
    }

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자가 자신의 계정 정보를 조회합니다. (본인만 조회 가능, 이메일 포함)")
    @GetMapping("/profile")
    public ApiResTemplate<UserInfoResDto> getUserInfo(Principal principal) {
        UserInfoResDto userInfoResDto = userService.getUserInfo(principal);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, userInfoResDto);
    }

    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자가 회원 탈퇴를 통해 자신의 정보를 삭제합니다." +
            "<br> 탈퇴 사유를 선택(enum 값 + detail: null) or 기타(ETC) 선택시 탈퇴 사유(detail)을 입력해야 합니다." +
            "<br> 해당 사용자가 작성한 후기는 삭제되지 않으며, userId로 판별하는 api에서 탈퇴한 사용자로 조회시 notfound 예외")
    @DeleteMapping
    public ApiResTemplate<String> deleteUser(Principal principal, @Valid @RequestBody UserDeleteReqDto userDeleteReqDto) {
        userService.deleteUser(principal, userDeleteReqDto);
        return ApiResTemplate.successWithNoContent(SuccessCode.DELETE_SUCCESS);
    }
}
