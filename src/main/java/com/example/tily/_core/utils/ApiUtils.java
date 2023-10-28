package com.example.tily._core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


public class ApiUtils {

    public static <T> ApiResult<T> success(T result) {
        return new ApiResult<>(true, 200, "ok", result);
    }

    public static ApiResult<?> error(String message, HttpStatus httpStatus) {
        return new ApiResult<>(false, httpStatus.value(),  message, null);
    }

    @Getter @Setter @AllArgsConstructor
    public static class ApiResult<T> {
        private final boolean success;
        private final int code;
        private final String message;
        private final T result;
    }

//    @Getter @Setter @AllArgsConstructor
//    public static class ApiError {
//        private final String message;
//        private final int status;
//    }
}
