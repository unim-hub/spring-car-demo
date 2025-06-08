package com.unim.cardemo.media_service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.unim.cardemo.media_service.entity.MediaConfig;
import com.unim.cardemo.media_service.entity.PlayList;
import com.unim.cardemo.media_service.entity.PlayerState;
import com.unim.cardemo.media_service.entity.Song;

@RestController
public class MediaController {

  private final Logger Log = LoggerFactory.getLogger(MediaServiceConfig.class);

  private final MediaService mMediaService;
  private final MediaServiceConfig mMediaServiceConfig;
  private final MediaConfig mConfig;

  public MediaController(@NonNull final MediaService mediaService, @NonNull final MediaServiceConfig mediaConfig) {
    mMediaService = mediaService;
    mMediaServiceConfig = mediaConfig;
    final String baseServiceUrl = "http://localhost:" + mMediaServiceConfig.getServicePort();
    mConfig = new MediaConfig(
      baseServiceUrl + "/config",
      baseServiceUrl + "/playLists",
      baseServiceUrl + "/playList?playListId=",
      baseServiceUrl + "/song?songId=",
      baseServiceUrl + "/songText?songId=",
      baseServiceUrl + "/player/set?songId=",
      baseServiceUrl + "/player/reset",
      baseServiceUrl + "/player/play",
      baseServiceUrl + "/player/pause",
      baseServiceUrl + "/player/stop",
      baseServiceUrl + "/player/state",
      baseServiceUrl + "/player/notification/state"
    );
  }

  @GetMapping("/config")
  public MediaConfig config() {
    return mConfig;
  }

  @GetMapping(value = "/playLists", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PlayList> playLists() {
    Log.info("playLists");
    return mMediaService.getPlayLists();
  }
  
  @GetMapping(value = "/playList", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Song> playList(@RequestParam Integer playListId) {
    Log.info("playList:playListId=" + playListId);
    return mMediaService.getPlayList(playListId);
  }
  
  @GetMapping(value = "/song", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Song>  song(@RequestParam Integer songId) {
    return mMediaService.getSong(songId);
  }

  @GetMapping(value = "/songText", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Song>  songText(@RequestParam Integer songId) {
    return mMediaService.getSongWithText(songId);
  }

  @GetMapping(value = "/player/set", produces = MediaType.APPLICATION_JSON_VALUE)
  public PlayerState playerSetSong(@RequestParam Integer songId) {
    return mMediaService.playerSetSong(songId);
  }

  @GetMapping(value = "/player/reset", produces = MediaType.APPLICATION_JSON_VALUE)
  public PlayerState playerReset() {
    return mMediaService.playerReset();
  }
  
  @GetMapping(value = "/player/play", produces = MediaType.APPLICATION_JSON_VALUE)
  public PlayerState playerPlay() {
    return mMediaService.playerPlay();
  }
  
  @GetMapping(value = "/player/pause", produces = MediaType.APPLICATION_JSON_VALUE)
  public PlayerState playerPause() {
    return mMediaService.playerPause();
  }

  @GetMapping(value = "/player/stop", produces = MediaType.APPLICATION_JSON_VALUE)
  public PlayerState playerStop() {
    return mMediaService.playerStop();
  }

  @GetMapping(value = "/player/state", produces = MediaType.APPLICATION_JSON_VALUE)
  public PlayerState playerState() {
    return mMediaService.getPlayerState();
  }

  @GetMapping("player/notification/state")
  public SseEmitter subscribeStateNotifications() {
    return mMediaService.subscribeStateNotifications();
  }
  
}