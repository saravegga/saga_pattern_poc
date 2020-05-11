package com.vegga.hotel.repository;

import com.vegga.hotel.entity.HotelReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelReservationRepository extends JpaRepository<HotelReservation, Long> {}
