package com.egm.kafkaexample.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name="outbox_event")
public class OutboxEvent {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "aggregatetype")
    private String aggregateType;

    @Column(name = "aggregateid")
    private String aggregateId;
    private String type;
    private String correlationId;

    @Column(columnDefinition = "bytea")
    private byte[] payload;

    @CreatedDate
    private Timestamp creation_date;

    @PrePersist
    private void prePersist(){
        this.creation_date = Timestamp.from(Instant.now());
    }

}
