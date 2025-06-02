package com.unim.cardemo.media_service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaController {

  private final Logger Log = LoggerFactory.getLogger(MediaServiceConfig.class);
  private final MediaService mMediaService;

  public MediaController(@NonNull final MediaService mediaService) {
    mMediaService = mediaService;
  }

  @GetMapping("/playLists")
  public List<PlayList> playLists() {
    Log.info("playLists");
    return mMediaService.getPlayList();
  }
  
  @GetMapping("/playList")
  public List<Song> playList(@RequestParam Integer playListId) {
    Log.info("playList:playListId=" + playListId);
    return mMediaService.getPlayList(playListId);
  }
  
}