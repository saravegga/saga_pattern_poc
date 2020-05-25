package com.vegga.trip.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentBookingInput {

  private Long id;
  private Long carReservationId;
  private Long airlineReservationId;
  private Long hotelReservationId;
  private Long clientId;

  @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime paymentDate;

  @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime paymentExpiryDate;

  @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime creationDate;

  public static BaseDTO<PaymentBookingInput> convert(
      PaymentBookingInput input, UUID transactionalId) {
    return new BaseDTO<>(
        transactionalId, input.getId(), PaymentBookingInput.class.getSimpleName(), input);
  }
}
