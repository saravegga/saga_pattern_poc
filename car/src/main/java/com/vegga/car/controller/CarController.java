package com.vegga.car.controller;

import com.vegga.car.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarController {

  @Autowired private CarService carService;

  @GetMapping(value = "/check")
  public String check() {
    return "Car service is alive";
  }
}
