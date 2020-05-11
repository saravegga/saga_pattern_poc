package com.vegga.car.controller;

import com.vegga.car.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class CarController {

  @Autowired private CarService carService;

  @GetMapping(value = "/check")
  public String check() {
    return "Car service is alive";
  }

  @PostMapping
  public String save(
      @RequestParam(value = "carId") Long carId,
      @RequestParam(value = "clientId") Long clientId,
      @RequestParam(value = "startDate") @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
          LocalDateTime startDate,
      @RequestParam(value = "endDate") @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
          LocalDateTime endDate) {
    carService.save(carId, clientId, startDate, endDate);
    return "Saved";
  }

  @DeleteMapping
  public String abortSaving(@RequestParam(value = "id") Long id) {
    carService.abortSaving(id);
    return "Aborted";
  }
}
