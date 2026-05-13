package com.enjoytrip;

import com.enjoytrip.config.DotenvApplicationContextInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class EnjoyTripApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(EnjoyTripApplication.class);
        application.addInitializers(new DotenvApplicationContextInitializer());
        application.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder
                .initializers(new DotenvApplicationContextInitializer())
                .sources(EnjoyTripApplication.class);
    }
}
