package com.preorder.controller;


import com.preorder.dto.viewdto.ProductViewDto;
import com.preorder.global.validation.ValidationMarker;
import com.preorder.service.facade.ProductFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.preorder.global.validation.ValidationMarker.OnCreate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {


    private final ProductFacadeService productFacadeService;

    @PostMapping("/products")
    public ResponseEntity<Boolean> registerProduct(@Validated({OnCreate.class})  // 경계 값 검증
                                                   @RequestBody ProductViewDto productViewDto) {

        assert (productViewDto != null);

        productFacadeService.registerProduct(productViewDto);

        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.CREATED);
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductViewDto>> getProductList(Pageable pageable) {

        assert (pageable != null);

        return new ResponseEntity<>(productFacadeService.getProductList(pageable), HttpStatus.OK);

    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductViewDto> getProductDetail(@PathVariable(value = "id") Long id) {

        assert (id != null && id > 0);
        ProductViewDto productDetail = productFacadeService.getProductDetail(id);

        return new ResponseEntity<>(productDetail, HttpStatus.OK);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Boolean> updateProduct(@Validated({ValidationMarker.OnUpdate.class})  // 경계 값 검증
                                                 @RequestBody ProductViewDto productViewDto) {

        assert (productViewDto != null);

        productFacadeService.updateProduct(productViewDto);

        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable(value = "id") Long id) {
        productFacadeService.deleteProduct(id);

        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

}
