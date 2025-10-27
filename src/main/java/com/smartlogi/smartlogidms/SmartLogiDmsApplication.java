package com.smartlogi.smartlogidms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SmartLogiDmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartLogiDmsApplication.class, args);
    }

}
