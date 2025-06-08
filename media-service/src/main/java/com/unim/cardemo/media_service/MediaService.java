package com.unim.cardemo.media_service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unim.cardemo.media_service.entity.PlayList;
import com.unim.cardemo.media_service.entity.PlayerState;
import com.unim.cardemo.media_service.entity.Song;
import com.unim.cardemo.media_service.entity.PlayerState.PlayingState;

@Service
public abstract class MediaService {
  private final Logger Log = LoggerFactory.getLogger(MediaService.class);

  private final static String EVENT_FIELD_TYPE = "eventType";
  private final static String EVENT_FIELD_PDC = "pdc";
  private final static int EVENT_TYPE_GEAR = 2;

  protected PlayerState mPlayerState;
  protected PlayerState mPrevState;
  protected final List<SseEmitter> mStaEmitters;
  private final ObjectMapper mObjectMapper;

  public MediaService() {
    mPlayerState = new PlayerState(PlayerState.PlayingState.STOPPED, null, true);
    mPrevState = new PlayerState(PlayerState.PlayingState.STOPPED, null, true);
    mStaEmitters = new LinkedList<>();
    mObjectMapper = new ObjectMapper();
  }

  public abstract List<PlayList> getPlayLists();

  public abstract List<Song> getPlayList(Integer playListId);

  public abstract ResponseEntity<Song> getSongWithText(Integer songId);

  public abstract ResponseEntity<Song> getSong(Integer songId);

  public PlayerState getPlayerState() {
    return mPlayerState;
  }

  public PlayerState playerSetSong(Integer songId) {
    ResponseEntity<Song> song = getSong(songId);
    if (song.getBody() != null) {
      mPlayerState.setSong(song.getBody());
      mPlayerState.setState(PlayerState.PlayingState.STOPPED);
    }
    dispatchPlayerState();
    return mPlayerState;
  }

  public PlayerState playerReset() {
    mPlayerState.setSong(null);
    mPlayerState.setState(PlayerState.PlayingState.STOPPED);
    dispatchPlayerState();
    return mPlayerState;
  }

  public PlayerState playerPlay() {
    mPlayerState.setState(PlayerState.PlayingState.PLAYING);
    dispatchPlayerState();
    return mPlayerState;
  }

  public PlayerState playerPause() {
    mPlayerState.setState(PlayerState.PlayingState.PAUSED);
    dispatchPlayerState();
    return mPlayerState;
  }

  public PlayerState playerStop() {
    mPlayerState.setState(PlayerState.PlayingState.STOPPED);
    dispatchPlayerState();
    return mPlayerState;
  }

  public SseEmitter subscribeStateNotifications() {
    final SseEmitter stateEmitter = new SseEmitter(0L);

    mStaEmitters.add(stateEmitter);

    stateEmitter.onCompletion(() -> mStaEmitters.remove(stateEmitter));
    stateEmitter.onTimeout(() -> mStaEmitters.remove(stateEmitter));

    try {
      stateEmitter.send(mPlayerState);
    } catch (IOException e) {
      stateEmitter.completeWithError(e);
    }

    return stateEmitter;
  }

  @KafkaListener(topics = "vehicle-service", groupId = "media-group-id")
  public void handleMessage(String message) {
    Log.info("Message:" + message);
    try {
      JsonNode json = mObjectMapper.readTree(message);
      int eventType = json.get(EVENT_FIELD_TYPE).asInt();
      switch (eventType) {
        case EVENT_TYPE_GEAR:
          handlePdcValue(json.get(EVENT_FIELD_PDC).asBoolean());
          break;
        default:
          break;
      }
    } catch (Exception e) {
      Log.error("Read Speed message fails: " + message + "\n", e);
    }
  }

  private void handlePdcValue(boolean bPdc) {
    if (bPdc) {
      if (mPlayerState.getAvailability()) {
        mPrevState = mPlayerState;
        mPlayerState = new PlayerState(mPlayerState.getState() == PlayingState.PLAYING ? PlayingState.PAUSED : mPrevState.getState(), mPrevState.getSong(), false);
      }
    } else {
      if (!mPlayerState.getAvailability()) {
        mPlayerState = mPrevState;
      }
    }
    dispatchPlayerState();
  }

  private void dispatchPlayerState() {
    for (SseEmitter emitter : mStaEmitters) {
      try {
        emitter.send(mPlayerState);
      } catch (IOException e) {
        emitter.complete();
      }
    }
  }
}
