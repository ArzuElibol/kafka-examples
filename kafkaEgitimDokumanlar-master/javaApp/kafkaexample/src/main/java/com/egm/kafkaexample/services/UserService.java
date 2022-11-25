package com.egm.kafkaexample.services;

import com.egm.kafkaexample.dto.UserDetail;
import com.egm.kafkaexample.model.OutboxEvent;
import com.egm.kafkaexample.model.User;
import com.egm.kafkaexample.repositories.OutboxEventRepository;
import com.egm.kafkaexample.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private ObjectMapper eventSerialier;

    public void saveFromDetail(UserDetail detail){
        var entity = new User();
        entity.setSoyadi(detail.getSoyadi());
        entity.setAdi(detail.getAdi());
        entity.setKullanici_adi(detail.getKullanici_adi());
        entity.setYas(detail.getYas());
        userRepository.save(entity);
    }


    @PostConstruct
    public void Test() throws Exception{

        var entity = new User();
        entity.setSoyadi("gunduzalp-1");
        entity.setAdi("veysel-1");
        entity.setKullanici_adi("vgunduzalp-1");
        entity.setYas(30);


        OutboxEvent event = new OutboxEvent();
        //event.setId(UUID.randomUUID());
        event.setAggregateId(UUID.randomUUID().toString());
        event.setCorrelationId(UUID.randomUUID().toString());
        event.setAggregateType(entity.getClass().getSimpleName());
        event.setType(entity.getClass().getSimpleName());

        byte[] data = eventSerialier.writeValueAsBytes(entity);
        event.setPayload(data);
        outboxEventRepository.save(event);
        userRepository.save(entity);

    }



//
//    @PostConstruct
//    public void Init(){
//
//        User user = new User();
//        user.setAdi("Veysel");
//        user.setYas(30);
//        user.setSoyadi("Gündüzalp");
//        user.setKullanici_adi("vgunduzalp");
//
//        userRepository.save(user);
//    }

}
