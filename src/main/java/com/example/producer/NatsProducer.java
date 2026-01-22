package com.example.producer;

import com.example.config.NatsConfiguration;
import com.example.model.SysinfoMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.nats.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
public class NatsProducer {
    private static final Logger log = LoggerFactory.getLogger(NatsProducer.class);
    private final NatsConfiguration config;
    private final Connection natsConnection;

    @Autowired
    public NatsProducer(
            NatsConfiguration config
    ) {
        this.config = config;
        this.natsConnection = config.getConnection();
    }

    public void publishSysinfoEvent(SysinfoMessage msg) throws JsonProcessingException, InterruptedException, TimeoutException {
        log.debug("attempting to publish to NATS subject publishing to NATS subject");
        String subjName = config.getSysinfoSubject();
        byte[] messageBytes = msg.getBytes();
        natsConnection.publish(subjName, messageBytes);
        log.trace("flushing connection to NATS");
        natsConnection.flush(Duration.ofMillis(100));
        log.debug("Published: {} to {}", msg, config.getSysinfoSubject());
    }
}
