package com.preorder.efficiency;

import com.preorder.PreOrderApplication;
import com.preorder.domain.Option;
import com.preorder.domain.Order;
import com.preorder.domain.Product;
import com.preorder.dto.mapper.OptionMapper;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.dto.viewdto.OptionViewDto;
import com.preorder.dto.viewdto.OrderViewDto;
import com.preorder.dto.viewdto.ProductViewDto;
import com.preorder.global.cache.CacheString;
import com.preorder.repository.order.OrderRepository;
import com.preorder.repository.product.OptionRepository;
import com.preorder.repository.product.ProductRepository;
import com.preorder.service.facade.OrderFacadeService;
import com.preorder.service.order.OrderManageService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = PreOrderApplication.class)
@ActiveProfiles("local")
public class OrderConcurrentTest {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OptionMapper optionMapper;

    @Autowired
    private OrderManageService orderManageService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderFacadeService orderFacadeService;

    @Autowired
    private OptionRepository optionRepository;


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("[성공] : 사전예약동시섬 확인 테스트 코드")
    public void orderConcurrentTest() throws InterruptedException {

        //given
        //상품 제고 충전
        //해당하는 OrderViewDto 만들기
        Product product = Product.builder()
                .price(1000)
                .name("hhh")
                .productNum(30L)
                .build();

        Product product1 = Product.builder()
                .price(1000)
                .name("kkk")
                .productNum(10L)
                .build();

        Product product2 = Product.builder()
                .price(1000)
                .name("ccc")
                .productNum(10L)
                .build();

        Product save = productRepository.save(product);
        Product save1 = productRepository.save(product1);
        Product save2 = productRepository.save(product2);

        Option option = Option.builder()
                .name("토핑1")
                .fee(500)
                .optionType("옵션1")
                .product(save)
                .build();
        Option savedOption = optionRepository.save(option);


        assert save.getId() == 1L;
        assert save1.getId() == 2L;
        assert save2.getId() == 3L;

        ProductViewDto productViewDto = productMapper.changeToProductViewDto(product);
        ProductViewDto productViewDto1 = productMapper.changeToProductViewDto(product1);
        ProductViewDto productViewDto2 = productMapper.changeToProductViewDto(product2);

        OptionViewDto optionViewDto = optionMapper.changeToOptionViewDto(savedOption);

        productViewDto.setOptionViewDtoList(Arrays.asList(optionViewDto));
        productViewDto1.setOptionViewDtoList(new ArrayList<>());
        productViewDto2.setOptionViewDtoList(new ArrayList<>());

        List<ProductViewDto> productViewDtoList = Arrays.asList(productViewDto, productViewDto);
        Map<Long, Long> orderInfoMap = orderFacadeService.getOrderInfoMap(productViewDtoList);


        cacheManager.addCache(CacheString.PRODUCT_COUNT_CACHE);
        Cache cache = cacheManager.getCache(CacheString.PRODUCT_COUNT_CACHE);
        cacheManager.getTransactionController().begin();
        cache.put(new Element(product.getId(), product.getProductNum()));
        Long setNum = (Long) cache.get(product.getId()).getObjectValue();
        cacheManager.getTransactionController().commit();

        Assertions.assertThat(setNum).isEqualTo(product.getProductNum());


        //when
        int threadCnt = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);


        OrderViewDto orderViewDto = OrderViewDto.builder()
                .clientName("한포성")
                .clientPhoneNum("010-9575-6302")
                .reservationDate(LocalDate.now().plusDays(3).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                .products(productViewDtoList)
                .build();


        for (int i = 0; i < threadCnt; i++) {
            executorService.execute(() -> {
                try {
                    orderManageService.registerOrderWithLock(orderViewDto, orderInfoMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();


        //then
        cache = cacheManager.getCache(CacheString.PRODUCT_COUNT_CACHE);

        Element element = cache.get(save.getId());
        Long remainNum = (Long) element.getObjectValue();

        Assertions.assertThat(remainNum).isEqualTo(10L);


        List<Order> orderList = orderRepository.findAll();
        Assertions.assertThat(orderList.size()).isEqualTo(threadCnt);


    }
}
