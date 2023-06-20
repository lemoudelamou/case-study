package com.db.gleis.response;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }


}
