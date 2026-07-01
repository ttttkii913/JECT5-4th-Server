package com.sossbar.user.repository;

import com.sossbar.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    // 사용자 고유 uuid로 조회
    Optional<User> findByUserLinkAndIsDeletedFalse(String userLink);
}
