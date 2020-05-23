package com.vegga.hotel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegga.hotel.dto.BookingInput;
import com.vegga.hotel.dto.MessageOutput;
import com.vegga.hotel.entity.HotelReservation;
import com.vegga.hotel.repository.HotelReservationRepository;
import com.vegga.hotel.validator.BookingValidator;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

  @Autowired private HotelReservationRepository hotelReservationRepository;

  public HotelService(HotelReservationRepository hotelReservationRepository) {
    this.hotelReservationRepository = hotelReservationRepository;
  }

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "hotel.booking.validate.rpc.requests"),
              exchange = @Exchange(value = "hotel.booking.rpc"),
              key = "hotel.booking.validate"))
  public String validateInput(String input) throws JsonProcessingException {

    BookingInput bookingInput = new ObjectMapper().readValue(input, BookingInput.class);
    List<String> errors = BookingValidator.validate(bookingInput);

    return new ObjectMapper()
        .writeValueAsString(new MessageOutput(bookingInput.getTransactionalId(), null, errors));
  }

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "hotel.booking.rpc.requests"),
              exchange = @Exchange(value = "hotel.booking.rpc"),
              key = "hotel.booking"))
  public String save(String input) throws JsonProcessingException {

    BookingInput bookingInput = new ObjectMapper().readValue(input, BookingInput.class);
    HotelReservation entity = new HotelReservation();
    BeanUtils.copyProperties(bookingInput, entity);

    HotelReservation hotelReservation = hotelReservationRepository.save(entity);
    return new ObjectMapper()
        .writeValueAsString(
            new MessageOutput(bookingInput.getTransactionalId(), hotelReservation.getId(), null));
  }

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "abort.hotel.booking.rpc.requests"),
              exchange = @Exchange(value = "hotel.booking.rpc"),
              key = "abort.hotel.booking"))
  public String abortSaving(String input) throws JsonProcessingException {

    BookingInput bookingInput = new ObjectMapper().readValue(input, BookingInput.class);
    hotelReservationRepository.delete(hotelReservationRepository.getOne(bookingInput.getId()));

    return new ObjectMapper()
        .writeValueAsString(new MessageOutput(bookingInput.getTransactionalId(), null, null));
  }
}
