package com.vegga.trip.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegga.trip.dto.AbortDTO;
import com.vegga.trip.dto.AirlineBookingInput;
import com.vegga.trip.dto.BaseDTO;
import com.vegga.trip.dto.MessageOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AirlineBookingRPCClient {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired private RabbitTemplate template;

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public MessageOutput validateInput(BaseDTO<AirlineBookingInput> input)
      throws JsonProcessingException {
    logger.info("[x] Requesting airline booking validate({})", input);
    String tmp =
        (String)
            template.convertSendAndReceive(
                "airline.booking.rpc",
                "airline.booking.validate",
                new ObjectMapper().writeValueAsString(input));
    MessageOutput response = new ObjectMapper().readValue(tmp, MessageOutput.class);
    logger.info("[.] Got '{}'", response);
    return response;
  }

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public MessageOutput save(BaseDTO<AirlineBookingInput> input) throws JsonProcessingException {
    logger.info("[x] Requesting airline booking({})", input);
    String tmp =
        (String)
            template.convertSendAndReceive(
                "airline.booking.rpc",
                "airline.booking",
                new ObjectMapper().writeValueAsString(input));
    MessageOutput response = new ObjectMapper().readValue(tmp, MessageOutput.class);
    logger.info("[.] Got '{}'", response);
    return response;
  }

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public void abort(AbortDTO<AirlineBookingInput> input) throws JsonProcessingException {
    logger.info("[x] Requested abort airline booking({})", input);
    template.convertAndSend(
        "airline.booking.rpc",
        "abort.airline.booking",
        new ObjectMapper().writeValueAsString(input));
  }
}
