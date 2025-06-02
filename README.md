## Web Car Infortainment demo

<!-- [TOC] -->

- [Web Car Infortainment demo.](#web-car-infortainment-demo)
- [Architecture](#architecture)
  - [Component overview](#component-overview)
- [Prconditions](#prconditions)
  - [Run Kafka](#run-kafka)


## Architecture
### Component overview

## Prconditions:
### Run Kafka

1. Kafka Start Zookeeper
```shell
  bin/zookeeper-server-start.sh config/zookeeper.properties
```

2. Start Kafka Broker
```shell
bin/kafka-server-start.sh config/server.properties
```

3. Test Vehicle messages

  3.1 Open producer console for vehicle-service
```shell
  bin/kafka-console-producer.sh --topic vehicle-service --bootstrap-server localhost:9092
```

  3.2 Optional: Open consumer console for vehicle-service
```shell
  bin/kafka-console-producer.sh --topic vehicle-service --bootstrap-server localhost:9092
```