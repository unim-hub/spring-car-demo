package com.unim.cardemo.vehicle_service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VehicleService extends TextWebSocketHandler{

  private static final Logger Log = LoggerFactory.getLogger(VehicleService.class);

  private final static int EVENT_TYPE_SPEED = 1;

  private final static String EVENT_FIELD_TYPE = "eventType";
  private final static String EVENT_FIELD_SPEED = "speed";

  private final ObjectMapper mObjectMapper;
  private final VehicleWebSocketHandler mVehicleWebSocketHandler;

  public VehicleService(@NonNull final VehicleWebSocketHandler vehicleWebSocketHandler) {
    super();
    Log.info("VehicleService: handler: " + Integer.toHexString(vehicleWebSocketHandler.hashCode()));
    mVehicleWebSocketHandler = vehicleWebSocketHandler;
    mObjectMapper = new ObjectMapper();
  }
 
  @KafkaListener(topics = "vehicle-service")
  public void handleMessage(String message){
    try {
      JsonNode json = mObjectMapper.readTree(message);
      int eventType = json.get(EVENT_FIELD_TYPE).asInt();
      switch (eventType) {
        case EVENT_TYPE_SPEED:
          handleSpeedMessage(json);
          break;
      
        default:
          break;
      }
    } catch (Exception e) {
      Log.error("Read Speed message fails: " + message + "\n", e);
    }
  }

  private void handleSpeedMessage(JsonNode speedMessage) {
    Log.info("handleSpeedMessage:" + speedMessage);
    try {
      int speed = speedMessage.get(EVENT_FIELD_SPEED).asInt();
      Log.info("handleSpeedMessage: " + speed);
      mVehicleWebSocketHandler.sendMessage(speedMessage);
    } catch (Exception e) {
      Log.error("Read Speed message fails: ", e);
    }
  }
}
