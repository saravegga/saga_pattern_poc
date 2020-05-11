package com.vegga.car.repository;

import com.vegga.car.entity.CarReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarReservationRepository extends JpaRepository<CarReservation, Long> {}
