package com.vegga.trip.controller;

import com.vegga.trip.dto.CarBookingInput;
import com.vegga.trip.queue.QueueProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TripController {

  @Autowired QueueProducer queueProducer;

  @GetMapping(value = "/check")
  public String check() {
    return "Trip service is alive";
  }

  @PostMapping
  public String createBooking(@RequestBody CarBookingInput input) throws Exception {
    queueProducer.produce(input);
    return "Message posted";
  }
}
