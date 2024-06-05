package com.sensor.util;

import java.util.HashMap;
import java.util.Map;

public interface Utils {
    String SENSOR_DATA_TOPIC = "sensor.data";
    String TEMPERATURE_SENSOR_TYPE = "Temperature";
    String HUMIDITY_SENSOR_TYPE = "Humidity";
    int TEMPERATURE_SENSOR_UDP_PORT = 3344;
    int HUMIDITY_SENSOR_UDP_PORT = 3355;

    static Map<String, String> kafkaProducerConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "io.vertx.kafka.client.serialization.JsonObjectSerializer");
        config.put("acks", "1");
        return config;
    }
}
