package com.sossbar.user.entity;

import com.sossbar.global.common.template.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLink extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_link_id")
    private Long id;
    private String userLink;

    @Enumerated(EnumType.STRING)
    private UserLinkType userLinkType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public UserLink(String userLink, UserLinkType userLinkType, User user) {
        this.userLink = userLink;
        this.userLinkType = userLinkType;
        this.user = user;
    }
}
