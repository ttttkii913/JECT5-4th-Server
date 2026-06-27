package com.sossbar.projects.facade;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.global.config.S3Service;
import com.sossbar.projects.dto.request.ProjectCreateRequest;
import com.sossbar.projects.dto.request.ProjectUpdateRequest;
import com.sossbar.projects.dto.response.ProjectResponse;
import com.sossbar.projects.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectFacade {

    private final ProjectService projectService;
    private final S3Service s3Service;

    private static final String S3_DIR = "project";

    public ProjectResponse createProject(Principal principal, ProjectCreateRequest request, MultipartFile image) {
        // 1. 이미지가 있으면 S3 업로드
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.uploadFile(image, S3_DIR);
        }

        // 2. DB 저장 - 실패 시 S3 보상 삭제
        try {
            return projectService.createProject(principal, request, imageUrl);
        } catch (Exception e) {
            log.error("[ProjectFacade] {} {}", ErrorCode.PROJECT_CREATE_ROLLBACK_EXCEPTION.getMessage(), imageUrl, e);
            s3Service.deleteFile(imageUrl);
            throw new BusinessException(
                    ErrorCode.PROJECT_CREATE_ROLLBACK_EXCEPTION,
                    ErrorCode.PROJECT_CREATE_ROLLBACK_EXCEPTION.getMessage() + imageUrl);
        }
    }

    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request, MultipartFile image, Principal principal) {
        // 1. 기존 이미지 URL 미리 조회 (DB 수정 성공 후 기존 S3 이미지 삭제에 사용)
        String oldImageUrl = projectService.getProjectImageUrl(projectId);

        // 2. 새 이미지가 있으면 S3 업로드
        String newImageUrl = null;
        if (image != null && !image.isEmpty()) {
            newImageUrl = s3Service.uploadFile(image, S3_DIR);
        }

        // 3. DB 수정 - 실패 시 새로 올린 S3 이미지 보상 삭제
        ProjectResponse response;
        try {
            response = projectService.updateProject(projectId, request, newImageUrl, principal);
        } catch (Exception e) {
            log.error("[ProjectFacade] {} {}", ErrorCode.PROJECT_UPDATE_ROLLBACK_EXCEPTION.getMessage(), newImageUrl, e);
            s3Service.deleteFile(newImageUrl);
            throw new BusinessException(
                    ErrorCode.PROJECT_UPDATE_ROLLBACK_EXCEPTION,
                    ErrorCode.PROJECT_UPDATE_ROLLBACK_EXCEPTION.getMessage() + newImageUrl);
        }

        // 4. DB 수정 성공 후 기존 S3 이미지 삭제 (새 이미지가 있을 때만)
        if (newImageUrl != null) {
            s3Service.deleteFile(oldImageUrl);
        }

        return response;
    }

    public void deleteProject(Long projectId) {
        // 1. 삭제할 이미지 URL 미리 조회
        String imageUrl = projectService.getProjectImageUrl(projectId);

        // 2. DB 삭제 - 실패 시 예외 그대로 throw (S3 삭제하지 않음)
        projectService.deleteProject(projectId);

        // 3. DB 삭제 성공 후 S3 이미지 삭제
        s3Service.deleteFile(imageUrl);
    }
}
