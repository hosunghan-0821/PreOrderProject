package com.preorder.global.util;

import com.preorder.dto.domaindto.ProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductExcelReader {


    List<ProductDto> getProductListFromExcel(MultipartFile file);
}
