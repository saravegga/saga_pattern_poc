package com.vegga.hotel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegga.hotel.dto.AbortDTO;
import com.vegga.hotel.dto.BaseDTO;
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

    BaseDTO<BookingInput> bookingInput =
        new ObjectMapper().readValue(input, new TypeReference<>() {});
    List<String> errors = BookingValidator.validate(bookingInput.getEntity());

    return new ObjectMapper()
        .writeValueAsString(new MessageOutput(bookingInput.getTransactionalId(), null, errors));
  }

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "hotel.booking.rpc.requests"),
              exchange = @Exchange(value = "hotel.booking.rpc"),
              key = "hotel.booking"),
      priority = "10")
  public String save(String input) throws JsonProcessingException {

    BaseDTO<BookingInput> bookingInput =
        new ObjectMapper().readValue(input, new TypeReference<>() {});
    HotelReservation entity = new HotelReservation();
    BeanUtils.copyProperties(bookingInput.getEntity(), entity);

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
  public void abortSaving(String input) throws JsonProcessingException {

    AbortDTO<BookingInput> bookingInput =
        new ObjectMapper().readValue(input, new TypeReference<>() {});

    if (bookingInput.getBefore() == null
        || bookingInput.getBefore().getEntity() == null
        || bookingInput.getBefore().getEntity().getId() == null
        || bookingInput.getBefore().getEntity().getId() == 0L) {

      hotelReservationRepository.delete(
          hotelReservationRepository.getOne(bookingInput.getThen().getEntity().getId()));

    } else {
      HotelReservation entity = new HotelReservation();
      BeanUtils.copyProperties(bookingInput.getBefore().getEntity(), entity);
      hotelReservationRepository.save(entity);
    }
  }
}
