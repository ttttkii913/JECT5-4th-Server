package com.sossbar.user.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.global.config.S3Service;
import com.sossbar.projects.entity.Project;
import com.sossbar.projects.entity.ProjectMember;
import com.sossbar.projects.enums.MemberStatus;
import com.sossbar.projects.repository.ProjectMemberRepository;
import com.sossbar.user.dto.request.UserInfoUpdateReqDto;
import com.sossbar.user.dto.request.UserLinkReqDto;
import com.sossbar.user.dto.response.UserInfoResDto;
import com.sossbar.user.entity.User;
import com.sossbar.user.entity.UserLink;
import com.sossbar.user.entity.UserPosition;
import com.sossbar.user.repository.UserRepository;
import com.sossbar.user_delete_reason.dto.request.UserDeleteReqDto;
import com.sossbar.user_delete_reason.entity.UserDeleteReasonEnum;
import com.sossbar.user_delete_reason.entity.UserDeleteReason;
import com.sossbar.user_delete_reason.repository.UserDeleteReasonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserDeleteReasonRepository userDeleteReasonLogRepository;

    // 온보딩 - 사용자 추가 정보 입력 (실명, 한 줄 소개, 프로필 이미지, 직군, 동의 항목 여부)
    @Transactional
    public UserInfoResDto onboarding(Principal principal, UserInfoUpdateReqDto userInfoUpdateReqDto, MultipartFile profileImage) {
        Long id = Long.parseLong(principal.getName());
        User user = getUserById(id);

        // 온보딩 마케팅 동의 false 불가 예외
        if (!userInfoUpdateReqDto.marketingAgree()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "온보딩 시 마케팅 동의는 필수입니다."
            );
        }

        // 이미 온보딩 완료된 경우 예외
        if (user.getUsername() != null) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "이미 온보딩이 완료된 사용자입니다."
            );
        }

        // 필수 약관 동의 여부 확인
        if (!userInfoUpdateReqDto.requiredAgree()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "필수 약관 동의가 필요합니다."
            );
        }

        List<UserPosition> positions = userInfoUpdateReqDto.defaultPositions();

        if (positions == null || positions.isEmpty() || positions.size() > 2) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "직군은 최대 2개까지만 선택할 수 있습니다."
            );
        }

        // 아무것도 보내지 않으면 초기 이미지는 null로
        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = s3Service.uploadFile(profileImage, "sossbar/profile");
        }

        // 새 링크 저장
        List<UserLink> newLinks = null;
        if (userInfoUpdateReqDto.links() != null) {
            newLinks = userInfoUpdateReqDto.links().stream()
                    .map(linkDto -> UserLinkReqDto.createLink(user, linkDto))
                    .toList();
        }

        user.updateUserInfo(userInfoUpdateReqDto, profileImageUrl, newLinks);
        userRepository.saveAndFlush(user);

        // 마케팅 동의 여부 저장
        user.updateMarketingAgree(userInfoUpdateReqDto.marketingAgree());

        return UserInfoResDto.from(user);
    }

    // 마이페이지 - 내 계정 정보 조회(실명, 이메일)
    public UserInfoResDto getUserInfo(Principal principal) {
        Long id = Long.parseLong(principal.getName());
        User user = getUserById(id);

        return UserInfoResDto.from(user);
    }

    // 회원 탈퇴 - soft delete
    @Transactional
    public void deleteUser(Principal principal, UserDeleteReqDto reqDto) {
        Long id = Long.parseLong(principal.getName());
        User user = getUserById(id);

        // 기타 선택 + 상세 내용 입력이 없는 경우 예외
        if (reqDto.userDeleteReasonEnum() == UserDeleteReasonEnum.ETC
                && (reqDto.detail() == null || reqDto.detail().isBlank())) {
            throw new BusinessException(ErrorCode.INVALID_USER_DELETE_EXCEPTION,
                    ErrorCode.INVALID_USER_DELETE_EXCEPTION.getMessage() + id);
        }

        // 탈퇴 사유 저장
        UserDeleteReason reason = new UserDeleteReason(reqDto.userDeleteReasonEnum(), reqDto.detail());
        userDeleteReasonLogRepository.save(reason);

        // 프로젝트 처리
        // 사용자가 속한 프로젝트 목록
        List<ProjectMember> memberships = projectMemberRepository.findAllByUser(user);

        // 현재 사용자가 리더인지 확인 후 프로젝트 참여일이 가장 빠른 팀원에게 권한 위임
        for (ProjectMember membership : memberships) {
            Project project = membership.getProject();
            boolean isLeader = membership.getMemberStatus() == MemberStatus.LEADER;

            // 리더인 경우 권한 위임
            if (isLeader) {
                // 남은 팀원
                ProjectMember nextLeader = projectMemberRepository
                                .findFirstByProjectAndUser_IdNotAndUser_IsDeletedFalseOrderByCreatedAtAsc(project, user.getId())
                                .orElse(null);

                // 남은 팀원이 없으면 프로젝트 상태 변경(DELETED)
                if (nextLeader == null) {
                    project.deleteProject();
                    projectMemberRepository.delete(membership);
                    continue;
                }

                // 리더 권한 위임
                nextLeader.changeMemberStatus(MemberStatus.LEADER);
            }

            projectMemberRepository.delete(membership);
        }

        user.deleteUser();
    }

    // entity 찾는 공통 메소드
    private User getUserById(Long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION
                        , ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));
    }
}
