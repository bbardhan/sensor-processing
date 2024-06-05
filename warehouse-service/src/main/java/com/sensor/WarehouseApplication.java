package com.sensor;

import com.sensor.verticle.WarehouseVerticle;
import io.vertx.reactivex.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarehouseApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseApplication.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(new WarehouseVerticle())
                .subscribe(
                        success -> LOGGER.info("Warehouse Service started successfully "),
                        error -> LOGGER.error("Error while starting Warehouse Service ", error));
    }
}
