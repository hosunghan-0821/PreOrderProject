package com.preorder.controller;


import com.preorder.dto.viewdto.OrderViewDto;
import com.preorder.global.validation.ValidationMarker;
import com.preorder.service.facade.OrderFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class OrderController {


    private final OrderFacadeService orderFacadeService;
    @PostMapping("/orders")
    public ResponseEntity<Boolean> registerOrder(@Validated({ValidationMarker.OnOrder.class})  // 경계 값 검증
                                                   @RequestBody OrderViewDto orderViewDto) {

        assert (orderViewDto != null);

        orderFacadeService.registerOrder(orderViewDto);

        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.CREATED);
    }

}
