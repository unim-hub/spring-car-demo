package com.unim.cardemo.media_service.entity;

import org.springframework.lang.Nullable;

public class PlayerState {

  public enum PlayingState {
    STOPPED(0),
    PLAYING(1),
    PAUSED(2);
        
    private final int state;

    PlayingState(int state) {
        this.state = state;
    }

    public int getCode() {
        return state;
    }
  }

  private PlayingState state;
  private Song song;
  private boolean availability;

  public PlayerState(PlayingState state, @Nullable Song song, boolean availability) {
    this.state = state;
    this.song = song;
    this.availability = availability;
  }

  public PlayingState getState() {
    return state;
  }

  public void setState(PlayingState state) {
    this.state = state;
  }

  public Song getSong() {
    return song;
  }

  public void setSong(Song song) {
    this.song = song;
  }

  public boolean getAvailability() {
    return availability;
  }

  public void setAvailability(boolean availability) {
    this.availability = availability;
  }
}