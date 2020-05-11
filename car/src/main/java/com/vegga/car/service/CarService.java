package com.vegga.car.service;

import com.vegga.car.entity.CarReservation;
import com.vegga.car.repository.CarReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CarService {

  @Autowired private CarReservationRepository carReservationRepository;

  public CarService(CarReservationRepository carReservationRepository) {
    this.carReservationRepository = carReservationRepository;
  }

  public void save(Long carId, Long clientId, LocalDateTime startDate, LocalDateTime endDate) {

    CarReservation reservation = new CarReservation();
    reservation.setCarId(carId);
    reservation.setClientId(clientId);
    reservation.setStartDate(startDate);
    reservation.setEndDate(endDate);

    carReservationRepository.save(reservation);
  }

  public void abortSaving(Long id) {

    carReservationRepository.delete(carReservationRepository.getOne(id));
  }
}
