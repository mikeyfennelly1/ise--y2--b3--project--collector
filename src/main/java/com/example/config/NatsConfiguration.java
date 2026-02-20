package com.example.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Configuration
public class NatsConfiguration {

    private static final int MAX_RETRIES = 5;
    private static final long INITIAL_BACKOFF_MS = 1000;

    @Value("${spring.nats.port}")
    private int natsPort;

    @Getter
    @Value("${spring.nats.host}")
    private String natsHost;
    @Getter
    @Value("${spring.nats.topic-name}")
    private String sysinfoSubject;

    @Value("${collector.id}")
    private String collectorName;

    @Getter
    private Connection connection;

    @PostConstruct
    void init() throws BeanCreationException {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                this.connection = newConn();
                log.info("Successfully connected to NATS at {}", hostName());
                return;
            } catch (Exception e) {
                attempts++;
                if (attempts >= MAX_RETRIES) {
                    log.error("FATAL: Failed to connect to NATS at {} after {} attempts: {}", hostName(), MAX_RETRIES, e.getMessage());
                    throw new BeanCreationException("fatal exception occurred creating connection to NATS broker: " + e.getMessage(), e);
                }

                long backoff = INITIAL_BACKOFF_MS * (long) Math.pow(2, attempts - 1);
                log.warn("Attempt {}/{} failed to connect to NATS at {}. Retrying in {}ms... Error: {}",
                        attempts, MAX_RETRIES, hostName(), backoff, e.getMessage());

                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BeanCreationException("NATS connection initialization interrupted", ie);
                }
            }
        }
    }

    private Connection newConn() throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server(hostName())
                .connectionTimeout(Duration.ofSeconds(5))
                .maxReconnects(5)
                .connectionName("ise--y2--b3--project--collector--" + collectorName)
                .reconnectWait(Duration.ofSeconds(1))
                .build();
        return Nats.connect(options);
    }

    private String hostName() {
        return String.format("nats://%s:%d", natsHost, natsPort);
    }
}
