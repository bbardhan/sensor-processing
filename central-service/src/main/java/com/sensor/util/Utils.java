package com.sensor.util;

import java.util.HashMap;
import java.util.Map;

public interface Utils {
    String SENSOR_DATA_TOPIC = "sensor.data";
    String SENSOR_DATA_GROUP_ID = "sensor-group-id";
    String TEMPERATURE_SENSOR_TYPE = "Temperature";
    String HUMIDITY_SENSOR_TYPE = "Humidity";
    int TEMPERATURE_SENSOR_THRESHOLD = 35;
    int HUMIDITY_SENSOR_THRESHOLD = 50;

    static Map<String, String> kafkaConsumerConfig(String group) {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "io.vertx.kafka.client.serialization.JsonObjectDeserializer");
        config.put("auto.offset.reset", "earliest");
        config.put("enable.auto.commit", "true");
        config.put("group.id", group);
        return config;
    }
}
