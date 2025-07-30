package com.sigma.gym.controllers.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseData<T> {
    private boolean success;
    private T data;
    private String error;

   
    public static <T> ResponseData<T> success(T data) {
        return new ResponseData<>(true, data, null);
    }

    public static <T> ResponseData<T> error(String errorMessage) {
        return new ResponseData<>(false, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
