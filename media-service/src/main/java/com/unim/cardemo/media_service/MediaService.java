package com.unim.cardemo.media_service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class MediaService implements CommandLineRunner {
  private final Logger Log = LoggerFactory.getLogger(MediaService.class);
  private static final String TABLE_NAME_MEDIA_TEXT = "MediaText";

  private JdbcTemplate mJdbcTemplate;

  public MediaService(@NonNull final JdbcTemplate jdbcTemplate) {
    mJdbcTemplate = jdbcTemplate;
  }

  public List<PlayList> getPlayList() {
    String sql = "SELECT playlist_id, title FROM playlist";

    RowMapper<PlayList> mapper = (rs, rowNum) -> new PlayList(rs.getInt("playlist_id"), rs.getString("title"), null);

    return mJdbcTemplate.query(sql, mapper);
  }

  public List<Song> getPlayList(Integer playListId) {
    String sql = "SELECT song_id, title, singer, art, duration FROM song WHERE playlist_id = ?";

    RowMapper<Song> mapper = (rs, rowNum) -> new Song(
        rs.getInt("song_id"),
        rs.getString("title"),
        rs.getString("singer"),
        rs.getString("art"),
        rs.getLong("duration"));

    return mJdbcTemplate.query(sql, mapper, playListId);
  }

  public List<SongTextLine> getSongText(Integer songId) {
    String sql = "SELECT song_id, start_time, end_time, line FROM song_text WHERE song_id = ?";

    RowMapper<SongTextLine> mapper = (rs, rowNum) -> new SongTextLine(
        rs.getInt("song_id"),
        rs.getInt("start_time"),
        rs.getInt("end_time"),
        rs.getString("line"));

    return mJdbcTemplate.query(sql, mapper, songId);
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
      mJdbcTemplate.execute("CREATE TABLE playlist ( playlist_id INT PRIMARY KEY, title VARCHAR(255))");
      mJdbcTemplate.execute("CREATE TABLE song (" +
          "song_id INT PRIMARY KEY, title VARCHAR(255), singer VARCHAR(255), art VARCHAR(255), duration BIGINT, playlist_id INT, "
          +
          "FOREIGN KEY (playlist_id) REFERENCES playlist(playlist_id))");
      mJdbcTemplate.execute("CREATE TABLE song_text (" +
          "song_id INT, start_time INT, end_time INT,line TEXT, " +
          "PRIMARY KEY (song_id, start_time)," +
          "FOREIGN KEY (song_id) REFERENCES song(song_id))");

      final int playlistCount = 5;
      final int songCound = 10;
      final int songTextLineCount = 10;
      for (int i = 0; i < playlistCount; i++) {
        String sql = "INSERT INTO playlist (playlist_id, title) VALUES (" + i + ", 'list-" + i + "')";
        // Log.info(sql);
        int countInserted = mJdbcTemplate.update(sql);
        if (countInserted != 1) {
          Log.error("INSERT failed on sql:" + sql);
          break;
        }
        for (int j = i * songCound; j < songCound * (i + 1); j++) {
          sql = "INSERT INTO song (song_id, title, singer, art, duration, playlist_id)\n" +
              "VALUES (" + j + ", 'title-" + j + "', 'artist-" + j + "', 'rock', " + songTextLineCount + ", " + i + ")";
          // Log.info(sql);
          countInserted = mJdbcTemplate.update(sql);
          if (countInserted != 1) {
            Log.error("INSERT failed on sql:" + sql);
            break;
          }
          for (int k = 0; k < songTextLineCount; k++) {
            int start = 2 * k;
            int end = 2 * (k + 1);
            String line = "line-" + k;
            sql = "INSERT INTO song_text (song_id, start_time, end_time, line)" +
                "VALUES(" + j + ", " + start + ", " + end + ", '" + line + "')";

            // Log.info(sql);
            countInserted = mJdbcTemplate.update(sql);
            if (countInserted != 1) {
              Log.error("INSERT failed on sql:" + sql);
              break;
            }
          }
        }
      }
    }
  }

}
