package com.unim.cardemo.media_service;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class MediaServiceApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(MediaServiceApplication.class);
	private static final String TABLE_NAME_MEDIA_TEXT = "MediaText";

	public static void main(String[] args) {
		SpringApplication.run(MediaServiceApplication.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... args) throws Exception {

		int[] count = jdbcTemplate.batchUpdate("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ? AND TABLE_SCHEMA = 'PUBLIC'",
																							new Object[] { TABLE_NAME_MEDIA_TEXT });
		if (count != null && count.length > 0 && count[0] > 0) {
			log.info("Table " + TABLE_NAME_MEDIA_TEXT + " already exists");
		} else {
        jdbcTemplate.execute("CREATE TABLE ? (id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))", new Object[] { TABLE_NAME_MEDIA_TEXT });
				//jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);
		}
 	}
}
