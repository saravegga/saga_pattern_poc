package com.vegga.trip.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegga.trip.dto.MessageOutput;
import com.vegga.trip.dto.PaymentBookingInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentRPCClient {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired private RabbitTemplate template;

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public MessageOutput validateInput(PaymentBookingInput input) throws JsonProcessingException {
    logger.info("[x] Requesting payment validate({})", input);
    String tmp =
        (String)
            template.convertSendAndReceive(
                "payment.rpc", "payment.validate", new ObjectMapper().writeValueAsString(input));
    MessageOutput response = new ObjectMapper().readValue(tmp, MessageOutput.class);
    logger.info("[.] Got '{}'", response);
    return response;
  }

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public MessageOutput save(PaymentBookingInput input) throws JsonProcessingException {
    logger.info("[x] Requesting payment({})", input);
    String tmp =
        (String)
            template.convertSendAndReceive(
                "payment.rpc", "payment", new ObjectMapper().writeValueAsString(input));
    MessageOutput response = new ObjectMapper().readValue(tmp, MessageOutput.class);
    logger.info("[.] Got '{}'", response);
    return response;
  }

  // rollback insert method

  // rollback update method

}
