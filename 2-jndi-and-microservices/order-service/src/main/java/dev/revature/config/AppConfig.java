package dev.revature.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(){
        return RestClient.builder().baseUrl("http://warehouse-service");
    }

    // if we fetch multiple addresses from eureka, the @LoadBalanced annotation makes sure those requests are distributed across the different addresses -> localhost:8082, localhost:8083, localhost:8085
}
