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
import com.sossbar.projects.service.ProjectMemberService;
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
@Tag(name = "Project Member API", description = "프로젝트 멤버 관련 API")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @Operation(summary = "팀원 추가", description = "프로젝트에 팀원을 추가하는 API입니다.")
    @PostMapping("/invite/{projectLink}")
    public ApiResTemplate<Void> inviteProjectMember(Principal principal,
                                                    @PathVariable("projectLink") String projectLink) {
        projectMemberService.inviteProjectMember(principal, projectLink);
        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, null);
    }

    @Operation(summary = "팀원 삭제", description = "프로젝트에서 팀원을 삭제하는 API입니다.")
    @DeleteMapping("/{projectId}/{userId}")
    public ApiResTemplate<Void> deleteProjectMember(Principal principal,
                                                    @PathVariable("projectId") Long projectId,
                                                    @PathVariable("userId") Long userId) {
        projectMemberService.deleteProjectMember(principal, projectId, userId);
        return ApiResTemplate.successWithNoContent(SuccessCode.DELETE_SUCCESS);
    }

    @Operation(summary = "팀원 확정", description = "프로젝트 팀원을 확정하는 API입니다.")
    @PatchMapping("/confirm/{projectId}")
    public ApiResTemplate<Void> confirmProjectMembers(Principal principal,
                                                      @PathVariable("projectId") Long projectId) {
        projectMemberService.confirmProjectMembers(principal, projectId);
        return ApiResTemplate.successResponse(SuccessCode.UPDATE_SUCCESS, null);
    }
}
