# Backend sensor monitoring application

The application collects sensor data from different sources and notify alarm based on the threshold values.

## Building the project

Docker need to be up and running in order to build and run the project.

The projects can be built by running the following command from the 'sensor-processing' base directory.

To build all services run:

    mvn clean package

## Running the project

Kafka is being used as message broker for this application.
First open a terminal to start Kafka and Zookeeper containers with Docker Compose:

    docker-compose up

The two services can be started by running the following two commands from terminals:

    java -jar .\warehouse-service\target\warehouse-service-1.0-SNAPSHOT-all.jar
    java -jar .\central-service\target\central-service-1.0-SNAPSHOT-all.jar

The services should be up and ready to process messages.