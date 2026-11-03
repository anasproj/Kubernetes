package com.example.b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@RestController
public class BApplication {

    @Value("${SERVICE_C_URL}")
    private String serviceCUrl;

    public static void main(String[] args) {
        SpringApplication.run(BApplication.class, args);
    }

    @GetMapping("/")
    public String callC() {
        RestTemplate rest = new RestTemplate();
        String responseFromC = rest.getForObject(serviceCUrl, String.class);
        return "B -> " + responseFromC;
    }
}