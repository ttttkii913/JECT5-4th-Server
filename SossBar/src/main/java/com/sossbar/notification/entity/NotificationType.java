package com.sossbar.notification.entity;

public enum NotificationType {
    PROJECT_JOINED, // 누군가 초대 수락 후 프로젝트 합류 -> 기존 팀원 전체에게 알림
    TEAM_COMPLETED // 팀장이 팀 확정 -> 전체 팀원에게 알림
}
