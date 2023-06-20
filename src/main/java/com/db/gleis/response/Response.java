package com.db.gleis.response;


import lombok.Data;

import java.util.List;

@Data
public class Response {
    private final List<String> sections;

    public Response(List<String> sections) {
        this.sections = sections;
    }


}
