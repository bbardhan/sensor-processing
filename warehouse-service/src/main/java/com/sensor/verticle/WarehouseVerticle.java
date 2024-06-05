package com.sensor.verticle;

import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.datagram.DatagramSocket;
import io.vertx.reactivex.kafka.client.producer.KafkaProducer;
import io.vertx.reactivex.kafka.client.producer.KafkaProducerRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sensor.util.Utils.*;

public class WarehouseVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseVerticle.class);

    private KafkaProducer<String, JsonObject> kafkaProducer;

    @Override
    public Completable rxStart() {
        kafkaProducer = KafkaProducer.create(vertx, kafkaProducerConfig());

        String host = System.getProperty("SENSOR_HOST", "localhost");

        // Create a DatagramSocket for UDP connections for Temperature Sensor
        DatagramSocket temperatureSocket = vertx.createDatagramSocket();

        // Bind the socket to a specific port and host
        temperatureSocket.listen(TEMPERATURE_SENSOR_UDP_PORT, host, asyncResult -> {
            if (asyncResult.succeeded()) {
                LOGGER.info("Temperature sensor UDP socket listening on port {}", TEMPERATURE_SENSOR_UDP_PORT);

                // Start consuming data from the socket
                temperatureSocket.handler(packet -> {
                    // Process the received data
                    String data = packet.data().toString();
                    LOGGER.info("Received temperature sensor data: {}", data);
                    handleTemperatureSensorMessage(data);
                });
            } else {
                LOGGER.error("Failed to bind UDP socket: {}", asyncResult.cause().getMessage());
            }
        });

        // Create a DatagramSocket for UDP connections for Humidity Sensor
        DatagramSocket humiditySocket = vertx.createDatagramSocket();

        // Bind the socket to a specific port and host
        humiditySocket.listen(HUMIDITY_SENSOR_UDP_PORT, host, asyncResult -> {
            if (asyncResult.succeeded()) {
                LOGGER.info("Humidity sensor UDP socket listening on port {}", HUMIDITY_SENSOR_UDP_PORT);

                // Start consuming data from the socket
                humiditySocket.handler(packet -> {
                    // Process the received data
                    String data = packet.data().toString();
                    LOGGER.info("Received humidity sensor data: {}", data);
                    handleHumiditySensorMessage(data);
                });
            } else {
                LOGGER.error("Failed to bind UDP socket: {}", asyncResult.cause().getMessage());
            }
        });

        return Completable.complete();

    }

    private void handleTemperatureSensorMessage(String message) {
        if (StringUtils.isBlank(message)) {
            LOGGER.error("Temperature sensor message can't be blank");
            return;
        }
        if (!(message.contains("sensor_id=") && message.contains("value="))) {
            LOGGER.error("Invalid Temperature sensor message: {}", message);
            return;
        }

        KafkaProducerRecord<String, JsonObject> producerRecord = makeKafkaRecord(message, TEMPERATURE_SENSOR_TYPE);

        if (producerRecord == null) {
            return;
        }

        kafkaProducer.rxSend(producerRecord).subscribe(
                ok -> LOGGER.info("Temperature message sent to Kafka {}", message),
                err -> LOGGER.error("Temperature message is failed to sent to Kafka {}", err)
        );
    }

    private void handleHumiditySensorMessage(String message) {
        if (StringUtils.isBlank(message)) {
            LOGGER.error("Humidity sensor message can't be blank");
            return;
        }
        if (!(message.contains("sensor_id=") && message.contains("value="))) {
            LOGGER.error("Invalid Humidity sensor message: {}", message);
            return;
        }

        KafkaProducerRecord<String, JsonObject> producerRecord = makeKafkaRecord(message, HUMIDITY_SENSOR_TYPE);
        if (producerRecord == null) {
            return;
        }
        kafkaProducer.rxSend(producerRecord).subscribe(
                ok -> LOGGER.info("Humidity message sent to Kafka {}", message),
                err -> LOGGER.error("Humidity message is failed to sent to Kafka {}", err)
        );

    }

    private KafkaProducerRecord<String, JsonObject> makeKafkaRecord(String message, String sensorType) {
        int semicolonIndex = message.indexOf(";");
        String sensorId = message.substring(10, semicolonIndex);
        String sensorValue = message.substring(semicolonIndex + 7);

        if (StringUtils.isBlank(sensorId)) {
            LOGGER.error("sensorId can't be blank");
            return null;
        }

        if (!StringUtils.isNumeric(sensorValue)) {
            LOGGER.error("sensorValue must be a numeric value: {}", sensorValue);
            return null;
        }

        JsonObject recordData = new JsonObject()
                .put("sensorType", sensorType)
                .put("sensorId", sensorId)
                .put("sensorValue", Integer.valueOf(sensorValue));

        return KafkaProducerRecord.create(SENSOR_DATA_TOPIC, sensorType, recordData);
    }

}