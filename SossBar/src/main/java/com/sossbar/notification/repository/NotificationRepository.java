package com.sossbar.notification.repository;

import com.sossbar.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId); // 최신순으로 알림 조회

    Long countByReceiverIdAndIsReadFalse(Long receiverId); // 읽지 않은 알림 수 세기

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiver.id = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId);
}
