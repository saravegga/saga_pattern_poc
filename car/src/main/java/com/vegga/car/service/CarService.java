package com.vegga.car.service;

import com.vegga.car.dto.BookingInput;
import com.vegga.car.entity.CarReservation;
import com.vegga.car.repository.CarReservationRepository;
import com.vegga.car.validator.BookingValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {

  @Autowired private CarReservationRepository carReservationRepository;

  public CarService(CarReservationRepository carReservationRepository) {
    this.carReservationRepository = carReservationRepository;
  }

  public void validateInput(BookingInput input) {
    BookingValidator.validate(input);
  }

  public void save(BookingInput input) {
    CarReservation entity = new CarReservation();
    BeanUtils.copyProperties(input, entity);

    carReservationRepository.save(entity);
  }

  public void abortSaving(Long id) {
    carReservationRepository.delete(carReservationRepository.getOne(id));
  }
}
