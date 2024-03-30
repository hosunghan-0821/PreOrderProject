package com.preorder.controller;


import com.preorder.dto.viewdto.OrderViewDto;
import com.preorder.global.validation.ValidationMarker;
import com.preorder.service.facade.OrderFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class OrderController {


    private final OrderFacadeService orderFacadeService;

    @PostMapping("/orders")
    public ResponseEntity<Boolean> registerOrder(@Validated({ValidationMarker.OnRegisterOrder.class})  // 경계 값 검증
                                                 @RequestBody OrderViewDto orderViewDto) {

        assert (orderViewDto != null);

        orderFacadeService.registerOrder(orderViewDto);

        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.CREATED);
    }

    @PostMapping("/orders/detail")
    public ResponseEntity<OrderViewDto> getOrderDetail(@RequestBody OrderViewDto orderViewDto) {

        //조회규칙 주문 ID,  핸드폰 번호,성명 모두 같아야 조회
        assert (orderViewDto.getId() != null);
        assert (orderViewDto.getClientName() != null && !orderViewDto.getClientName().isEmpty());
        assert (orderViewDto.getClientPhoneNum() != null);

        OrderViewDto orderDetail = orderFacadeService.getOrderDetail(orderViewDto);
        return ResponseEntity.ok(orderDetail);
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderViewDto>> getOrderList(Pageable pageable) {
        assert (pageable != null);

        Page<OrderViewDto> orderViewDtoList = orderFacadeService.getOrder(pageable);
        assert (orderViewDtoList != null);
        return ResponseEntity.ok(orderViewDtoList);
    }
}
