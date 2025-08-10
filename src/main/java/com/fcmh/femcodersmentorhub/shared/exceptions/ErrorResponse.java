package com.fcmh.femcodersmentorhub.shared.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String message;
    private int status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponse(ErrorCode errorCode, String message, HttpStatus httpStatus, String path) {
        this.errorCode = errorCode.name();
        this.message = message;
        this.status = httpStatus.value();
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
}
