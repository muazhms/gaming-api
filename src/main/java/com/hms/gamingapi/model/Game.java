package com.hms.gamingapi.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "game")
public class Game {
    @Id
    private String id;
    private String name;
    private String description;
    private String developerName;
    private String developerId;
    @CreatedDate
    private LocalDateTime createdDateTime;
    @LastModifiedDate
    private LocalDateTime lastUpdatedDateTime;
    private String poster;
    private String status;
    private boolean online;
    private boolean downloadable;
    private Long usage;
    private String gameFile;
}
