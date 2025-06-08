package com.unim.cardemo.media_service.entity;

import org.springframework.lang.Nullable;

public record PlayList(int id, String title, @Nullable Song[] songs){}