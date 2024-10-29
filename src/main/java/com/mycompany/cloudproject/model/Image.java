package com.mycompany.cloudproject.model;


import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "url")
    private String url;

    @Column(name = "upload_date",updatable = false)
    private LocalDate uploadDate;

    @Column(name = "user_id", nullable = false)
    private String userId; 

    @Column
    private String contentType;

    // Constructors
    public Image() {
        this.uploadDate = LocalDate.now();
    }

    public Image(String fileName, String url, LocalDate uploadDate, String userId) {
        this.fileName = fileName;
        this.url = url;
        this.uploadDate = LocalDate.now();
        this.userId = userId; 
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    
}
