package com.sossbar.notification.service;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.exception.BusinessException;
import com.sossbar.notification.dto.response.NotificationResDto;
import com.sossbar.notification.entity.Notification;
import com.sossbar.notification.entity.NotificationType;
import com.sossbar.notification.repository.NotificationRepository;
import com.sossbar.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 알림 발송 (팀 확정 & 팀원 합류)
    @Transactional
    public void sendNotification(List<User> receivers, NotificationType notificationType, String title, String body) {
        if (receivers == null || receivers.isEmpty()) {
            return;
        }

        List<Notification> notifications = receivers.stream()
                .map(receiver -> Notification.builder()
                        .receiver(receiver)
                        .notificationType(notificationType)
                        .title(title)
                        .body(body)
                        .build())
                .toList();
        notificationRepository.saveAll(notifications);
    }

    // 내 알림 목록 조회
    public List<NotificationResDto> getMyNotifications(Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResDto::from)
                .toList();
    }

    // 안 읽은 알림 개수
    public Long getUnreadCount(Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    // 개별 알림 읽음 처리
    @Transactional
    public void markAsRead(Principal principal, Long notificationId) {
        Long userId = Long.parseLong(principal.getName());

        Notification notification = getNotification(notificationId);

        if (!notification.getReceiver().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOTIFICATION_NOT_AUTHORIZED, ErrorCode.NOTIFICATION_NOT_AUTHORIZED.getMessage());
        }
        notification.markAsRead();
    }

    // 전체 알림 읽음 처리
    @Transactional
    public void markAllAsRead(Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        notificationRepository.markAllAsRead(userId);
    }

    // 엔티티 조회 공통 메서드
    private Notification getNotification(Long notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND, ErrorCode.NOTIFICATION_NOT_FOUND.getMessage()));
    }
}
