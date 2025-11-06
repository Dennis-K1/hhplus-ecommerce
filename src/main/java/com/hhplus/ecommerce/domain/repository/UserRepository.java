package com.hhplus.ecommerce.domain.repository;

import com.hhplus.ecommerce.domain.entity.User;

import java.util.Optional;

/**
 * 사용자 리포지토리 인터페이스
 * - 도메인 레이어에 위치하여 인프라스트럭처 독립성 유지
 */
public interface UserRepository {

    /**
     * 사용자 조회
     */
    Optional<User> findById(Long userId);

    /**
     * 사용자 저장 (포인트 업데이트)
     */
    User save(User user);

    /**
     * 비관적 락으로 사용자 조회 (동시성 제어)
     */
    Optional<User> findByIdWithLock(Long userId);
}
