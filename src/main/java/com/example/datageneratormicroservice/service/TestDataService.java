package com.example.datageneratormicroservice.service;

import com.example.datageneratormicroservice.model.test.DataTestOptions;

public interface TestDataService {

    void sendMessages(DataTestOptions testOptions);

}
