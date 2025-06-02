package com.unim.cardemo.vehicle_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VehicleServiceConfig {
  
  @Bean
  VehicleWebSocketHandler createVehicleWebSocketHandler() {
    return new VehicleWebSocketHandler();
  }
}
