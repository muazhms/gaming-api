package com.hms.gamingapi.model;

import lombok.Data;

@Data
public class SearchRequest {
    private String status;
    private boolean online;
    private boolean downloadable;
}
