package com.egm.kafkaexample.repositories;

import com.egm.kafkaexample.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
}