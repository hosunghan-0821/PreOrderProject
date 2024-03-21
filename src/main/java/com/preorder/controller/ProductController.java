package com.preorder.controller;


import com.preorder.dto.viewdto.ProductViewDto;
import com.preorder.service.product.ProductFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.preorder.global.validation.ValidationMarker.OnCreate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {


    private final ProductFacadeService productFacadeService;
    @PostMapping("/product")
    public ResponseEntity<?> registerProduct(@Validated({OnCreate.class})  // 경계 값 검증
                                                 @RequestBody ProductViewDto productViewDto) {

        assert (productViewDto != null);

        productFacadeService.registerProduct(productViewDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
