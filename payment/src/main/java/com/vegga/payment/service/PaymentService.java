package com.vegga.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegga.payment.dto.BaseDTO;
import com.vegga.payment.dto.BookingInput;
import com.vegga.payment.dto.MessageOutput;
import com.vegga.payment.entity.Payment;
import com.vegga.payment.repository.PaymentRepository;
import com.vegga.payment.validator.BookingValidator;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

  @Autowired private PaymentRepository paymentRepository;

  public PaymentService(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;
  }

  @RabbitListener(
      bindings =
          @QueueBinding(
              value = @Queue(value = "payment.validate.rpc.requests"),
              exchange = @Exchange(value = "payment.rpc"),
              key = "payment.validate"))
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
              value = @Queue(value = "payment.rpc.requests"),
              exchange = @Exchange(value = "payment.rpc"),
              key = "payment"),
      priority = "10")
  public String save(String input) throws JsonProcessingException {

    BaseDTO<BookingInput> bookingInput =
        new ObjectMapper().readValue(input, new TypeReference<>() {});
    Payment entity = new Payment();
    BeanUtils.copyProperties(bookingInput.getEntity(), entity);

    if (bookingInput.getEntity().getClientId() == 34L) {

      return new ObjectMapper()
              .writeValueAsString(
                      new MessageOutput(bookingInput.getTransactionalId(), null, List.of("This client does not have balance.")));
    }

    Payment payment = paymentRepository.save(entity);
    return new ObjectMapper()
        .writeValueAsString(
            new MessageOutput(bookingInput.getTransactionalId(), payment.getId(), null));
  }
}
