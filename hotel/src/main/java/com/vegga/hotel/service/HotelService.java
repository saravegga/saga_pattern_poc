package com.vegga.hotel.service;

import com.vegga.hotel.entity.HotelReservation;
import com.vegga.hotel.repository.HotelReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HotelService {

  @Autowired private HotelReservationRepository hotelReservationRepository;

  public HotelService(HotelReservationRepository hotelReservationRepository) {
    this.hotelReservationRepository = hotelReservationRepository;
  }

  public void save(Long hotelId, Long roomId, Long clientId, LocalDateTime startDate, LocalDateTime endDate) {

    HotelReservation reservation = new HotelReservation();
    reservation.setHotelId(hotelId);
    reservation.setRoomId(roomId);
    reservation.setClientId(clientId);
    reservation.setStartDate(startDate);
    reservation.setEndDate(endDate);

    hotelReservationRepository.save(reservation);
  }

  public void abortSaving(Long id) {

    hotelReservationRepository.delete(hotelReservationRepository.getOne(id));
  }
}
