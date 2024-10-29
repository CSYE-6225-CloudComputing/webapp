package com.mycompany.cloudproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class  UserDTO {

    private String id;


    @NotBlank(message = "first name should not be blank")
    @Size(max = 255, message = "length of the first name excedded")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only letters")
    @JsonProperty("first_name")
    private String firstName;

    @Size(max = 255, message = "length of the last name excedded")
    @NotBlank(message = "last name should not be blank")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only letters")
    @JsonProperty("last_name")
    private String lastName;

    @Email(message = "Email is not valid")
    @NotBlank(message = "email name should not be blank")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "password should not be blank")
    @Size(min = 6, max = 15, message = "password length is not satisfied")
    private String password;


    @JsonProperty("account_created")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime accountCreated;


    @JsonProperty("account_updated")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime accountUpdated;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //	@JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String userId) {
        id = userId;
    }

    @JsonProperty
    public LocalDateTime getAccountCreated() {
        return accountCreated;
    }

    @JsonProperty
    public LocalDateTime getAccountUpdated() {
        return accountUpdated;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password.trim();
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    public void setAccountCreated(LocalDateTime accountCreated) {
        this.accountCreated = accountCreated;
    }

    @JsonIgnore
    public void setAccountUpdated(LocalDateTime accountUpdated) {
        this.accountUpdated = accountUpdated;
    }
}
