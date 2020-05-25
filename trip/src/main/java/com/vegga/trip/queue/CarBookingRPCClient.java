package com.vegga.trip.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegga.trip.dto.AbortDTO;
import com.vegga.trip.dto.BaseDTO;
import com.vegga.trip.dto.CarBookingInput;
import com.vegga.trip.dto.MessageOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CarBookingRPCClient {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired private RabbitTemplate template;

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public MessageOutput validateInput(BaseDTO<CarBookingInput> input)
      throws JsonProcessingException {
    logger.info("[x] Requesting car booking validate({})", input);
    String tmp =
        (String)
            template.convertSendAndReceive(
                "car.booking.rpc",
                "car.booking.validate",
                new ObjectMapper().writeValueAsString(input));
    MessageOutput response = new ObjectMapper().readValue(tmp, MessageOutput.class);
    logger.info("[.] Got '{}'", response);
    return response;
  }

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public MessageOutput save(BaseDTO<CarBookingInput> input) throws JsonProcessingException {
    logger.info("[x] Requesting car booking({})", input);
    String tmp =
        (String)
            template.convertSendAndReceive(
                "car.booking.rpc", "car.booking", new ObjectMapper().writeValueAsString(input));
    MessageOutput response = new ObjectMapper().readValue(tmp, MessageOutput.class);
    logger.info("[.] Got '{}'", response);
    return response;
  }

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public void abort(AbortDTO<CarBookingInput> input) throws JsonProcessingException {
    logger.info("[x] Requested abort car booking({})", input);
    template.convertAndSend(
        "car.booking.rpc", "abort.car.booking", new ObjectMapper().writeValueAsString(input));
  }
}
