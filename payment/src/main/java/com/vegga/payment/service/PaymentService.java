package com.vegga.payment.service;

import com.vegga.payment.entity.Payment;
import com.vegga.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

  @Autowired private PaymentRepository paymentRepository;

  public PaymentService(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;
  }

  public void save(
      Long airlineReservationId,
      Long carReservationId,
      Long hotelReservationId,
      Long clientId,
      LocalDateTime paymentDate,
      LocalDateTime paymentExpiryDate,
      LocalDateTime creationDate) {

    Payment payment = new Payment();
    payment.setAirlineReservationId(airlineReservationId);
    payment.setCarReservationId(carReservationId);
    payment.setHotelReservationId(hotelReservationId);
    payment.setClientId(clientId);
    payment.setPaymentDate(paymentDate);
    payment.setPaymentExpiryDate(paymentExpiryDate);
    payment.setCreationDate(creationDate);

    paymentRepository.save(payment);
  }

  public void abortSaving(Long id) {

    paymentRepository.delete(paymentRepository.getOne(id));
  }
}
