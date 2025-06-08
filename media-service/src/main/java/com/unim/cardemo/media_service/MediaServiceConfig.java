package com.unim.cardemo.media_service;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MediaServiceConfig {
  private final Logger Log = LoggerFactory.getLogger(MediaServiceConfig.class);

  @Value("${server.port}")
  private int mServicePort;

  public MediaServiceConfig() {
    Log.info("MediaServiceConfig: " + Integer.toHexString(this.hashCode()));
  }

  public int getServicePort() {
    return mServicePort;
  }

  @Bean
  JdbcTemplate createJdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:8080", "http://localhost:8081", "http://localhost:8082")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
      }
    };
  }
}
