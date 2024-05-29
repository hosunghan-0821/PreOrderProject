package com.preorder.global.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CacheServiceTest {

    @Autowired
    private CacheService cacheService;


    @Test
    @Disabled
    @Order(1)
    void setTest() {
        Cache cache = cacheService.getCache(CacheString.PRODUCT_COUNT_CACHE);

        Element element;
        Long value;

        element = cache.get(1L);
        value = (Long) element.getObjectValue();
        System.out.println(value);

        element = cache.get(2L);
        value = (Long) element.getObjectValue();
        System.out.println(value);


        element = cache.get(3L);
        value = (Long) element.getObjectValue();
        System.out.println(value);

    }


}