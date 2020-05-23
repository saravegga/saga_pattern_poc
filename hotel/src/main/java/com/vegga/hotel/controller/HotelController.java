package com.vegga.hotel.controller;

import com.vegga.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HotelController {

  @Autowired private HotelService hotelService;

  @GetMapping(value = "/check")
  public String check() {
    return "Hotel service is alive";
  }
}
