package com.vegga.airline.controller;

import com.vegga.airline.service.AirlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AirlineController {

  @Autowired private AirlineService airlineService;

  @GetMapping(value = "/check")
  public String check() {
    return "Airline service is alive";
  }

  @PostMapping
  public String save(
      @RequestParam(value = "flightId") Long flightId,
      @RequestParam(value = "clientId") Long clientId,
      @RequestParam(value = "seatId") Long seatId) {
    airlineService.save(flightId, clientId, seatId);
    return "Saved";
  }

  @DeleteMapping
  public String abortSaving(@RequestParam(value = "id") Long id) {
    airlineService.abortSaving(id);
    return "Aborted";
  }
}
