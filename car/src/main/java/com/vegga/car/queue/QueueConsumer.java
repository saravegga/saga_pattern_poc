package com.vegga.car.queue;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegga.car.dto.BookingInput;
import com.vegga.car.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {

  @Autowired CarService carService;

  protected Logger logger = LoggerFactory.getLogger(getClass());

  public void receiveMessage(String message) {
    logger.info("Received (String) " + message);
    processMessage(message);
  }

  public void receiveMessage(byte[] message) {
    String strMessage = new String(message);
    logger.info("Received (No String) " + strMessage);
    processMessage(strMessage);
  }

  private void processMessage(String message) {
    try {
      BookingInput input = new ObjectMapper().readValue(message, BookingInput.class);
      carService.save(input);
    } catch (JsonParseException e) {
      logger.warn("Bad JSON in message: " + message);
    } catch (JsonMappingException e) {
      logger.warn("cannot map JSON to NotificationRequest: " + message);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
}
