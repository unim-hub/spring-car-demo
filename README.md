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

3. Test messages

  3.1 Register topic
```shell
  bin/kafka-topics.sh --create --topic test-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

  3.2 Send message 
```shell
  bin/kafka-console-producer.sh --topic test-topic --bootstrap-server localhost:9092
```

  3.3 Read message
```shell
  bin/kafka-console-consumer.sh --topic test-topic --from-beginning --bootstrap-server localhost:9092
```

