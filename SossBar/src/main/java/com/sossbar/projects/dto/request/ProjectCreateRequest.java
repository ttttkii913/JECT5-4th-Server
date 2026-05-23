package com.sossbar.projects.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "프로젝트 생성 요청")
public class ProjectCreateRequest {

    @NotBlank(message = "프로젝트명은 필수입니다.")
    @Schema(description = "프로젝트명", example = "소스바 앱 개발", requiredMode = Schema.RequiredMode.REQUIRED)
    private String projectName;

    @NotBlank(message = "주최사는 필수입니다.")
    @Schema(description = "주최사", example = "멋쟁이사자처럼", requiredMode = Schema.RequiredMode.REQUIRED)
    private String host;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "프로젝트 시작 날짜", example = "2025-01-01T00:00:00")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "프로젝트 종료 날짜", example = "2025-12-31T23:59:59")
    private LocalDateTime endDate;

    // 이미지는 MultipartFile로 별도 수신 (Controller의 @RequestPart("image") 참고)
}
