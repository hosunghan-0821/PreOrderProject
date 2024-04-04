package com.preorder.global.error;


import com.preorder.global.error.dto.ErrorResponseDto;
import com.preorder.global.error.exception.BusinessLogicException;
import com.preorder.global.error.exception.InvalidArgumentException;
import com.preorder.global.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionController {


    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleRunTimeException(RuntimeException ex, NativeWebRequest request) {

        if (ex instanceof NotFoundException) {
            NotFoundException notFoundException = (NotFoundException) ex;

            return new ResponseEntity<>(
                    ErrorResponseDto.builder().ErrorCode(notFoundException.getErrorCode().getErrorCode()).Message(notFoundException.getErrorCode().getDefaultMessage()).build(),
                    HttpStatus.BAD_REQUEST
            );
        } else if (ex instanceof InvalidArgumentException) {
            InvalidArgumentException invalidArgumentException = (InvalidArgumentException) ex;

            return new ResponseEntity<>(
                    ErrorResponseDto.builder().ErrorCode(invalidArgumentException.getErrorCode().getErrorCode()).Message(invalidArgumentException.getErrorCode().getDefaultMessage()).build(),
                    HttpStatus.BAD_REQUEST
            );
        } else if(ex instanceof BusinessLogicException) {
            BusinessLogicException businessLogicException = (BusinessLogicException) ex;

            return new ResponseEntity<>(
                    ErrorResponseDto.builder().ErrorCode(businessLogicException.getErrorCode().getErrorCode()).Message(businessLogicException.getErrorCode().getDefaultMessage()).build(),
                    HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                ErrorResponseDto.builder().Message(ex.getMessage()).build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
