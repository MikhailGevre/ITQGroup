package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients(basePackages = "org.utils.client")
@ComponentScan(value = "org.utils")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}