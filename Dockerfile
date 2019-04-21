FROM openjdk:8-jre-alpine

RUN apk update && apk add bash &&  mkdir url-validation-service

COPY target/*.jar url-validation-service
COPY wait-for-it.sh url-validation-service
COPY start-app.sh url-validation-service

RUN chmod +x url-validation-service/wait-for-it.sh && chmod +x url-validation-service/start-app.sh

EXPOSE 8080

CMD url-validation-service/start-app.sh
