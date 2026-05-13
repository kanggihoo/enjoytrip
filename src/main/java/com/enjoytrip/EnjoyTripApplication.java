package com.enjoytrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class EnjoyTripApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(EnjoyTripApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(EnjoyTripApplication.class);
    }
}
