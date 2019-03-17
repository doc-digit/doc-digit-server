package com.springbootdev.springcloud.stream.examples.consumer.Listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan({"com.springbootdev.springcloud.stream.examples.consumer.*"})
public class rotatemicroservice {
    public static void main(String[] args) {
        SpringApplication.run(rotatemicroservice.class, args);
    }
}
