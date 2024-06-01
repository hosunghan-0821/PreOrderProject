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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.preorder.global.cache.CacheString.PRODUCT_COUNT_CACHE;

@RequiredArgsConstructor
@Slf4j
@Component
public class CacheService {

    // Lua Script


    public static final String PRODUCT_CACHE_PREFIX = "PRODUCT_";

    private final ProductRepository productRepository;

    @Getter
    private final CacheManager cacheManager;

    private final RedisTemplate<String, Long> redisTemplate;


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

    @EventListener(ApplicationReadyEvent.class)
    public void setProductCacheToRedis() {

        List<Product> productList = productRepository.findAll();

        for (Product product : productList) {
            redisTemplate.opsForValue().set(PRODUCT_CACHE_PREFIX + product.getId(), product.getProductNum());
        }
    }

    public Long getRedisCacheOrNull(String redisCacheKey) {
        Object object = redisTemplate.opsForValue().get(redisCacheKey);
        return (Long) object;
    }

    public Set<String> getAllProductKey() {

        Set<String> keys = redisTemplate.keys(PRODUCT_CACHE_PREFIX + "*");
        return keys;
    }

    public boolean decreaseProductNumByOrder(Map<Long, Long> orderMapInfo) {

        assert (!orderMapInfo.isEmpty());

        List<String> keyList = new ArrayList<>();
        List<Object> orderNumList = new ArrayList<>();
        for (var orderData : orderMapInfo.entrySet()) {
            keyList.add(PRODUCT_CACHE_PREFIX + orderData.getKey());
            orderNumList.add(orderData.getValue());
            log.info("key =  {}, value = {}", PRODUCT_CACHE_PREFIX + orderData.getKey(), orderData.getValue());
        }
        StringBuilder luaScriptDynamicStringBuilder = getLuaScriptDynamic(keyList);



        RedisScript<Boolean> decrementLeftSeatRedisScript = new DefaultRedisScript<>(luaScriptDynamicStringBuilder.toString(), Boolean.class);


        return redisTemplate.execute(decrementLeftSeatRedisScript, keyList, orderNumList.toArray());
    }

    private StringBuilder getLuaScriptDynamic(List<String> keyList) {
        StringBuilder finalStringBuilder = new StringBuilder();

        //변수 만들기
        StringBuilder innerStringBuilder;
        for (int i = 1; i <= keyList.size(); i++) {
            innerStringBuilder = new StringBuilder();
            String productKey = "leftProductNum_" + i;

            innerStringBuilder.append("local ");
            innerStringBuilder.append(productKey);
            innerStringBuilder.append(" = tonumber(redis.call('get', KEYS[");
            innerStringBuilder.append(i);
            innerStringBuilder.append("])) ");
            //String data1 = "local " + productKey + " = tonumber(redis.call('get', KEYS[" + i + "])) ";
            finalStringBuilder.append(innerStringBuilder.toString());
        }

        finalStringBuilder.append("if ");

       innerStringBuilder = new StringBuilder();
       //조건문 만들기
        for (int i = 1; i <= keyList.size(); i++) {
            String productKey = "leftProductNum_" + i;

            innerStringBuilder.append(productKey);
            innerStringBuilder.append("- tonumber(ARGV[");
            innerStringBuilder.append(i);
            innerStringBuilder.append("]) >= 0 ");
            //data2 += productKey + " - tonumber(ARGV[" + i + "]) >= 0 ";
            if (i == keyList.size()) {
                //data2 += "then ";
                innerStringBuilder.append("then ");
            } else {
                //data2 += "and ";
                innerStringBuilder.append("and ");
            }
        }
        finalStringBuilder.append(innerStringBuilder.toString());

        // 변수 생성 및 값 대입하기
        for (int i = 1; i <= keyList.size(); i++) {
            innerStringBuilder = new StringBuilder();
            String productKey = "leftProductNum_" + i;
            String productLeftValue = "leftProductNum_" + i + "_Value";

            innerStringBuilder.append("local ");
            innerStringBuilder.append(productLeftValue);
            innerStringBuilder.append(" = ");
            innerStringBuilder.append(productKey);
            innerStringBuilder.append(" - tonumber(ARGV[");
            innerStringBuilder.append(i);
            innerStringBuilder.append("]) ");

            innerStringBuilder.append("redis.call('set', KEYS[");
            innerStringBuilder.append(i);
            innerStringBuilder.append("], ");
            innerStringBuilder.append(productLeftValue);
            innerStringBuilder.append(") ");

//            String data = "local " + productLeftValue + " = " + productKey + " - tonumber(ARGV[" + i + "]) "
//                    + "redis.call('set', KEYS[" + i + "], " + productLeftValue + ") ";
            finalStringBuilder.append(innerStringBuilder.toString());
        }
        finalStringBuilder.append(" return true ");
        finalStringBuilder.append(" else return false ");
        finalStringBuilder.append(" end ");
        return finalStringBuilder;
    }


}

