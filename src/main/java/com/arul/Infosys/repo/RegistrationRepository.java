package com.arul.Infosys.repo;

import com.arul.Infosys.model.RegistrationDetails;
import com.arul.Infosys.model.key.RegistrationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository
        extends JpaRepository<RegistrationDetails, RegistrationKey> {


    List<RegistrationDetails> findByEventId(Long eventId);

    List<RegistrationDetails> findByVolunteerId(Long volunteerId);

    boolean existsByEventIdAndVolunteerId(Long eventId, Long volunteerId);
}
