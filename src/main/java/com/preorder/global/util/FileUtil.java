package com.preorder.global.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class FileUtil {


    public static boolean isValidExcelFile(MultipartFile file) {

        assert (file != null);

        try (InputStream inputStream = file.getInputStream()) {

            // 첫 번째 바이트 읽기
            inputStream.mark(1);
            int firstByte = inputStream.read();


            // 엑셀 파일 시그니처 확인
            if (firstByte == 0xD0 && inputStream.read() == 0xCF && inputStream.read() == 0x11
                    && inputStream.read() == 0xE0 && inputStream.read() == 0xA1
                    && inputStream.read() == 0xB1 && inputStream.read() == 0x1A
                    && inputStream.read() == 0xE1) {
                // XLS 파일 시그니처
                return true;
            } else if (firstByte == 0x50 && inputStream.read() == 0x4B && inputStream.read() == 0x03
                    && inputStream.read() == 0x04) {
                // XLSX 파일 시그니처
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("file read error");
        }

        return false;
    }


    //생성자 사용금지
    private FileUtil() {

    }
}
