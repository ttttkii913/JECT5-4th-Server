package com.sossbar.user_delete_reason.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDeleteReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delete_reason_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserDeleteReasonEnum userDeleteReasonEnum;

    private String detail;

    @Builder
    public UserDeleteReason(UserDeleteReasonEnum userDeleteReasonEnum, String detail) {
        this.userDeleteReasonEnum = userDeleteReasonEnum;
        this.detail = detail;
    }
}
