import React, { useEffect, useState } from "react";

function MediaApp({ carDemoConfig }) {
  const [playlists, setPlayLists] = useState([]);
  const [selectedPlayList, setSelectedPlaylist] = useState(null);
  const [songs, setSongs] = useState([]);
  const [selectedSong, setSelectedSong] = useState(null);

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
  };

  return (
    <div>
      <h1>Media Service</h1>
      <div style={{ display: "flex", gap: "2rem", padding: "1rem" }}>
        <div style={{ padding: "1rem" }}>
          <h2>PlayLists</h2>
          <ul style={{ listStyle: "none", padding: 0 }}>
            {playlists.map((pl) => (
              <li
                key={pl.id}
                onClick={() => handlePlayListClick(pl)}
                style={{
                  padding: "0.5rem",
                  margin: "0.5rem 0",
                  border: "1px solid #ccc",
                  borderRadius: "5px",
                  cursor: "pointer",
                  background: selectedPlayList?.id === pl.id ? "#eef" : "#fff"
                }}
              >
                {pl.title}
              </li>
            ))}
          </ul>
        </div>

        <div style={{ padding: "1rem" }}>
          <h2>Songs in {selectedPlayList?.title}</h2>
          <ul style={{ listStyle: "none", padding: 0 }}>
            {songs.map((song) => (
              <li
                key={song.id}
                onClick={() => handleSongClick(song)}
                style={{
                  padding: "0.5rem",
                  margin: "0.5rem 0",
                  border: "1px solid #ccc",
                  borderRadius: "5px",
                  cursor: "pointer",
                  background: selectedSong?.id === song.id ? "#eef" : "#fff"
                }}
              >
                {song.title}
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default MediaApp;