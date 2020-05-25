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
public class HotelBookingInput {

  private Long id;
  private Long hotelId;
  private Long roomId;
  private Long clientId;

  @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime startDate;

  @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime endDate;

  public static BaseDTO<HotelBookingInput> convert(HotelBookingInput input, UUID transactionalId) {
    return new BaseDTO<>(
        transactionalId, input.getId(), HotelBookingInput.class.getSimpleName(), input);
  }

  public static AbortDTO<HotelBookingInput> createAbortMessage(
          BaseDTO<HotelBookingInput> before, BaseDTO<HotelBookingInput> then, Long id) {
    then.setObjectId(id);
    then.getEntity().setId(id);
    return new AbortDTO<>(before, then);
  }
}
