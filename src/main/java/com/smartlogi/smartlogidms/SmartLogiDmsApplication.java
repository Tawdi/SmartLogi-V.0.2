package com.smartlogi.smartlogidms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableAspectJAutoProxy
@EntityScan(basePackages = {
        "com.smartlogi.smartlogidms.delivery",
        "com.smartlogi.smartlogidms.masterdata",
        "io.github.tawdi.security.user.domain"
})
@EnableJpaRepositories(basePackages = {
        "com.smartlogi.smartlogidms",
        "io.github.tawdi.security.user.repository"
})
@ComponentScan(basePackages = {
        "com.smartlogi.smartlogidms",
        "io.github.tawdi.security"
})
public class SmartLogiDmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartLogiDmsApplication.class, args);
    }

}
