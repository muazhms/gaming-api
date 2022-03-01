package com.hms.gamingapi.model;

import lombok.Data;

@Data
public class PageApiResponse<T> {
    private int totalPages;
    private int pageNo;
    private long totalElements;
    private T payload;

    public PageApiResponse(T payload) {
        this.payload = payload;
    }
}
