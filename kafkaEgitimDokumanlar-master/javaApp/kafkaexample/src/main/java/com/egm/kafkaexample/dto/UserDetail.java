package com.egm.kafkaexample.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDetail {

    private String id;
    private String adi;
    private String soyadi;
    private int yas;
    private String kullanici_adi;

}
