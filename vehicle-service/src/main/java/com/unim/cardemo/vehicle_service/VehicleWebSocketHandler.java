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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class VehicleWebSocketHandler extends TextWebSocketHandler {

  private static final Logger Log = LoggerFactory.getLogger(VehicleWebSocketHandler.class);

  private final Set<WebSocketSession> mSessions = ConcurrentHashMap.newKeySet();
  private final ObjectMapper mObjectMapper;

  public VehicleWebSocketHandler() {
    Log.info("VehicleWebSocketHandler: " + Integer.toHexString(this.hashCode()));
    mObjectMapper = new ObjectMapper();
  }
  
  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    mSessions.add(session);
    Log.info("afterConnectionEstablished: session:" + Integer.toHexString(session.hashCode()) + ", count sessions: " + mSessions.size());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    Log.info("handleTextMessage: session:" + session + ", message: " + message);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    mSessions.remove(session);
    Log.info("afterConnectionClosed: session:" + Integer.toHexString(session.hashCode()) + ", count sessions: " + mSessions.size());
  }

  public void sendMessage(JsonNode jsonMessage) {
    Log.warn("sendMessage: session count: " + mSessions.size() + ", message: " + jsonMessage);
    try {
      String strMessage = mObjectMapper.writeValueAsString(jsonMessage);
      final TextMessage textMessage = new TextMessage(strMessage);
      mSessions.forEach(s -> {
        try {
          s.sendMessage(textMessage);
        } catch (IOException e) {
          Log.warn("Sending message to " + s + " failed\n Error:" + e.getMessage());
        }
      });
      } catch (JsonProcessingException e) {
        Log.error("sendMessage: Parce " + jsonMessage + " failed.");
        e.printStackTrace();
      }

  }

}
