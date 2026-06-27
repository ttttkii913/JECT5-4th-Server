package com.sossbar.projects.controller;

import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.global.common.template.SwaggerApiResTemplate;
import com.sossbar.projects.dto.request.ProjectCreateRequest;
import com.sossbar.projects.dto.request.ProjectUpdateRequest;
import com.sossbar.projects.dto.response.MyProjectResponse;
import com.sossbar.projects.dto.response.ProjectResponse;
import com.sossbar.projects.dto.response.PublicProjectResponse;
import com.sossbar.projects.facade.ProjectFacade;
import com.sossbar.projects.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@SwaggerApiResTemplate
@Tag(name = "Project API", description = "프로젝트 관련 API")
public class ProjectController {

    private final ProjectFacade projectFacade;
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "새 프로젝트를 생성하는 API입니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResTemplate<ProjectResponse> createProject(
            Principal principal,
            @RequestPart("request") @Valid ProjectCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, projectFacade.createProject(principal, request, image));
    }

    @Operation(summary = "내 프로젝트 리스트 조회", description = "로그인한 사용자가 속한 프로젝트 목록을 조회하는 API입니다.")
    @GetMapping
    public ApiResTemplate<List<MyProjectResponse>> getMyProjects(Principal principal) {
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, projectService.getMyProjects(principal));
    }

    @Operation(summary = "유저 프로젝트 리스트 조회", description = "특정 유저가 속한 프로젝트 목록을 조회하는 API입니다.")
    @GetMapping("/users/{userId}")
    public ApiResTemplate<List<PublicProjectResponse>> getUserProjects(@PathVariable Long userId) {
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, projectService.getUserProjects(userId));
    }

    @Operation(summary = "프로젝트 조회", description = "프로젝트 ID로 단일 프로젝트를 조회하는 API입니다.")
    @GetMapping("/{projectId:\\d+}")
    public ApiResTemplate<ProjectResponse> getProject(
            @PathVariable Long projectId
    ) {
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, projectService.getProject(projectId));
    }

    @Operation(summary = "프로젝트 수정", description = "프로젝트 정보를 수정하는 API입니다.")
    @PatchMapping(value = "/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResTemplate<ProjectResponse> updateProject(
            @PathVariable Long projectId,
            @RequestPart("request") @Valid ProjectUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ApiResTemplate.successResponse(SuccessCode.UPDATE_SUCCESS, projectFacade.updateProject(projectId, request, image));
    }

    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제하는 API입니다.")
    @DeleteMapping("/{projectId}")
    public ApiResTemplate<Void> deleteProject(
            @PathVariable Long projectId
    ) {
        projectFacade.deleteProject(projectId);
        return ApiResTemplate.successWithNoContent(SuccessCode.DELETE_SUCCESS);
    }

    @Operation(summary = "팀원 추가", description = "프로젝트에 팀원을 추가하는 API입니다.")
    @PostMapping("invite/{projectId}")
    public ApiResTemplate<Void> inviteProjectMember(Principal principal, @PathVariable Long projectId) {
        projectService.inviteProjectMember(principal, projectId);
        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, null);
    }

    @Operation(summary = "팀원 삭제", description = "프로젝트에서 팀원을 삭제하는 API입니다.")
    @DeleteMapping("{projectId}/{userId}")
    public ApiResTemplate<Void> deleteProjectMember(Principal principal, @PathVariable Long projectId, @PathVariable Long userId) {
        projectService.deleteProjectMember(principal, projectId, userId);
        return ApiResTemplate.successWithNoContent(SuccessCode.DELETE_SUCCESS);
    }

    @Operation(summary = "팀원 확정", description = "프로젝트 팀원을 확정하는 API입니다.")
    @PatchMapping("confirm/{projectId}")
    public ApiResTemplate<Void> confirmProjectMembers(Principal principal, @PathVariable Long projectId) {
        projectService.confirmProjectMembers(principal, projectId);
        return ApiResTemplate.successResponse(SuccessCode.UPDATE_SUCCESS, null);
    }
}
