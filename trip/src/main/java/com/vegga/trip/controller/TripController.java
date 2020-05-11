package com.vegga.trip.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TripController {

    @GetMapping(value = "/check")
    public String check() {
        return "Trip service is alive";
    }
}
