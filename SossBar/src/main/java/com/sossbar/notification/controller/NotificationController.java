package com.sossbar.notification.controller;

import com.sossbar.global.common.code.SuccessCode;
import com.sossbar.global.common.template.ApiResTemplate;
import com.sossbar.notification.dto.response.NotificationResDto;
import com.sossbar.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Tag(name = "Notification API", description = "알림 관련 API")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "내 알림 목록 조회", description = "로그인한 사용자가 알림 목록을 조회할 수 있습니다.")
    @GetMapping("/api/v1/notifications/all")
    public ApiResTemplate<List<NotificationResDto>> getMyNotifications(Principal principal) {
        List<NotificationResDto> notifications = notificationService.getMyNotifications(principal);

        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, notifications);
    }

    @Operation(summary = "안 읽은 알림 개수 조회", description = "로그인한 사용자가 안 읽은 알림 개수를 조회할 수 있습니다.")
    @GetMapping("/api/v1/notifications/unread-count")
    public ApiResTemplate<Long> getUnreadNotifications(Principal principal) {
        Long unreadCount = notificationService.getUnreadCount(principal);

        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, unreadCount);
    }

    @Operation(summary = "알림 개별 읽음 처리", description = "사용자의 개별 알림 읽음을 처리합니다.")
    @PatchMapping("/api/v1/notifications/read/{notificationId}")
    public ApiResTemplate<Boolean> markAsRead(
            @PathVariable Long notificationId,
            Principal principal
    ) {
        boolean updated = notificationService.markAsRead(principal, notificationId);

        return ApiResTemplate.successResponse(SuccessCode.UPDATE_SUCCESS, updated);
    }

    @Operation(summary = "알림 전체 읽음 처리", description = "사용자의 모든 알림 읽음을 처리합니다.")
    @PatchMapping("/api/v1/notifications/read/all")
    public ApiResTemplate<Boolean> markAllAsRead(Principal principal) {
        boolean updated = notificationService.markAllAsRead(principal);

        return ApiResTemplate.successResponse(SuccessCode.UPDATE_SUCCESS, updated);
    }
}
