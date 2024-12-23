package com.mycompany.cloudproject.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageResponseDTO {

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("id")
    private String id;

    @JsonProperty("url")
    private String url;

    @JsonProperty("upload_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate uploadDate;
    
    @JsonProperty("user_id")
    private String userId;

    // Constructor
    public ImageResponseDTO(String fileName, String id, String url, LocalDate localDate, String userId) {
        this.fileName = fileName;
        this.id = id;
        this.url = url;
        this.userId = userId;
        this.uploadDate = localDate;
    }

    public ImageResponseDTO() {
        
    }


    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}


