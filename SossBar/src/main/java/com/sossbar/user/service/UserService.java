package com.sossbar.user.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.global.config.S3Service;
import com.sossbar.user.dto.request.UserOnboardingReqDto;
import com.sossbar.user.dto.request.UserInfoUpdateReqDto;
import com.sossbar.user.dto.response.UserInfoResDto;
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
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    // 온보딩 - 사용자 추가 정보 입력 (이름, 한 줄 소개, 프로필 이미지)
    @Transactional
    public UserInfoResDto onboarding(Principal principal, UserOnboardingReqDto userOnboardingReqDto, MultipartFile profileImage) {
        Long id = Long.parseLong(principal.getName());
        User user = getUserById(id);

        // 이미 온보딩 완료된 경우 예외
        if (user.getUsername() != null) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "이미 온보딩이 완료된 사용자입니다."
            );
        }

        // 아무것도 보내지 않으면 초기 이미지는 null로
        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = s3Service.uploadFile(profileImage, "sossbar/profile");
        }

        user.onboarding(userOnboardingReqDto, profileImageUrl);

        return UserInfoResDto.from(user);
    }

    // 마이페이지 - 내 프로필 조회
    public UserInfoResDto getUserInfo(Principal principal) {
        Long id = Long.parseLong(principal.getName());
        User user = getUserById(id);

        return UserInfoResDto.from(user);
    }

    // 마이페이지 - 내 프로필 수정
    @Transactional
    public UserInfoResDto updateUserInfo(Principal principal, UserInfoUpdateReqDto userInfoUpdateReqDto, MultipartFile profileImage) {
        Long id = Long.parseLong(principal.getName());
        User user = getUserById(id);

        String newNickname = userInfoUpdateReqDto.nickname();

        // nickname 중복 체크 - 자신 제외
        if (newNickname != null &&
                userRepository.existsByNicknameAndIdNot(newNickname, user.getId())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "이미 사용 중인 닉네임입니다."
            );
        }

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

        return UserInfoResDto.from(user);
    }

    // entity 찾는 공통 메소드
    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION
                        , ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));
    }
}
