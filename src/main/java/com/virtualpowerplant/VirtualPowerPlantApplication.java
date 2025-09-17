package com.virtualpowerplant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VirtualPowerPlantApplication {

    public static void main(String[] args) {
        SpringApplication.run(VirtualPowerPlantApplication.class, args);
    }
}