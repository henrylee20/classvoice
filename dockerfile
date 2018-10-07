from openjdk:8-jdk-alpine
volume /tmp
arg JAR_FILE
copy ${JAR_FILE} app.jar

RUN apk add ffmpeg

entrypoint ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
