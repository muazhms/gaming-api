package com.hms.gamingapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FileUploadResponse {
    private String status;
    private String fileName;
}
