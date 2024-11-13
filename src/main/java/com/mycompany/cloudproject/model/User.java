package com.mycompany.cloudproject.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name="User")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "first_name")
    private String firstName;


    @Column(name = "last_name")
    private String lastName;

    private String password;

    @Column(unique = true)
    private String email;


    @Column(name = "account_created", updatable = false)
    private LocalDateTime accountCreated;

    @Column(name = "account_updated")
    private LocalDateTime accountUpdated;

    @Column(name="user_active")
    private boolean active;


    public User() {
        this.id = UUID.randomUUID().toString();
        this.accountCreated = LocalDateTime.now();
        this.accountUpdated = LocalDateTime.now();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getAccountCreated() {
        return accountCreated;
    }

    public LocalDateTime getAccountUpdated() {
        return accountUpdated;
    }

    public void setAccountUpdated(LocalDateTime accountUpdated) {
        this.accountUpdated = accountUpdated;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public boolean isActive() {
        return active;
    }
    


}
