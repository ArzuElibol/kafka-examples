package com.egm.kafkaexample.producers;

import com.egm.kafkaexample.dto.UserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class UserProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    //https://codeshare.io/oQV3MB

    //@PostConstruct
    public void MesajGonder() {
        log.info("Kullanici bilgileri kafkaTemplate kullanılarak gönderiliyor..");

        var user = new UserDetail();
        user.setAdi("Veysel");
        user.setId("user-1");
        user.setYas(33);
        user.setKullanici_adi("vgunduzalp");

        Message<UserDetail> message = MessageBuilder
                .withPayload(user)
                .setHeader(KafkaHeaders.TOPIC, "ssmdb.yazilim.user-created.0")
                .setHeader(KafkaHeaders.MESSAGE_KEY, user.getId())
                .setHeader("X-AgentName", "java-user-create-service")
                .build();
                
        kafkaTemplate.send(message);
        log.info("Mesaj gönderildi.");

    }

}
