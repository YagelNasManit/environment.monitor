FROM maven:latest AS builder
RUN ls -l
ADD . /app
WORKDIR /app
RUN mvn clean install -P embed-angular -DskipTests=true -Dembedmongo.skip

FROM openjdk:8u131-jdk-alpine
WORKDIR /root/
COPY --from=builder /app/environment.monitor.test.extension/target/environment.monitor.test.extension-2.0.0.jar .
COPY --from=builder /app/environment.monitor.server/target/environment.monitor.server-2.0.0.jar .
RUN ls -l
CMD java -version && java -Dmongo.connect.uri=$MONGO_URL \
   -Dplugin.jar.location="environment.monitor.test.extension-2.0.0.jar" \
   -jar "environment.monitor.server-2.0.0.jar"