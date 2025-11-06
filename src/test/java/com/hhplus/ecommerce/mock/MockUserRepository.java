package com.hhplus.ecommerce.mock;

import com.hhplus.ecommerce.domain.entity.User;
import com.hhplus.ecommerce.domain.repository.UserRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UserRepository Mock 구현
 * - 테스트용 인메모리 저장소
 * - 동시성 테스트를 위해 ConcurrentHashMap 사용
 */
public class MockUserRepository implements UserRepository {

    // 동시성 제어를 위한 ConcurrentHashMap
    private final Map<Long, User> users = new ConcurrentHashMap<>();

    // 비관적 락 시뮬레이션을 위한 락 맵
    private final Map<Long, Object> locks = new ConcurrentHashMap<>();

    public MockUserRepository() {
        // 빈 상태로 시작 - 각 테스트에서 필요한 데이터를 save()로 추가
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        locks.putIfAbsent(user.getId(), new Object());  // lock도 함께 추가
        return user;
    }

    /**
     * 비관적 락으로 사용자 조회 (동시성 제어)
     * - synchronized 블록으로 원자적 처리 보장
     */
    @Override
    public Optional<User> findByIdWithLock(Long userId) {
        // 락 객체가 없으면 생성
        locks.putIfAbsent(userId, new Object());

        // 해당 사용자의 락을 획득하고 조회
        synchronized (locks.get(userId)) {
            return Optional.ofNullable(users.get(userId));
        }
    }
}
