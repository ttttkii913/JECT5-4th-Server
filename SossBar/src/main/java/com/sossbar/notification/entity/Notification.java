package com.sossbar.notification.entity;

import com.sossbar.projects.enums.ProjectStatus;
import com.sossbar.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // 알림 받는 사람

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Notification(User receiver, NotificationType notificationType, String title, String body) {
        this.receiver = receiver;
        this.notificationType = notificationType;
        this.title = title;
        this.body = body;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
