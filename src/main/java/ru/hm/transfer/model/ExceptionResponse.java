package ru.hm.transfer.model;

import lombok.Data;

@Data
public class ExceptionResponse {
    private static long idGenerator = 0;

    private final String message;
    private long id;

    public ExceptionResponse(String message) {
        this.message = message;
        this.id=idGenerator++;
    }
}
