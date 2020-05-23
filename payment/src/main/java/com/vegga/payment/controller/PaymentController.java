package com.vegga.payment.controller;

import com.vegga.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

  @Autowired private PaymentService paymentService;

  @GetMapping(value = "/check")
  public String check() {
    return "Payment service is alive";
  }
}
