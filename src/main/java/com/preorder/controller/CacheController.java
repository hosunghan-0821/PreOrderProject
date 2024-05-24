package com.preorder.controller;

import com.preorder.global.cache.CacheService;
import com.preorder.global.cache.CacheString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class CacheController {

    private final CacheService cacheService;

    @GetMapping("/caches/all")
    public void getAllCache() {
        cacheService.getCacheManager().getTransactionController().begin();
        Cache cache = cacheService.getCache(CacheString.PRODUCT_COUNT_CACHE);

        for (Object key : cache.getKeys()) {
            Element element = cache.get(key);
            if (element != null) {
                System.out.println("Key = " + key + ", Value = " + element.getObjectValue());
            }
        }
        cacheService.getCacheManager().getTransactionController().commit();
    }


}
