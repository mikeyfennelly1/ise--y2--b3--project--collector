package com.example.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class NatsConfiguration {

    @Value("${spring.nats.port}")
    private int natsPort;

    @Getter
    @Value("${spring.nats.topic-name}")
    private String sysinfoSubject;

    @Getter
    private Connection connection;

    @PostConstruct
    void init() throws BeanCreationException {
        try {
            this.connection = newConn();
        } catch (Exception e) {
            throw new BeanCreationException("fatal exception occurred creating connection to NATS broker: {}", e.getMessage());
        }
    }

    private Connection newConn() throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server(hostName())
                .connectionTimeout(Duration.ofSeconds(5))
                .maxReconnects(5)
                .reconnectWait(Duration.ofSeconds(1))
                .build();
        return Nats.connect(options);
    }

    private String hostName() {
        return String.format("nats://localhost:%d", natsPort);
    }
}
