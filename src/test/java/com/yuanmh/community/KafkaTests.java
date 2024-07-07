package com.yuanmh.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: Yuanmh
 * @Date: 下午10:01 2024/6/29
 * @Describe:
 */

@SpringBootTest
public class KafkaTests {

    @Autowired
    private KafkaProducer producer;

    @Autowired
    private KafkaConsumer consumer;

    @Test
    public void testKafka() {
        producer.sendMessage("test", "Hello, Kafka!");
        producer.sendMessage("test", "你好!");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@Component
class KafkaProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

}

@Component
class KafkaConsumer {

    @KafkaListener(topics = "test")
    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }


}