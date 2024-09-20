package com.library.libraryapp.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorDetails {
    private LocalDateTime timeStamp;

    private String message;

    private String path;

    private String errorCode;
}
