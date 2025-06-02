package com.unim.cardemo.vehicle_service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class VehicleWebSocketHandler extends TextWebSocketHandler {

  private static final Logger Log = LoggerFactory.getLogger(VehicleWebSocketHandler.class);

  private final Set<WebSocketSession> mSessions = ConcurrentHashMap.newKeySet();

  public VehicleWebSocketHandler() {
    Log.info("VehicleWebSocketHandler: " + Integer.toHexString(this.hashCode()));
  }
  
  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    mSessions.add(session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    Log.info("handleTextMessage: session:" + session + ", message: " + message);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    mSessions.remove(session);
  }

  public void sendMessage(JsonNode message) {
    mSessions.forEach(s -> {
      try {
        s.sendMessage(new TextMessage(message.asText()));
      } catch (IOException e) {
        Log.warn("Sending message to " + s + " failed\n Error:" + e.getMessage());
      }
    });
  }

}
