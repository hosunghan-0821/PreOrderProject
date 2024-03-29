package com.preorder.global.util;

import com.preorder.dto.domaindto.ProductDomainDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductExcelReader {


    List<ProductDomainDto> getProductListFromExcel(MultipartFile file);
}
