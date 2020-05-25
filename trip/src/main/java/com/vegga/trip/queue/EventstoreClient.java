package com.vegga.trip.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegga.trip.dto.BaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventstoreClient {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired private RabbitTemplate template;

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public BaseDTO getEventLog(BaseDTO input) throws JsonProcessingException {
    logger.info("[x] Requesting event log retrieve({})", input);
    String tmp =
        (String)
            template.convertSendAndReceive(
                "eventstore.get", "eventstore", new ObjectMapper().writeValueAsString(input));
    BaseDTO response = new ObjectMapper().readValue(tmp, BaseDTO.class);
    logger.info("[.] Got '{}'", response);
    return response;
  }

  @Scheduled(fixedDelay = 1000, initialDelay = 500)
  public void finishEventStatus(BaseDTO input) throws JsonProcessingException {
    logger.info("[x] Requested finish event status({})", input);
    template.convertAndSend(
        "eventstore.update", "eventstore", new ObjectMapper().writeValueAsString(input));
  }
}
