package com.vegga.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageOutput {

  private UUID transactionalId;
  private Long objectId;
  private List<String> errors;
}
