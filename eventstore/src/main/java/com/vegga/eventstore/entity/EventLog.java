package com.vegga.eventstore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventLog {

  @Id private String id;
  private UUID transactionalId;
  private Long objectId;
  private String entityName;
  private Object entity;
  private boolean finished;
  private LocalDateTime insertionDate;
}
