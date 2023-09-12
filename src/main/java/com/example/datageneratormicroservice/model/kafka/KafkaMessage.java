package com.example.datageneratormicroservice.model.kafka;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class KafkaMessage<T> {

    private String topic;
    private String key;
    private T data;

}
