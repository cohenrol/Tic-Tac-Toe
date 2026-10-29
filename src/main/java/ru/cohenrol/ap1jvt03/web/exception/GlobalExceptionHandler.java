package ru.cohenrol.ap1jvt03.web.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.cohenrol.ap1jvt03.web.model.response.ExceptionResponseDto;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionResponseDto> handleException(Throwable throwable) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionResponseDto responseBody = new ExceptionResponseDto(status.value(), throwable.getMessage());
        return new ResponseEntity<>(responseBody, buildHeaders(), status);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
