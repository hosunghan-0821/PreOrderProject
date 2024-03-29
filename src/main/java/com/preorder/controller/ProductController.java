package com.preorder.controller;


import com.preorder.dto.viewdto.ProductViewDto;
import com.preorder.global.util.FileUtil;
import com.preorder.global.validation.ValidationMarker;
import com.preorder.service.facade.ProductFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.preorder.global.validation.ValidationMarker.OnCreate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ProductController {


    private final ProductFacadeService productFacadeService;

    @PostMapping("/products")
    public ResponseEntity<Boolean> registerProduct(@Validated({OnCreate.class})  // 경계 값 검증
                                                   @RequestBody ProductViewDto productViewDto) {

        assert (productViewDto != null);

        productFacadeService.registerProduct(productViewDto);

        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.CREATED);
    }

    @PostMapping("/products/bulk")
    public ResponseEntity<Boolean> bulkRegisterProduct(@RequestPart(value = "file", required = true) MultipartFile multipartFile) {

        assert (multipartFile != null);
        // 경계값 검증
        // 유효한 액셀파일 확인
        if (FileUtil.isValidExcelFile(multipartFile)) {

            productFacadeService.bulkRegisterProduct(multipartFile);
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);

        } else {
            log.error("file is not valid");
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }

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
