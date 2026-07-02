package com.sossbar.notification.dto.response;

import com.sossbar.notification.entity.Notification;
import com.sossbar.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResDto (
    Long notificationId,
    String notificationType,
    String title,
    String body,
    boolean isRead,
    LocalDateTime createdAt
) {
    public static NotificationResDto from(Notification notification) {
        return new NotificationResDto(
                notification.getNotificationId(),
                notification.getNotificationType().name(),
                notification.getTitle(),
                notification.getBody(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
