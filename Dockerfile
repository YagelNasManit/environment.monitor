FROM openjdk:8u131-jdk-alpine

RUN apk add --no-cache curl tar bash vim

ARG MAVEN_VERSION=3.5.0
ARG USER_HOME_DIR="/root"
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-$MAVEN_VERSION-bin.tar.gz \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

VOLUME "$USER_HOME_DIR/.m2"

WORKDIR app
CMD mvn install -DskipTests=true -Dembedmongo.skip
CMD java -version && java -Dmongo.connect.uri=$MONGO_URL -Dplugin.jar.location="environment.monitor.test.extension-1.0-SNAPSHOT.jar" -jar  "environment.monitor.ui/target/environment.monitor.ui-1.0-SNAPSHOT-jetty-console.war"  --headless