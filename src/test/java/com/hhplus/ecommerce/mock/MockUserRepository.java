package com.hhplus.ecommerce.mock;

import com.hhplus.ecommerce.domain.entity.User;
import com.hhplus.ecommerce.domain.repository.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UserRepository Mock 구현
 * - 테스트용 인메모리 저장소
 * - synchronized로 DB 비관적 락 시뮬레이션
 * - 실제 프로덕션에서는 JPA @Lock(PESSIMISTIC_WRITE) 사용 필요
 */
public class MockUserRepository implements UserRepository {

    private final Map<Long, User> users = new ConcurrentHashMap<>();

    @Override
    public synchronized Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public synchronized User save(User user) {
        users.put(user.getId(), user);
        return user;
    }
}
