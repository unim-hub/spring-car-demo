package com.unim.cardemo.media_service;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaController {

  @GetMapping("/playLists")
  public List<PlayList> playLists() {
      return Arrays.asList(
        new PlayList(1, "playlist-1", null),
        new PlayList(2, "playlist-2", null)
      );
  }
  
  @GetMapping("/playList")
  public PlayList playList(@RequestParam Integer playListId) {
      return new PlayList(playListId, "playlist-" + playListId.intValue(), 
                          new Song[]{
                            new Song(1, "title-1", "singer-1", "rock"),
                            new Song(2, "title-2", "singer-2", "rock"),
                            new Song(3, "title-3", "singer-3", "rock")
                          });
  }
  
}