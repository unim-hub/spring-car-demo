package com.unim.cardemo.vehicle_service;

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
  private final static int EVENT_TYPE_GEAR = 2;

  private final static String EVENT_FIELD_TYPE = "eventType";

  private final ObjectMapper mObjectMapper;
  private final VehicleWebSocketHandler mVehicleWebSocketHandler;

  public VehicleService(@NonNull final VehicleWebSocketHandler vehicleWebSocketHandler) {
    super();
    Log.info("VehicleService: handler: " + Integer.toHexString(vehicleWebSocketHandler.hashCode()));
    mVehicleWebSocketHandler = vehicleWebSocketHandler;
    mObjectMapper = new ObjectMapper();
  }
 
  @KafkaListener(topics = "vehicle-service", groupId = "vehicle-group-id")
  public void handleMessage(String message){
    Log.info("Message:" + message);
    try {
      JsonNode json = mObjectMapper.readTree(message);
      int eventType = json.get(EVENT_FIELD_TYPE).asInt();
      switch (eventType) {
        case EVENT_TYPE_SPEED:
        case EVENT_TYPE_GEAR:
          mVehicleWebSocketHandler.sendMessage(json);
          break;
      
        default:
          break;
      }
    } catch (Exception e) {
      Log.error("Read Speed message fails: " + message + "\n", e);
    }
  }
}
