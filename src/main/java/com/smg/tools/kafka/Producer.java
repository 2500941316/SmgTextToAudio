package com.smg.tools.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class Producer {
    private static KafkaProducer<String, String> producer;
    private final static String TOPIC = "heima";

    public Producer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "114.55.93.118:9092");
        props.put(ProducerConfig.RETRIES_CONFIG, 10);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //设置分区类,根据key进行数据分区
        producer = new KafkaProducer<String, String>(props);
    }

    public void produce() {
        for (int i = 3; i < 22; i++) {
            String key = String.valueOf(i);
            String data = "hello kafka message：" + key;
            producer.send(new ProducerRecord<String, String>(TOPIC, key, data));
        }
        producer.close();
    }

    public static void main(String[] args) {
        new Producer().produce();
    }
}