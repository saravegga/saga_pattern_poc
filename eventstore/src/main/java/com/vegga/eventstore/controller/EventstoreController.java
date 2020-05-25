package com.vegga.eventstore.controller;

import com.vegga.eventstore.service.EventstoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventstoreController {

  @Autowired private EventstoreService service;

  @GetMapping(value = "/check")
  public String check() {
    return "Eventstore service is alive";
  }
}
