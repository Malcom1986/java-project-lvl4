FROM gradle:8.12.1-jdk17

WORKDIR /app

COPY /app .

RUN gradle clean build

CMD gradle run
