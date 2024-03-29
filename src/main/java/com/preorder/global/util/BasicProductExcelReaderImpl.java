package com.preorder.global.util;

import com.preorder.dto.domaindto.ProductDto;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Component
public class BasicProductExcelReaderImpl implements ProductExcelReader {
    @Override
    public List<ProductDto> getProductListFromExcel(MultipartFile file) {

        assert (file != null);
        List<ProductDto> productDtoList = new ArrayList<>();
        try (
                InputStream inputStream = file.getInputStream();
                Workbook workbook = WorkbookFactory.create(inputStream);
        ) {

            Sheet sheet = workbook.getSheetAt(0);
            int startIndex = 1;
            int lastIndex = sheet.getLastRowNum();

            for (int i = startIndex; i <= lastIndex; i++) {
                Row row = sheet.getRow(i);

                if (getCellValue(row.getCell(0)).isEmpty()) {
                    break;
                }
                ProductDto productDto = ProductDto.builder()
                        .name(row.getCell(0).getStringCellValue())
                        .category(row.getCell(1).getStringCellValue())
                        .price((int) row.getCell(2).getNumericCellValue())
                        .build();
                productDtoList.add(productDto);
            }


        } catch (IOException e) {
           throw new IllegalArgumentException(e.getMessage());
        }

        return productDtoList;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return ""; // 셀이 비어있을 경우 빈 문자열 반환
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return ""; // 다른 타입의 셀은 빈 문자열 반환
        }
    }
}
