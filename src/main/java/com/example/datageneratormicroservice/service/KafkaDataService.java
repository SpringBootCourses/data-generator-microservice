package com.example.datageneratormicroservice.service;

import com.example.datageneratormicroservice.model.Data;

public interface KafkaDataService {

    void send(Data data);

}
