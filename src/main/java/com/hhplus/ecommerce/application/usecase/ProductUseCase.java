package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.Product;
import com.hhplus.ecommerce.domain.exception.ProductNotFoundException;
import com.hhplus.ecommerce.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 상품 유스케이스
 * - 상품 조회 및 재고 관리 비즈니스 로직
 */
@Service
@Transactional(readOnly = true)
public class ProductUseCase {

    private final ProductRepository productRepository;

    public ProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 상품 목록 조회 (페이징)
     */
    public List<Product> getProducts(int page, int size, String search) {
        return productRepository.findAll(page, size, search);
    }

    /**
     * 상품 목록 전체 개수 조회
     */
    public long getProductCount(String search) {
        return productRepository.count(search);
    }

    /**
     * 상품 상세 조회
     */
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);
    }

    /**
     * 인기 상품 조회 (최근 판매량 기준 Top 5)
     */
    public List<Product> getTopProducts() {
        return productRepository.findTopSelling(5);
    }

    /**
     * 재고 확인
     */
    public boolean checkStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        return product.hasEnoughStock(quantity);
    }
}
