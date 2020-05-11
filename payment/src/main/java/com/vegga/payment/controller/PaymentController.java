package com.vegga.payment.controller;

import com.vegga.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class PaymentController {

  @Autowired private PaymentService paymentService;

  @GetMapping(value = "/check")
  public String check() {
    return "Payment service is alive";
  }

  @PostMapping
  public String save(
      @RequestParam(value = "airlineReservationId") Long airlineReservationId,
      @RequestParam(value = "carReservationId") Long carReservationId,
      @RequestParam(value = "hotelReservationId") Long hotelReservationId,
      @RequestParam(value = "clientId") Long clientId,
      @RequestParam(value = "paymentDate") @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
          LocalDateTime paymentDate,
      @RequestParam(value = "paymentExpiryDate") @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
          LocalDateTime paymentExpiryDate,
      @RequestParam(value = "creationDate") @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
          LocalDateTime creationDate) {
    paymentService.save(
        airlineReservationId,
        carReservationId,
        hotelReservationId,
        clientId,
        paymentDate,
        paymentExpiryDate,
        creationDate);
    return "Saved";
  }

  @DeleteMapping
  public String abortSaving(@RequestParam(value = "id") Long id) {
    paymentService.abortSaving(id);
    return "Aborted";
  }
}
