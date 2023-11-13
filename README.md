# Data generator microservice

[![codecov](https://codecov.io/gh/IlyaLisov/data-generator-microservice/graph/badge.svg?token=SI8VHQM1I3)](https://codecov.io/gh/IlyaLisov/data-generator-microservice)

This is data generator microservice for [YouTube course](https://www.youtube.com/playlist?list=PL3Ur78l82EFBhKojbSO26BVqQ7n4AthHC).

This application produces data and sends it to [Data consumer service](https://github.com/IlyaLisov/data-analyser-microservice) with Apache Kafka.

### Usage

To start an application you need to pass variables to `.env` file.

Application is running on port `8081`.

All insignificant features (checkstyle, build check, dto validation) are not presented. 

#### Example:
```agsl
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

Application has two endpoints:
* POST `/api/v1/data/send`
#### Example JSON
```json
{
  "sensorId": 1,
  "timestamp": "2023-09-12T12:10:05",
  "measurement": 15.5,
  "measurementType": "TEMPERATURE"
}
```

* POST `/api/v1/data/test/send`
#### Example JSON
```json
{
  "delayInSeconds": 3,
  "measurementTypes": [
    "POWER",
    "VOLTAGE",
    "TEMPERATURE"
  ]
}
```