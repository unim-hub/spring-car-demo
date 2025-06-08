import './App.css';
import React, { useEffect, useState } from "react";

function MediaApp({ carDemoConfig }) {
  /*
  public record MediaConfig(
    String config,
    String playLists,
    String playList,
    String song,
    String songText,
    String playerSet,
    String playerReset,
    String playerPlay,
    String playerPause,
    String playerStop,
    String playerState,
    String playerNotificationState
  )
   */
  const [mediaServiceConfig, setMediaServiceConfig] = useState(null);
  const [playlists, setPlayLists] = useState([]);
  const [selectedPlayList, setSelectedPlaylist] = useState(null);
  const [songs, setSongs] = useState([]);
  const [selectedSong, setSelectedSong] = useState(null);
  const [playedTime, setPlayedTime] = useState(0);
  const [playerState, setPlayerState] = useState(null);
  var eventSource = null;

  useEffect(() => {
    console.log("fetch: " + carDemoConfig.media_service_config);
    fetch(carDemoConfig.media_service_config)
      .then((res) => res.json())
      .then((data) => handleMeidaConfigUpdate(data))
      .catch((err) => console.error("Error fetching media configuration,  err: ", err));

    return () => {
      eventSource?.close();
    }
  }, []);

  const handleMeidaConfigUpdate = (meidaConfig) => {
    setMediaServiceConfig(meidaConfig);
    console.log("fetch: " + meidaConfig.playLists);
    fetch(meidaConfig.playLists)
      .then((res) => res.json())
      .then((data) => setPlayLists(data))
      .catch((err) => console.error("Error fetching play lists:", err));

    eventSource = new EventSource(meidaConfig.playerNotificationState);
    eventSource.onmessage = (event) => {
      console.log("Media Notification:", event.data);
      setPlayerState(JSON.parse(event?.data));
    };
  };

  const handlePlayListClick = (playList) => {
    setSelectedPlaylist(playList);
    console.log("fetch: " + mediaServiceConfig.playList + playList.id);
    fetch(mediaServiceConfig.playList + playList.id)
      .then((res) => res.json())
      .then((data) => setSongs(data))
      .catch((err) => console.error("Error fetching songs in playlist:", err));
  };

  const handleSongClick = (song) => {
    setSelectedSong(song);
    console.log("fetch: " + mediaServiceConfig.songText + song.id);
    fetch(mediaServiceConfig.songText + song.id)
      .then((res) => res.json())
      .then((data) => setSelectedSong(data))
      .catch((err) => console.error("Error fetching song with text :", err));
    console.log("fetch: " + mediaServiceConfig.playerSet + song.id);
    fetch(mediaServiceConfig.playerSet + song.id)
      .then((res) => res.json())
      .then((data) => handlePlayerStateChanged(data))
      .catch((err) => console.error("Error fetching player state :", err));
  };

  const handlePlayerStateChanged = (playerState) => {
    setPlayerState(playerState);
    console.log("PlayerState:{state:" + playerState?.state + ", song:" + playerState?.song?.title + ", availability:" + playerState?.availability);
  };

  const handlePlayerClick = (action) => {
    console.log("fetch: " + action);
    fetch(action)
      .then((res) => res.json())
      .then((data) => handlePlayerStateChanged(data))
      .catch((err) => console.error("Error fetching " + action + " :", err));
  }

  return (
    <div>
      <h1>Media Service</h1>
      <div style={{ display: "flex", gap: "2rem", padding: "1rem" }}>
        <div style={{ padding: "1rem" }}>
          <h2>PlayLists</h2>
          <ul className='scrollable-list' style={{ listStyle: "none", padding: 0 }}>
            {playlists.map((pl) => (
              <li key={pl.id} onClick={() => handlePlayListClick(pl)}
                style={{ background: selectedPlayList?.id === pl.id ? "#eef" : "#fff" }} >
                {pl.title}
              </li>
            ))}
          </ul>
        </div>

        <div style={{ padding: "1rem" }}>
          <h2>Songs in {selectedPlayList?.title}</h2>
          <ul className='scrollable-list' style={{ listStyle: "none", padding: 0 }}>
            {songs.map((song) => (
              <li key={song.id} onClick={() => handleSongClick(song)}
                style={{ background: selectedSong?.id === song.id ? "#eef" : "#fff" }} >
                {song.title}
              </li>
            ))}
          </ul>
        </div>

        <div>
          <div style={{ padding: "1rem" }}>
            <h2>Media player {selectedSong?.title} : {playerState?.state}</h2>
            <ul style={{
              display: "flex", gap: "1rem", listStyle: "none", padding: 0, margin: 0,
              pointerEvents: playerState?.availability ? "auto" : "none",
              opacity: playerState?.availability ? 1 : 0.5
            }}>
              <li onClick={() => handlePlayerClick(mediaServiceConfig?.playerPlay)}
                className={`App-List-Item ${playerState?.state === 'PLAYING' ? 'App-Button-Pressed' : ''}`}>Play</li>
              <li onClick={() => handlePlayerClick(mediaServiceConfig?.playerPause)}
                className={`App-List-Item ${playerState?.state === 'PAUSED' ? 'App-Button-Pressed' : ''}`}>Pause</li>
              <li onClick={() => handlePlayerClick(mediaServiceConfig?.playerStop)}
                className={`App-List-Item ${playerState?.state === 'STOPPED' ? 'App-Button-Pressed' : ''}`}>Stop</li>
            </ul>
            <h2>Song: {selectedSong?.title}</h2>
            <ul className='scrollable-list' style={{ listStyle: "none", padding: 0, margin: 0 }}>
              {selectedSong?.textLines?.map((songTextLine) => (
                <li key={songTextLine.startTime}
                  style={{ background: songTextLine.startTime < playedTime && songTextLine.endTime > playedTime ? "#eef" : "#fff" }} >
                  {songTextLine.startTime} - {songTextLine.endTime} : {songTextLine.textLine}
                </li>
              ))}
            </ul>
          </div>
        </div>

      </div>
    </div>
  );
};

export default MediaApp;