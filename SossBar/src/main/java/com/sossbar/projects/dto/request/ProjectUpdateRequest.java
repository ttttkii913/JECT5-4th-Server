package com.sossbar.projects.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sossbar.user.entity.UserPosition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "프로젝트 수정 요청")
public class ProjectUpdateRequest {

    @Schema(description = "프로젝트명", example = "소스바 앱 개발 v2")
    private String projectName;

    @Schema(description = "주최사", example = "멋쟁이사자처럼")
    private String host;

    // 이미지는 MultipartFile로 별도 수신 (Controller의 @RequestPart("image") 참고)
}
