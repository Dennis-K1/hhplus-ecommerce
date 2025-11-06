package com.hhplus.ecommerce.domain.repository;

import com.hhplus.ecommerce.domain.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * 상품 리포지토리 인터페이스
 * - 도메인 레이어에 위치하여 인프라스트럭처 독립성 유지
 */
public interface ProductRepository {

    /**
     * 상품 조회
     */
    Optional<Product> findById(Long productId);

    /**
     * 상품 목록 조회 (페이징)
     */
    List<Product> findAll(int page, int size, String search);

    /**
     * 전체 상품 수 조회
     */
    long count(String search);

    /**
     * 인기 상품 조회 (최근 판매량 기준)
     */
    List<Product> findTopSelling(int limit);

    /**
     * 상품 저장 (재고 업데이트)
     */
    Product save(Product product);

    /**
     * 비관적 락으로 상품 조회 (동시성 제어)
     */
    Optional<Product> findByIdWithLock(Long productId);
}
