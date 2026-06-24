package com.sossbar.user.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.global.config.S3Service;
import com.sossbar.user.dto.request.UserInfoUpdateReqDto;
import com.sossbar.user.dto.request.UserLinkReqDto;
import com.sossbar.user.dto.response.UserInfoResDto;
import com.sossbar.user.dto.response.UserProfileInfoResDto;
import com.sossbar.user.entity.User;
import com.sossbar.user.entity.UserLink;
import com.sossbar.user.entity.UserPosition;
import com.sossbar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

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
            // 새 이미지 업로드
            String uploadedImageUrl = s3Service.uploadFile(profileImage, "sossbar/profile");
            // 업로드 성공 후 기존 이미지가 존재한다면 S3에서 삭제
            if (newProfileImageUrl != null && !newProfileImageUrl.isBlank()) {
                s3Service.deleteFile(newProfileImageUrl);
            }
            newProfileImageUrl = uploadedImageUrl;
        }

        // 직군 ETC 선택시 직접 직군 입력
        if (userInfoUpdateReqDto.defaultPosition() == UserPosition.ETC
                && (userInfoUpdateReqDto.defaultDetailPosition() == null
                || userInfoUpdateReqDto.defaultDetailPosition().isBlank())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "직군을 입력해 주세요.");
        }

        if (userInfoUpdateReqDto.links() != null && userInfoUpdateReqDto.links().size() > 3) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "Link는 최대 3개까지만 추가할 수 있습니다.");
        }

        // 프로필 수정에서도 link 수정
        List<UserLink> newLinks = null;
        if (userInfoUpdateReqDto.links() != null) {
            newLinks = userInfoUpdateReqDto.links().stream()
                    .map(linkDto -> UserLinkReqDto.createLink(user, linkDto))
                    .toList();
        }

        user.updateUserInfo(userInfoUpdateReqDto, newProfileImageUrl, newLinks);
        userRepository.saveAndFlush(user);

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
