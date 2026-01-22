package com.example.controller;

import com.example.model.SysinfoMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventReadingController {

    private static final Logger logger = LogManager.getLogger(EventReadingController.class);

    @PostMapping("/sysinfo")
    public void postEventReading(@RequestBody SysinfoMessage sysinfoMessage) {
        logger.info("received message={}", sysinfoMessage.toString());
    }
}
