package com.example;

import com.example.domain.Greeting;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@EnableHystrix
@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    public GreetingClient client;

    @Override
    public void run(String... strings) throws Exception {
        Greeting greeting = client.sendAndReceive();
        log.info(greeting.toString());
    }
}

@Component
class GreetingClient {
    private RestTemplate restTemplate = new RestTemplate();

    @HystrixCommand(fallbackMethod = "sendAndReceiveFallback")
    public Greeting sendAndReceive() {
        return restTemplate.getForObject("http://localhost:8080/greeting", Greeting.class);
    }

    public Greeting sendAndReceiveFallback() {
        Greeting greeting = new Greeting();
        greeting.setId(-1);
        greeting.setContent("ERROR");
        return greeting;
    }

}
