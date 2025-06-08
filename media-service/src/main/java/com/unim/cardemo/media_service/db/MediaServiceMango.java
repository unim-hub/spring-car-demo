package com.unim.cardemo.media_service.db;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.unim.cardemo.media_service.MediaService;
import com.unim.cardemo.media_service.entity.PlayList;
import com.unim.cardemo.media_service.entity.Song;
import com.unim.cardemo.media_service.entity.SongTextLine;

@Service
@ConditionalOnProperty(name = "app.database.type", havingValue = "mangodb")
public class MediaServiceMango extends MediaService implements CommandLineRunner {

  private final Logger Log = LoggerFactory.getLogger(MediaServiceMango.class);

  public MediaServiceMango() {
    super();
    Log.info("MediaServiceMango");
  }
  
  public List<PlayList> getPlayList() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getPlayList'");
  }

  public List<Song> getPlayList(Integer playListId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getPlayList'");
  }

  public List<SongTextLine> getSongText(Integer songId) {
          // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getSongText'");
  }

  @Override
  public ResponseEntity<Song> getSong(Integer songId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getSong'");
  }

  @Override
  public List<PlayList> getPlayLists() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getPlayLists'");
  }

  @Override
  public  ResponseEntity<Song> getSongWithText(Integer songId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getSongWithText'");
  }

  @Override
  public void run(String... args) throws Exception {
    Log.info("Initialize media database");
  }
}
