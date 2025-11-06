package com.hhplus.ecommerce.mock;

import com.hhplus.ecommerce.domain.entity.Product;
import com.hhplus.ecommerce.domain.repository.ProductRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ProductRepository Mock 구현
 * - 테스트용 인메모리 저장소
 * - synchronized로 DB 비관적 락 시뮬레이션
 * - 실제 프로덕션에서는 JPA @Lock(PESSIMISTIC_WRITE) 사용 필요
 */
public class MockProductRepository implements ProductRepository {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    @Override
    public synchronized Optional<Product> findById(Long productId) {
        return Optional.ofNullable(products.get(productId));
    }

    /**
     * 비관적 락을 시뮬레이션하는 조회 메서드
     * synchronized로 동시성 제어
     */
    public synchronized Optional<Product> findByIdForUpdate(Long productId) {
        return Optional.ofNullable(products.get(productId));
    }

    @Override
    public List<Product> findAll(int page, int size, String search) {
        List<Product> allProducts = new ArrayList<>(products.values());

        if (search != null && !search.isEmpty()) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getName().contains(search))
                    .collect(Collectors.toList());
        }

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
        return products.values().stream()
                .sorted(Comparator.comparingInt(Product::getStockQuantity))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized Product save(Product product) {
        products.put(product.getId(), product);
        return product;
    }
}
