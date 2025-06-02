package com.unim.cardemo.ui_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class UIController {

  private static final Logger log = LoggerFactory.getLogger(UIController.class);

  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("media_service_url", "http://localhost:8081");
    model.addAttribute("media_service_playlists", "http://localhost:8081/playLists");
    model.addAttribute("media_service_playlist", "http://localhost:8081/playList?playListId=");
    model.addAttribute("media_service_song", "http://localhost:8081/song?songId=");
    model.addAttribute("vehicle_service_url", "http://localhost:8082");
    model.addAttribute("vehicle_ws_url", "ws://localhost:8082/vehicle-ws");
    return "index";
  }
}
