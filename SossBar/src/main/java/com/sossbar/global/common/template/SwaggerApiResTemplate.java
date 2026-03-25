package com.sossbar.global.common.template;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "성공 처리",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApiResTemplate.class),
                        examples = @ExampleObject(
                                value = "{\n" +
                                        "  \"status\": 200,\n" +
                                        "  \"code\": \"COMMON-200\",\n" +
                                        "  \"message\": \"성공적으로 조회했습니다.\",\n" +
                                        "  \"data\": {}\n" +
                                        "}"
                        )
                )
        ),

        @ApiResponse(
                responseCode = "404",
                description = "찾을 수 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApiResTemplate.class),
                        examples = @ExampleObject(
                                value = "{\n" +
                                        "  \"status\": 404,\n" +
                                        "  \"code\": \"USER-001\",\n" +
                                        "  \"message\": \"해당 사용자가 없습니다.\",\n" +
                                        "  \"data\": null\n" +
                                        "}"
                        )
                )
        ),

        @ApiResponse(
                responseCode = "500",
                description = "내부 서버 오류",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApiResTemplate.class),
                        examples = @ExampleObject(
                                value = "{\n" +
                                        "  \"status\": 500,\n" +
                                        "  \"code\": \"COMMON-002\",\n" +
                                        "  \"message\": \"내부 서버 에러가 발생했습니다.\",\n" +
                                        "  \"data\": null\n" +
                                        "}"
                        )
                )
        )
})
public @interface SwaggerApiResTemplate {
    // swagger response template
    // controller 상단에 @SwaggerApiResTemplate import
}
