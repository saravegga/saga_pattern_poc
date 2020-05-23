package com.vegga.car.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegga.car.dto.BookingInput;
import com.vegga.car.dto.MessageOutput;
import com.vegga.car.entity.CarReservation;
import com.vegga.car.repository.CarReservationRepository;
import com.vegga.car.validator.BookingValidator;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

  @Autowired private CarReservationRepository carReservationRepository;

  public CarService(CarReservationRepository carReservationRepository) {
    this.carReservationRepository = carReservationRepository;
  }

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "car.booking.validate.rpc.requests"),
              exchange = @Exchange(value = "car.booking.rpc"),
              key = "car.booking.validate"))
  public String validateInput(String input) throws JsonProcessingException {

    BookingInput bookingInput = new ObjectMapper().readValue(input, BookingInput.class);
    List<String> errors = BookingValidator.validate(bookingInput);

    return new ObjectMapper()
        .writeValueAsString(new MessageOutput(bookingInput.getTransactionalId(), null, errors));
  }

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "car.booking.rpc.requests"),
              exchange = @Exchange(value = "car.booking.rpc"),
              key = "car.booking"))
  public String save(String input) throws JsonProcessingException {

    BookingInput bookingInput = new ObjectMapper().readValue(input, BookingInput.class);
    CarReservation entity = new CarReservation();
    BeanUtils.copyProperties(bookingInput, entity);

    CarReservation carReservation = carReservationRepository.save(entity);
    return new ObjectMapper()
        .writeValueAsString(
            new MessageOutput(bookingInput.getTransactionalId(), carReservation.getId(), null));
  }

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "abort.car.booking.rpc.requests"),
              exchange = @Exchange(value = "car.booking.rpc"),
              key = "abort.car.booking"))
  public String abortSaving(String input) throws JsonProcessingException {

    BookingInput bookingInput = new ObjectMapper().readValue(input, BookingInput.class);
    carReservationRepository.delete(carReservationRepository.getOne(bookingInput.getId()));

    return new ObjectMapper()
        .writeValueAsString(new MessageOutput(bookingInput.getTransactionalId(), null, null));
  }
}
