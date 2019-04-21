#!/bin/bash
url-validation-service/wait-for-it.sh -t 15 localhost:15672 -- echo "--- RabbitMQ is up ---" &&
url-validation-service/wait-for-it.sh -t 15 localhost:3306 -- echo "--- MySQL is up ---" &&
java -jar url-validation-service/url-validation-service-0.0.1-SNAPSHOT.jar
