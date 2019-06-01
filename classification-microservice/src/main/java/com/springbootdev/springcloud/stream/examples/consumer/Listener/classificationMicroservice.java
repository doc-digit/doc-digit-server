package com.springbootdev.springcloud.stream.examples.consumer.Listener;

import com.springbootdev.springcloud.stream.examples.consumer.Model.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan({"com.springbootdev.springcloud.stream.examples.consumer.*"})
public class classificationMicroservice {
    public static void main(String[] args) {
        SpringApplication.run(classificationMicroservice.class, args);
    }
}
