package com.hhplus.ecommerce.mock;

import com.hhplus.ecommerce.domain.entity.Product;
import com.hhplus.ecommerce.domain.repository.ProductRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ProductRepository Mock 구현
 * - 테스트용 인메모리 저장소
 * - 동시성 테스트를 위해 ConcurrentHashMap 사용
 */
public class MockProductRepository implements ProductRepository {

    // 동시성 제어를 위한 ConcurrentHashMap
    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    // 비관적 락 시뮬레이션을 위한 락 맵
    private final Map<Long, Object> locks = new ConcurrentHashMap<>();

    public MockProductRepository() {
        // 빈 상태로 시작 - 각 테스트에서 필요한 데이터를 save()로 추가
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return Optional.ofNullable(products.get(productId));
    }

    @Override
    public List<Product> findAll(int page, int size, String search) {
        List<Product> allProducts = new ArrayList<>(products.values());

        // 검색어 필터링
        if (search != null && !search.isEmpty()) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getName().contains(search))
                    .collect(Collectors.toList());
        }

        // 페이징 처리
        int start = (page - 1) * size;
        int end = Math.min(start + size, allProducts.size());

        if (start >= allProducts.size()) {
            return new ArrayList<>();
        }

        return allProducts.subList(start, end);
    }

    @Override
    public long count(String search) {
        if (search == null || search.isEmpty()) {
            return products.size();
        }

        return products.values().stream()
                .filter(p -> p.getName().contains(search))
                .count();
    }

    @Override
    public List<Product> findTopSelling(int limit) {
        // 테스트용: 재고가 적은 순서로 반환 (많이 팔린 것으로 가정)
        return products.values().stream()
                .sorted(Comparator.comparingInt(Product::getStockQuantity))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        products.put(product.getId(), product);
        locks.putIfAbsent(product.getId(), new Object());  // lock도 함께 추가
        return product;
    }

    /**
     * 비관적 락으로 상품 조회 (동시성 제어)
     * - synchronized 블록으로 원자적 처리 보장
     */
    @Override
    public Optional<Product> findByIdWithLock(Long productId) {
        // 락 객체가 없으면 생성
        locks.putIfAbsent(productId, new Object());

        // 해당 상품의 락을 획득하고 조회
        synchronized (locks.get(productId)) {
            return Optional.ofNullable(products.get(productId));
        }
    }
}
