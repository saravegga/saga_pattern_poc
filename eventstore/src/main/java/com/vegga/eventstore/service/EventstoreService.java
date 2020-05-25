package com.vegga.eventstore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegga.eventstore.dto.BaseDTO;
import com.vegga.eventstore.entity.EventLog;
import com.vegga.eventstore.repository.EventLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class EventstoreService {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired EventLogRepository repository;

  @Autowired MongoTemplate template;

  @RabbitListener(
      bindings = {
        @QueueBinding(
            value = @Queue(value = "airline.booking.rpc.requests.log"),
            exchange = @Exchange(value = "airline.booking.rpc"),
            key = "airline.booking"),
        @QueueBinding(
            value = @Queue(value = "car.booking.rpc.requests.log"),
            exchange = @Exchange(value = "car.booking.rpc"),
            key = "car.booking"),
        @QueueBinding(
            value = @Queue(value = "hotel.booking.rpc.requests.log"),
            exchange = @Exchange(value = "hotel.booking.rpc"),
            key = "hotel.booking"),
        @QueueBinding(
            value = @Queue(value = "payment.rpc.requests.log"),
            exchange = @Exchange(value = "payment.rpc"),
            key = "payment")
      },
      priority = "0")
  public void savingEvents(String input) throws JsonProcessingException {

    BaseDTO dto = new ObjectMapper().readValue(input, BaseDTO.class);
    EventLog eventLog = new EventLog();
    BeanUtils.copyProperties(dto, eventLog);
    eventLog.setInsertionDate(LocalDateTime.now());
    eventLog.setFinished(false);

    repository.save(eventLog);
  }

  @RabbitListener(
      bindings = {
        @QueueBinding(
            value = @Queue(value = "eventstore.get.requests"),
            exchange = @Exchange(value = "eventstore.get"),
            key = "eventstore")
      })
  public String getEventLog(String input) throws JsonProcessingException {

    BaseDTO dto = new ObjectMapper().readValue(input, BaseDTO.class);

    Optional<EventLog> result =
        template
            .query(EventLog.class)
            .matching(
                query(where("finished").is(true).and("entityName").is(dto.getEntityName()))
                    .with(Sort.by(Sort.Direction.DESC, "insertionDate")))
            .first();

    BaseDTO response = new BaseDTO();

    if (result.isPresent()) {
      BeanUtils.copyProperties(result.get(), response);
    }

    return new ObjectMapper().writeValueAsString(response);
  }

  @RabbitListener(
      bindings = {
        @QueueBinding(
            value = @Queue(value = "eventstore.update.requests"),
            exchange = @Exchange(value = "eventstore.update"),
            key = "eventstore")
      })
  public void finishEventStatus(String input) throws JsonProcessingException {

    BaseDTO dto = new ObjectMapper().readValue(input, BaseDTO.class);

    EventLog eventLogExample = new EventLog();
    eventLogExample.setTransactionalId(dto.getTransactionalId());
    eventLogExample.setEntityName(dto.getEntityName());

    ExampleMatcher matcher = ExampleMatcher.matching();

    Example<EventLog> example = Example.of(eventLogExample, matcher);
    Optional<EventLog> eventLog = repository.findOne(example);

    eventLog.get().setObjectId(dto.getObjectId());
    eventLog.get().setFinished(true);

    repository.save(eventLog.get());
  }
}
