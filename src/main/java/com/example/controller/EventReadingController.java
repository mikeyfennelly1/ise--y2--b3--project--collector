package com.example.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EventReadingController {

    private static final Logger logger = LogManager.getLogger(EventReadingController.class);

    @PostMapping("/event_reading/{deviceId}")
    public void postEventReading(@PathVariable String deviceId, @RequestBody Map<String, Double> reading) {
        logger.info("received reading={} from deviceId={}", reading, deviceId);
    }
}
