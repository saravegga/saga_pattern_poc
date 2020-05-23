package com.vegga.airline.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegga.airline.dto.BookingInput;
import com.vegga.airline.dto.MessageOutput;
import com.vegga.airline.entity.AirlineReservation;
import com.vegga.airline.repository.AirlineReservationRepository;
import com.vegga.airline.validator.BookingValidator;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirlineService {

  @Autowired private AirlineReservationRepository airlineReservationRepository;

  public AirlineService(AirlineReservationRepository airlineReservationRepository) {
    this.airlineReservationRepository = airlineReservationRepository;
  }

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "airline.booking.validate.rpc.requests"),
              exchange = @Exchange(value = "airline.booking.rpc"),
              key = "airline.booking.validate"))
  public String validateInput(String input) throws JsonProcessingException {

    BookingInput bookingInput = new ObjectMapper().readValue(input, BookingInput.class);
    List<String> errors = BookingValidator.validate(bookingInput);

    return new ObjectMapper()
        .writeValueAsString(new MessageOutput(bookingInput.getTransactionalId(), null, errors));
  }

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "airline.booking.rpc.requests"),
              exchange = @Exchange(value = "airline.booking.rpc"),
              key = "airline.booking"))
  public String save(String input) throws JsonProcessingException {

    BookingInput bookingInput = new ObjectMapper().readValue(input, BookingInput.class);
    AirlineReservation entity = new AirlineReservation();
    BeanUtils.copyProperties(bookingInput, entity);

    AirlineReservation airlineReservation = airlineReservationRepository.save(entity);
    return new ObjectMapper()
        .writeValueAsString(
            new MessageOutput(bookingInput.getTransactionalId(), airlineReservation.getId(), null));
  }

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "abort.airline.booking.rpc.requests"),
              exchange = @Exchange(value = "airline.booking.rpc"),
              key = "abort.airline.booking"))
  public String abortSaving(String input) throws JsonProcessingException {

    BookingInput bookingInput = new ObjectMapper().readValue(input, BookingInput.class);
    airlineReservationRepository.delete(airlineReservationRepository.getOne(bookingInput.getId()));

    return new ObjectMapper()
        .writeValueAsString(new MessageOutput(bookingInput.getTransactionalId(), null, null));
  }
}
