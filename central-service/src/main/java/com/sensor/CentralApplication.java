package com.sensor;

import com.sensor.verticle.CentralVerticle;
import io.vertx.reactivex.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CentralApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(CentralApplication.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(new CentralVerticle())
                .subscribe(
                        success -> LOGGER.info("Central Service started successfully "),
                        error -> LOGGER.error("Error while starting Central Service ", error));
    }
}