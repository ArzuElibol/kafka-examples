package com.egm.kafkaexample.infra;

public class MyException extends Exception {
    public MyException(String errorMessage) {
        super(errorMessage);
    }
}
