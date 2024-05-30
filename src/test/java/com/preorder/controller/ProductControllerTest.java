package com.preorder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.preorder.dto.viewdto.ProductViewDto;
import com.preorder.global.util.FileUtil;
import com.preorder.service.facade.ProductFacadeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {


    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductFacadeService productFacadeService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {


        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    private ProductViewDto productViewDto() {
        return ProductViewDto.builder()
                .name("마이넘버원")
                .price(10000)
                .category("페스츄리")
                .build();
    }

    private ProductViewDto productViewDtoForUpdate() {
        return ProductViewDto.builder()
                .id(1L)
                .name("마이넘버원")
                .price(10000)
                .category("페스츄리")
                .build();
    }

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        return new MockMultipartFile("file", fileName + "." + contentType, contentType, fileInputStream);
    }


    @Test
    @DisplayName("[성공] : 상품등록 API 테스트")
    void registerProduct() throws Exception {
        //given
        ProductViewDto productViewDto = this.productViewDto();
        doNothing().when(productFacadeService).registerProduct(any());
        String request = objectMapper.writeValueAsString(productViewDto);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("[실패] : 상품등록 API 테스트")
    void registerProductFailAssert() throws Exception {
        assertThrows(AssertionError.class, () -> productController.registerProduct(null));
    }

    @Test
    @DisplayName("[실패] : 상품등록 API 테스트 - 유효성검사 실패")
    void registerProductFail() throws Exception {
        //given
        ProductViewDto productViewDto = this.productViewDtoForUpdate();

        String request = objectMapper.writeValueAsString(productViewDto);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("[성공] : 상품 다중등록 API 테스트")
    void bulkRegisterProduct() throws Exception {
        //given
        String fileName = "test";
        String contentType = "xlsx";
        String filePath = "src/test/resources/test.xlsx";

        doNothing().when(productFacadeService).bulkRegisterProduct(any());

        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);
        Assertions.assertThat(FileUtil.isValidExcelFile(mockMultipartFile)).isEqualTo(true);

        //when
        ResultActions resultActions = mockMvc.perform
                (
                        multipart("/api/products/bulk")
                                .file(mockMultipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andDo(print());
        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    @DisplayName("[실패] : 상품 다중등록 API 테스트")
    void bulkRegisterProductFailAssert() throws Exception {
        assertThrows(AssertionError.class, () -> productController.bulkRegisterProduct(null));
    }

    @Test
    @DisplayName("[실패] : 상품 다중등록 API 테스트 - 액셀파일 아님")
    void bulkRegisterProductFail() throws Exception {
        //given
        String fileName = "test";
        String contentType = "xml";
        String filePath = "src/test/resources/ehcache.xml";


        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);
        Assertions.assertThat(FileUtil.isValidExcelFile(mockMultipartFile)).isEqualTo(false);

        //when
        ResultActions resultActions = mockMvc.perform
                (
                        multipart("/api/products/bulk")
                                .file(mockMultipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andDo(print());
        //then
        resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError());

    }

    @Test
    @DisplayName("[성공] : 상품 다중조회 API 테스트")
    void getProductList() throws Exception {
        //given
        doReturn(new PageImpl(Arrays.asList(this.productViewDto()))).when(productFacadeService).getProductList(any());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(jsonPath("$.content").exists());

    }

    @Test
    @DisplayName("[성공] : 상품 단일조회 API 테스트")
    void getProductDetail() throws Exception {

        //given
        doReturn(this.productViewDto()).when(productFacadeService).getProductDetail(any());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

    }

    @Test
    @DisplayName("[실패] : 상품 단일조회 API 테스트 - assert")
    void getProductDetailFailAssert() throws Exception {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> productController.getProductDetail(0L));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> productController.getProductDetail(null));
    }

    @Test
    @DisplayName("[성공] : 상품 업데이트 API 테스트")
    void updateProduct() throws Exception {

        //given
        ProductViewDto productViewDto = this.productViewDtoForUpdate();
        doNothing().when(productFacadeService).updateProduct(any());
        String content = objectMapper.writeValueAsString(productViewDto);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        resultActions
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @DisplayName("[실패] : 상품 업데이트 API 테스트 - assert Fail")
    void updateProductFailAssert() throws Exception {
        assertThrows(AssertionError.class, () -> productController.updateProduct(null));
    }

    @Test
    @DisplayName("[성공] : 상품 제거 API 테스트")
    void deleteProduct() throws Exception {

        //given
        doNothing().when(productFacadeService).deleteProduct(any());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)

        );

        //then
        resultActions
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }
}