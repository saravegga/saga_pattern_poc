package com.vegga.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseDTO<T> {

  private UUID transactionalId;
  private Long objectId;
  private String entityName;
  private T entity;
}
