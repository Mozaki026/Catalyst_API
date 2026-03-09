package com.meshwarcoders.catalyst.api.dto.response;

import lombok.Builder;

@Builder
public record ApiResponse<T>(
        boolean success,
        String message,
        T data) {

    public ApiResponse(boolean success, String message) {
        this(success, message, null);
    }
}