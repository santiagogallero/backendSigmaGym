package com.sigma.gym.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseData<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public static <T> ResponseData<T> ok(T data) {
        return ResponseData.<T>builder()
                .success(true)
                .code("OK")
                .data(data)
                .build();
    }

    public static <T> ResponseData<T> ok(String message, T data) {
        return ResponseData.<T>builder()
                .success(true)
                .code("OK")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseData<T> error(String code, String message) {
        return ResponseData.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }

    public static <T> ResponseData<T> error(String message) {
        return ResponseData.<T>builder()
                .success(false)
                .code("ERROR")
                .message(message)
                .build();
    }
}

