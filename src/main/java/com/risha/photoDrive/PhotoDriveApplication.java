package com.risha.photoDrive;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PhotoDriveApplication {

    public static void main(String[] args) {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
        SpringApplication.run(PhotoDriveApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}