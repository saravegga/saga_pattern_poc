package com.vegga.hotel.controller;

import com.vegga.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class HotelController {

  @Autowired
  private HotelService hotelService;

  @GetMapping(value = "/check")
  public String check() {
    return "Hotel service is alive";
  }

  @PostMapping
  public String save(
          @RequestParam(value = "hotelId") Long hotelId,
          @RequestParam(value = "roomId") Long roomId,
          @RequestParam(value = "clientId") Long clientId,
          @RequestParam(value = "startDate") @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
                  LocalDateTime startDate,
          @RequestParam(value = "endDate") @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
                  LocalDateTime endDate) {
    hotelService.save(hotelId, roomId, clientId, startDate, endDate);
    return "Saved";
  }

  @DeleteMapping
  public String abortSaving(@RequestParam(value = "id") Long id) {
    hotelService.abortSaving(id);
    return "Aborted";
  }
}
