package com.vegga.trip.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegga.trip.dto.HotelBookingInput;
import com.vegga.trip.dto.MessageOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HotelBookingRPCClient {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired private RabbitTemplate template;

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public MessageOutput validateInput(HotelBookingInput input) throws JsonProcessingException {
    logger.info("[x] Requesting hotel booking validate({})", input);
    String tmp =
        (String)
            template.convertSendAndReceive(
                "hotel.booking.rpc",
                "hotel.booking.validate",
                new ObjectMapper().writeValueAsString(input));
    MessageOutput response = new ObjectMapper().readValue(tmp, MessageOutput.class);
    logger.info("[.] Got '{}'", response);
    return response;
  }

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public MessageOutput save(HotelBookingInput input) throws JsonProcessingException {
    logger.info("[x] Requesting hotel booking({})", input);
    String tmp =
        (String)
            template.convertSendAndReceive(
                "hotel.booking.rpc", "hotel.booking", new ObjectMapper().writeValueAsString(input));
    MessageOutput response = new ObjectMapper().readValue(tmp, MessageOutput.class);
    logger.info("[.] Got '{}'", response);
    return response;
  }

  // rollback insert method

  // rollback update method

}
