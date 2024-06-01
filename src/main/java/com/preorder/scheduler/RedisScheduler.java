package com.preorder.scheduler;

import com.preorder.domain.Product;
import com.preorder.global.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisScheduler {

    private final CacheService cacheService;

    private final JdbcTemplate jdbcTemplate;


    @Transactional
    @Scheduled(cron = "0 * * * * *") // 매분 0초 마다
    public void redisWriteBackRdb() {
        Set<String> allProductKey = cacheService.getAllProductKey();
        List<Product> productList = new ArrayList<>();

        for (String key : allProductKey) {

            Long value = cacheService.getRedisCacheOrNull(key);
            assert (key.length() >= 2);
            Long productId = Long.parseLong(key.substring(key.length() - 1));
            Product product = Product.builder()
                    .id(productId)
                    .productNum(value)
                    .build();
            productList.add(product);
        }

        this.jdbcTemplate.batchUpdate(
                "update tb_product set product_num = ? where id = ?",
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {
                        ps.setLong(1, productList.get(i).getProductNum());
                        ps.setLong(2, productList.get(i).getId());
                    }

                    public int getBatchSize() {
                        return productList.size();
                    }

                });
    }


}
