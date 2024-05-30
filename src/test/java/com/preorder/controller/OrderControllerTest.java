package com.preorder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.preorder.dto.viewdto.OrderViewDto;
import com.preorder.dto.viewdto.ProductViewDto;
import com.preorder.service.facade.OrderFacadeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @InjectMocks
    OrderController orderController;

    @Mock
    private OrderFacadeService orderFacadeService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    private OrderViewDto orderViewDto() {
        return OrderViewDto.builder()
                .clientName("한호성")
                .clientPhoneNum("01095756302")
                .reservationDate(Instant.now())
                .products(Arrays.asList(ProductViewDto.builder().id(1L).name("마이넘버원").build()))
                .build();
    }

    private OrderViewDto getDetailOrderViewDto() {
        return OrderViewDto.builder()
                .id(1L)
                .clientName("한호성")
                .clientPhoneNum("01095756302")
                .reservationDate(Instant.now())
                .products(Arrays.asList(ProductViewDto.builder().id(1L).name("마이넘버원").build()))
                .build();
    }

    private List<OrderViewDto> getOrderViewDtoList() {
        List<OrderViewDto> orderViewDtoList = new ArrayList<>();

        OrderViewDto orderViewDto1 = OrderViewDto.builder()
                .id(1L)
                .clientName("한호성")
                .clientPhoneNum("01095756302")
                .reservationDate(Instant.now())
                .products(Arrays.asList(ProductViewDto.builder().id(1L).name("마이넘버원").build()))
                .build();

        OrderViewDto orderViewDto2 = OrderViewDto.builder()
                .id(1L)
                .clientName("한호성")
                .clientPhoneNum("01095756302")
                .reservationDate(Instant.now())
                .products(Arrays.asList(ProductViewDto.builder().id(1L).name("마이넘버원").build()))
                .build();

        orderViewDtoList.add(orderViewDto1);
        orderViewDtoList.add(orderViewDto2);

        return orderViewDtoList;
    }

    @BeforeEach
    public void init() {


        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("[성공] : 상품주문 API 테스트")
    void registerOrder() throws Exception {
        //given
        OrderViewDto orderViewDto = this.orderViewDto();

        Mockito.doNothing().when(orderFacadeService).registerOrder(any(OrderViewDto.class));
        String request = objectMapper.writeValueAsString(orderViewDto);


        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        );

        //then
        resultActions
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("[성공] : 상품주문 상세정보 조회")
    void getOrderDetail() throws Exception {

        //given
        OrderViewDto orderViewDto = this.getDetailOrderViewDto();

        Mockito.when(orderFacadeService.getOrderDetail(any(OrderViewDto.class))).thenReturn(orderViewDto);
        String request = objectMapper.writeValueAsString(orderViewDto);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/orders/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        );
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.clientName").exists())
                .andExpect(jsonPath("$.reservationDate").exists())
                .andExpect(jsonPath("$.products[0].id").exists());
    }

    @Test
    @DisplayName("[실패] : 상품주문 상세정보 조회 - assert Fail")
    void getOrderDetailFail() throws Exception {
        //when,then
        Assertions.assertThrows(AssertionError.class, () -> orderController.getOrderDetail(new OrderViewDto()));
        Assertions.assertThrows(AssertionError.class, () -> orderController.getOrderDetail(OrderViewDto.builder().id(1L).build()));
        Assertions.assertThrows(AssertionError.class, () -> orderController.getOrderDetail(OrderViewDto.builder().id(1L).clientName("").build()));
        Assertions.assertThrows(AssertionError.class, () -> orderController.getOrderDetail(OrderViewDto.builder().id(1L).clientName("han").build()));

    }

    @Test
    @DisplayName("[실패] : 상품주문 Controller")
    void registerOrderFail() throws Exception {
        Assertions.assertThrows(AssertionError.class, () -> orderController.registerOrder(null));
    }

    @Test
    @DisplayName("[성공] : 주문 다중조회 Controller")
    void getOrderList() throws Exception {

        //given
        List<OrderViewDto> orderViewDtoList = this.getOrderViewDtoList();
        Page<OrderViewDto> orderViewDtoPage = new PageImpl<>(orderViewDtoList);

        Mockito.when(orderFacadeService.getOrder(any())).thenReturn(orderViewDtoPage);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

    }
}