package com.example.carins.api;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder

public class ApiResponse <T> {
    private int statusCode;
    private boolean success;
    private String message;
    private T data;


    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();



    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .statusCode(200)
                .message("Success")
                .success(true)
                .data(data)
                .build();
    }




    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .statusCode(201)
                .message("Created")
                .success(true)
                .data(data)
                .build();
    }


    public static <T> ApiResponse<T> badRequest(String message) {
        return ApiResponse.<T>builder()
                .statusCode(400)
                .message(message)
                .success(false)
                .build();
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .statusCode(404)
                .message(message)
                .success(false)
                .build();
    }
}

