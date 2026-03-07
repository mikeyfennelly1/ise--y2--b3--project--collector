package com.example.producer;

import com.example.config.NatsConfiguration;
import org.example.libb3project.dto.TimeSeriesMessageDTO;
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

    public void publishTimeSeriesMsg(TimeSeriesMessageDTO msg, String subjName) throws JsonProcessingException, InterruptedException, TimeoutException {
        log.debug("getting byte array from TimeSeriesMessageDTO");
        byte[] messageBytes = msg.getBytes();
        log.debug("attempting to publish to NATS subject publishing to NATS subject");
        natsConnection.publish(subjName, messageBytes);
        log.trace("flushing connection to NATS");
        natsConnection.flush(Duration.ofMillis(100));
        log.debug("Published: {} to {}", msg, subjName);
    }

    public void publish(String subject, byte[] message) throws InterruptedException, TimeoutException {
        log.debug("attempting to publish to NATS subject publishing to NATS subject");
        natsConnection.publish(subject, message);
        log.trace("flushing connection to NATS");
        natsConnection.flush(Duration.ofMillis(100));
        log.debug("Published: {} to {}", new String(message), subject);
    }
}
