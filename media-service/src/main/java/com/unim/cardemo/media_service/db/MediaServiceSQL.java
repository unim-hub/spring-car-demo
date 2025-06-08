package com.unim.cardemo.media_service.db;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.unim.cardemo.media_service.MediaService;
import com.unim.cardemo.media_service.entity.PlayList;
import com.unim.cardemo.media_service.entity.Song;
import com.unim.cardemo.media_service.entity.SongTextLine;

@Service
@ConditionalOnProperty(name = "app.database.type", havingValue = "sql")
public class MediaServiceSQL extends MediaService implements CommandLineRunner {

  private final Logger Log = LoggerFactory.getLogger(MediaServiceSQL.class);
  private static final String TABLE_NAME_MEDIA_TEXT = "MediaText";

  protected JdbcTemplate mJdbcTemplate;

  public MediaServiceSQL(@NonNull final JdbcTemplate jdbcTemplate) {
    super();
    Log.info("MediaServiceSQL");
    mJdbcTemplate = jdbcTemplate;
  }

  public List<PlayList> getPlayLists() {
    String sql = "SELECT * FROM playlist";

    RowMapper<PlayList> mapper = (rs, rowNum) -> new PlayList(rs.getInt("playlist_id"), rs.getString("title"), null);

    return mJdbcTemplate.query(sql, mapper);
  }

  public List<Song> getPlayList(Integer playListId) {
    String sql = "SELECT s.song_id, s.title, s.singer, s.art, s.duration FROM song s, playlist_songs ps WHERE ps.playlist_id = ?";

    RowMapper<Song> mapper = (rs, rowNum) -> new Song(
        rs.getInt("song_id"),
        rs.getString("title"),
        rs.getString("singer"),
        rs.getString("art"),
        rs.getLong("duration"),
        null);

    return mJdbcTemplate.query(sql, mapper, playListId);
  }

  @Override
  public ResponseEntity<Song> getSong(Integer songId) {
    String sql = "SELECT * FROM song WHERE song_id = ?";

    RowMapper<Song> mapper = (rs, rowNum) -> new Song(
        rs.getInt("song_id"),
        rs.getString("title"),
        rs.getString("singer"),
        rs.getString("art"),
        rs.getLong("duration"),
        null);

    Song result = mJdbcTemplate.queryForObject(sql, mapper, songId);
    if (result == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(result);
  }

  public ResponseEntity<Song> getSongWithText(Integer songId) {
    Song result = null;
    final String sql = "SELECT s.song_id, s.title, s.singer, s.art, s.duration, st.start_time, st.end_time, st.textLine, " +
        "COUNT(*) OVER () AS total_count " +
        "FROM song s " +
        "LEFT JOIN song_text st ON s.song_id = st.song_id " +
        "WHERE s.song_id = ? " +
        "ORDER BY st.start_time";

    final Song[] tempSong = new Song[1];
    RowMapper<SongTextLine> mapper = (rs, rowNum) -> {
      if (tempSong[0] == null) {
        tempSong[0] = new Song(
            rs.getInt("song_id"),
            rs.getString("title"),
            rs.getString("singer"),
            rs.getString("art"),
            rs.getLong("duration"),
            null);
      }
      return new SongTextLine(
          rs.getInt("song_id"),
          rs.getInt("start_time"),
          rs.getInt("end_time"),
          rs.getString("textLine"));
    };
    List<SongTextLine> songText = mJdbcTemplate.query(sql, mapper, songId);
    if(tempSong[0] != null) {
      result = new Song(tempSong[0].id(), tempSong[0].title(), tempSong[0].singer(), tempSong[0].art(), tempSong[0].duration(), songText);
    }
    if (result == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(result);
  }

  @Override
  public void run(String... args) throws Exception {
    Log.info("Initialize media database");
    Integer count = mJdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ? AND TABLE_SCHEMA = 'PUBLIC'",
        Integer.class, TABLE_NAME_MEDIA_TEXT);

    if (count != null && count.intValue() > 0) {
      Log.info("Table " + TABLE_NAME_MEDIA_TEXT + " already exists");
    } else {
      Log.info("Creage Table " + TABLE_NAME_MEDIA_TEXT + " and fill with initial values");
      mJdbcTemplate.execute("CREATE TABLE playlist ( " +
          "playlist_id INT PRIMARY KEY, title VARCHAR(255))");
      mJdbcTemplate.execute("CREATE TABLE song (" +
          "song_id INT PRIMARY KEY, title VARCHAR(255), singer VARCHAR(255), art VARCHAR(255), duration BIGINT)");
      mJdbcTemplate.execute("CREATE TABLE playlist_songs (" +
          "song_id INT, playlist_id INT, " +
          "PRIMARY KEY (song_id, playlist_id)," +
          "FOREIGN KEY (song_id) REFERENCES song(song_id)," +
          "FOREIGN KEY (playlist_id) REFERENCES playlist(playlist_id))");
      mJdbcTemplate.execute("CREATE TABLE song_text (" +
          "song_id INT, start_time INT, end_time INT, textLine TEXT, " +
          "PRIMARY KEY (song_id, start_time)," +
          "FOREIGN KEY (song_id) REFERENCES song(song_id))");

      final int playlistCount = 5;
      final int songCount = 10;
      final int songTextLineCount = 10;
      for (int i = 0; i < playlistCount; i++) {
        String sql = "INSERT INTO playlist (playlist_id, title) VALUES (" + i + ", 'list-" + i + "')";
        // Log.info(sql);
        int countInserted = mJdbcTemplate.update(sql);
        if (countInserted != 1) {
          Log.error("INSERT failed on sql:" + sql);
          break;
        }
      }
      for (int i = 0; i < songCount; i++) {
        String sql = "INSERT INTO song (song_id, title, singer, art, duration)\n" +
            "VALUES (" + i + ", 'title-" + i + "', 'artist-" + i + "', 'rock', " + songTextLineCount + ")";
        // Log.info(sql);
        int countInserted = mJdbcTemplate.update(sql);
        if (countInserted != 1) {
          Log.error("INSERT failed on sql:" + sql);
          break;
        }

        for (int k = 0; k < songTextLineCount; k++) {
          int start = 2 * k;
          int end = 2 * (k + 1);
          String textLine = "song-" + i + "-text-line-" + k;
          sql = "INSERT INTO song_text (song_id, start_time, end_time, textLine)" +
              "VALUES(" + i + ", " + start + ", " + end + ", '" + textLine + "')";

          // Log.info(sql);
          countInserted = mJdbcTemplate.update(sql);
          if (countInserted != 1) {
            Log.error("INSERT failed on sql:" + sql);
            break;
          }
        }
      }
      for (int i = 0; i < playlistCount; i++) {
        for (int j = i; j < songCount; j += 2) {
          String sql = "INSERT INTO playlist_songs (song_id, playlist_id) VALUES (" + j + "," + i + ")";
          int countInserted = mJdbcTemplate.update(sql);
          if (countInserted != 1) {
            Log.error("INSERT failed on sql:" + sql);
            break;
          }
        }
      }
    }
  }
}
