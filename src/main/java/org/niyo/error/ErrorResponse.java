package org.niyo.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    int code;
    String error;

    public ErrorResponse(int code, String error) {
        this.code = code;
        this.error = error;
    }
}

