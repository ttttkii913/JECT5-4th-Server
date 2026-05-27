package com.sossbar.user.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.global.config.S3Service;
import com.sossbar.user.dto.request.UserInfoUpdateReqDto;
import com.sossbar.user.dto.response.UserInfoResDto;
import com.sossbar.user.dto.response.UserProfileInfoResDto;
import com.sossbar.user.entity.User;
import com.sossbar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    // 프로필 페이지 - 내 프로필 수정(실명, 한 줄 소개, 프로필 이미지)
    @Transactional
    public UserInfoResDto updateUserInfo(Principal principal, UserInfoUpdateReqDto userInfoUpdateReqDto, MultipartFile profileImage) {
        Long id = Long.parseLong(principal.getName());
        User user = getUserById(id);

        // 프로필 이미지 수정 처리 (기존 이미지 삭제 후 새 이미지 업로드)
        String newProfileImageUrl = user.getProfileImageUrl();

        if (profileImage != null && !profileImage.isEmpty()) {
            // 기존 이미지가 존재한다면 S3에서 삭제
            if (newProfileImageUrl != null && !newProfileImageUrl.isBlank()) {
                s3Service.deleteFile(newProfileImageUrl);
            }
            // 새 이미지 업로드
            newProfileImageUrl = s3Service.uploadFile(profileImage, "sossbar/profile");
        }

        user.updateUserInfo(userInfoUpdateReqDto, newProfileImageUrl);
        user.updateMarketingAgree(userInfoUpdateReqDto.marketingAgree());

        return UserInfoResDto.from(user);
    }

    // 프로필 페이지 - 사용자 프로필 조회, userId로 구분
    public UserProfileInfoResDto getUserProfile(Long userId) {
        User user = getUserById(userId);

        return UserProfileInfoResDto.from(user);
    }

    // entity 찾는 공통 메소드
    private User getUserById(Long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION
                        , ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));
    }
}
