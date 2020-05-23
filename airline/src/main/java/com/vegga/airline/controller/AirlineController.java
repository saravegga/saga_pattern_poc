package com.vegga.airline.controller;

import com.vegga.airline.service.AirlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AirlineController {

  @Autowired private AirlineService airlineService;

  @GetMapping(value = "/check")
  public String check() {
    return "Airline service is alive";
  }
}
