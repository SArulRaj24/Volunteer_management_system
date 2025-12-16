package com.arul.Infosys.model;

import com.arul.Infosys.model.key.RegistrationKey;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "registration_details")
@IdClass(RegistrationKey.class)
public class RegistrationDetails {

    /* ============================
       COMPOSITE PRIMARY KEY
       ============================ */

    @Id
    @Column(name = "event_id")
    private Long eventId;   // ✔ MUST match IdClass field name

    @Id
    @Column(name = "volunteer_id")
    private String volunteerId;  // ✔ MUST match IdClass field name

    /* ============================
       OTHER COLUMNS
       ============================ */

    @Column(nullable = false)
    private String status; // REGISTERED / WITHDRAWN

    @Column(nullable = false)
    private Boolean checkIn;

    private Integer rating;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    /* ============================
       RELATIONSHIPS (OPTIONAL)
       ============================ */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private EventDetails event;

    public RegistrationDetails() {}

    // getters & setters
}
