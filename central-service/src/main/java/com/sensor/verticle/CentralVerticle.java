package com.sensor.verticle;

import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sensor.util.Utils.*;

public class CentralVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(CentralVerticle.class);

    @Override
    public Completable rxStart() {
        KafkaConsumer.<String, JsonObject>create(vertx, kafkaConsumerConfig(SENSOR_DATA_GROUP_ID))
                .subscribe(SENSOR_DATA_TOPIC)
                .toFlowable()
                .subscribe(this::processSensorMessage);

        return Completable.complete();
    }

    private void processSensorMessage(KafkaConsumerRecord<String, JsonObject> stringJsonObjectKafkaConsumerRecord) {
        JsonObject sensorData = stringJsonObjectKafkaConsumerRecord.value();
        String sensorType = sensorData.getString("sensorType");
        String sensorId = sensorData.getString("sensorId");
        Integer sensorValue = sensorData.getInteger("sensorValue");

        LOGGER.info("Received message: sensorType = {} , sensorId = {} , sensorValue = {} ", sensorType, sensorId, sensorValue);

        switch (sensorType) {
            case TEMPERATURE_SENSOR_TYPE -> {
                if (sensorValue > TEMPERATURE_SENSOR_THRESHOLD) {
                    LOGGER.error("Alarm! Temperature sensor value {} exceeds the sensor threshold {} ", sensorValue, TEMPERATURE_SENSOR_THRESHOLD);
                }
            }
            case HUMIDITY_SENSOR_TYPE -> {
                if (sensorValue > HUMIDITY_SENSOR_THRESHOLD) {
                    LOGGER.error("Alarm! Humidity sensor value {} exceeds the sensor threshold {} ", sensorValue, HUMIDITY_SENSOR_THRESHOLD);
                }
            }
            default -> LOGGER.error("Invalid sensor type: {}", sensorType);
        }
    }

}
