package com.arul.Infosys.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_details")
public class UserDetails {

    @Id
    @Column(name = "email_id")
    private String emailId;

    @Column(name = "phone_number")
    private Long phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_role", nullable = false)
    private String role;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

    // NO GETTERS OR SETTERS AS YOU REQUESTED
}
