package com.vegga.airline.repository;

import com.vegga.airline.entity.AirlineReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirlineReservationRepository extends JpaRepository<AirlineReservation, Long> {}
