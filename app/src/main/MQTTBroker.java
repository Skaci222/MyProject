package com.course.mqttapptest;

public class MQTTBroker {

    private String broker;
    private String username;
    private String password;


    public MQTTBroker(String broker, String username, String password){
        this.broker = broker;
        this.username = username;
        this.password = password;
    }

}
