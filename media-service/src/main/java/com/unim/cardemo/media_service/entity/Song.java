package com.unim.cardemo.media_service.entity;

import java.util.List;

public record Song(int id, String title, String singer, String art, long duration, List<SongTextLine> textLines){}
