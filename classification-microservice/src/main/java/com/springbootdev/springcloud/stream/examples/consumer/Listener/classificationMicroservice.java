package com.springbootdev.springcloud.stream.examples.consumer.Listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableEurekaClient
@EnableJpaRepositories({"com.springbootdev.springcloud.stream.examples.consumer.Repository"})
@ComponentScan({"com.springbootdev.springcloud.stream.examples.consumer.*"})
@EntityScan({"com.springbootdev.springcloud.stream.examples.consumer.*"})
public class classificationMicroservice {
    public static void main(String[] args) {
        SpringApplication.run(classificationMicroservice.class, args);
    }
}
