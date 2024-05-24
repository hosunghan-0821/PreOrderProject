package com.preorder.global.cache;

import com.preorder.domain.Product;
import com.preorder.repository.product.ProductRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.preorder.global.cache.CacheString.PRODUCT_COUNT_CACHE;

@RequiredArgsConstructor
@Slf4j
@Component
public class CacheService {

    private final ProductRepository productRepository;

    @Getter
    private final CacheManager cacheManager;

    public Cache getCache(String cacheKey) {
        return cacheManager.getCache(cacheKey);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setProductCache() {

        Cache cache = cacheManager.getCache(PRODUCT_COUNT_CACHE);
        List<Product> productList = productRepository.findAll();

        cacheManager.getTransactionController().begin();
        productList.forEach(v -> cache.put(new Element(v.getId(), v.getProductNum())));
        cacheManager.getTransactionController().commit();
    }
}
