package com.vegga.car.controller;

import com.vegga.car.dto.BookingInput;
import com.vegga.car.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CarController {

  @Autowired private CarService carService;

  @GetMapping(value = "/check")
  public String check() {
    return "Car service is alive";
  }

  @PostMapping(value = "/validate")
  public String validate(@RequestBody BookingInput input) {
    try {
      carService.validateInput(input);
      return "Ok";
    } catch (IllegalArgumentException ex) {
      return "Failed";
    }
  }

  @PostMapping
  public String save(@RequestBody BookingInput input) {
    carService.save(input);
    return "Saved";
  }

  @DeleteMapping
  public String abortSaving(@RequestParam(value = "id") Long id) {
    carService.abortSaving(id);
    return "Aborted";
  }
}
