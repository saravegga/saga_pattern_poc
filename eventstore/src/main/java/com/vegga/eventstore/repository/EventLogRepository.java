package com.vegga.eventstore.repository;

import com.vegga.eventstore.entity.EventLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventLogRepository extends MongoRepository<EventLog, Long> {}
