package com.unim.cardemo.vehicle_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
  private static final Logger Log = LoggerFactory.getLogger(WebSocketConfig.class);

  private final VehicleWebSocketHandler mVehicleWebSocketHandler;

  public WebSocketConfig(@NonNull final VehicleWebSocketHandler vehicleWebSocketHandler) {
    super();
    mVehicleWebSocketHandler = vehicleWebSocketHandler;
    Log.info("WebSocketConfig: handler: " + Integer.toHexString(mVehicleWebSocketHandler.hashCode()));
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    Log.info("registerWebSocketHandlers");
    registry.addHandler(mVehicleWebSocketHandler, "/vehicle-ws").setAllowedOrigins("*");
  }
  
}
