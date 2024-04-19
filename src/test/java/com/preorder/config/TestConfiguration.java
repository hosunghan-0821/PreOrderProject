package com.preorder.config;


import com.preorder.dto.mapper.OrderMapper;
import com.preorder.dto.mapper.ProductMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory(){
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public ProductMapper productMapper(){
       return Mappers.getMapper(ProductMapper.class);
    }

    @Bean
    public OrderMapper orderMapper(){
        return Mappers.getMapper(OrderMapper.class);
    }
}
