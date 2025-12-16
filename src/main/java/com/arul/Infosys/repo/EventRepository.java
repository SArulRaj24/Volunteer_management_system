package com.arul.Infosys.repo;

import com.arul.Infosys.model.EventDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<EventDetails, Long> {

    List<EventDetails> findByRegistrationAllowed(Boolean status);
}
