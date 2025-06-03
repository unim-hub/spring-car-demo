import './App.css';
import React, { useEffect, useState } from "react";

function MediaApp({ carDemoConfig }) {
  const [playlists, setPlayLists] = useState([]);
  const [selectedPlayList, setSelectedPlaylist] = useState(null);
  const [songs, setSongs] = useState([]);
  const [selectedSong, setSelectedSong] = useState(null);
  const [songsTextLines, setSongTextLines] = useState([]);
  const [playedTime, setPlayedTime] = useState(0);

  useEffect(() => {
    console.log("fetch: " + carDemoConfig.media_service_playlists);
    // Replace with your actual API endpoint
    fetch(carDemoConfig.media_service_playlists)
      .then((res) => res.json())
      .then((data) => setPlayLists(data))
      .catch((err) => console.error("Error fetching data:", err));
  }, []);

  const handlePlayListClick = (playList) => {
    setSelectedPlaylist(playList);
    console.log("Clicked playlist:", playList);
    fetch(carDemoConfig.media_service_playlist + playList.id)
      .then((res) => res.json())
      .then((data) => setSongs(data))
      .catch((err) => console.error("Error fetching data:", err));
  };

  const handleSongClick = (song) => {
    setSelectedSong(song);
    console.log("Clicked song:", song);
    fetch(carDemoConfig.media_service_song + song.id)
      .then((res) => res.json())
      .then((data) => setSongTextLines(data))
      .catch((err) => console.error("Error fetching data:", err));
  };

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
            <h2>Media player</h2>
            <ul style={{ display: "flex", gap: "1rem", listStyle: "none", padding: 0, margin: 0 }}>
              <li className='App-List-Item' style={{ background: "#fff" }}>Play</li>
              <li className='App-List-Item' style={{ background: "#fff" }}>Pause</li>
              <li className='App-List-Item' style={{ background: "#fff" }}>Stop</li>
            </ul>
            <h2>Song: {selectedSong?.title}</h2>
            <ul className='scrollable-list' style={{ listStyle: "none", padding: 0, margin: 0 }}>
              {songsTextLines.map((line) => (
                <li key={songsTextLines.startTime}
                  style={{ background: songsTextLines?.startTime > playedTime ? "#eef" : "#fff" }} >
                  {line.textLine}
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