package com.egm.kafkaexample.consumers;

import com.egm.kafkaexample.dto.UserDetail;
import com.egm.kafkaexample.infra.MyException;
import com.egm.kafkaexample.model.User;
import com.egm.kafkaexample.repositories.UserRepository;
import com.egm.kafkaexample.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class UserConsumer {

    @Autowired
    private UserService userService;


    @KafkaListener(topics = "ssmdb.yazilim.user-created.0", containerFactory = "kafkaListenerContainerFactory")
    public void listener(
            @Payload UserDetail message,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) int offset,
            @Header("X-AgentName") String agentName
            //,Acknowledgment acknowledgment
    ) throws Exception {
        log.info("Kafka mesajı geldi: message::{}", message.toString());
        if (Objects.equals(message.getKullanici_adi(), "")) {
            log.error("Kullanici adi boş olamaz, O yüzden bu mesaj ignore edilecek");
            throw new MyException("Kullanici adi yok.");
        }

        //https://codeshare.io/K8qNQ7
        Thread.sleep(1000);

        log.info("partitionId: {}", partition);
        log.info("offset: {}", offset);
        log.info("agentName: {}", agentName);
        log.info("key: {}", key);
        log.info("message: {}", message.toString());

        // Eğer yas=0 gelirse AritmeticExcepiton yollayacak
        var sonuc = 12 / message.getYas();

        if (Objects.equals(message.getAdi(), "egm")) {
            throw new Exception("Veritabanina gidemedi.");
        }

        log.info("User başarılı bir şekilde kaydedili. user::{}", message.toString());
        islemYap(message);

        //menual olarak commit yollamak için
        //acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "ssmdb.yazilim.user-created.0.retry", containerFactory = "kafkaListenerContainerFactory")
    public void retryListener(@Payload UserDetail user) throws Exception {
        log.info("Retry Message: {}", user.toString());
        islemYap(user);
        //throw new Exception("Veritabanina gidemedi.");
    }


    // topic ve retry topic den gelen mesajlar burada işlenecek.
    private void islemYap(UserDetail user){
        userService.saveFromDetail(user);
    }


}
