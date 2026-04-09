package com.sossbar.projects.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sossbar.projects.enums.ProjectStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProjectResponse {

    private Long projectId;
    private String projectName;
    private String host;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
    private String projectLink;
    private String projectImage;
    private ProjectStatus projectStatus;
    private List<ProjectMemberResponse> members;
}
