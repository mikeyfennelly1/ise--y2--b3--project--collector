package com.example.firestore;

import com.example.config.NatsConfiguration;
import com.example.producer.NatsProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FirestorePoller {
    private final Firestore firestore;
    private final NatsProducer natsProducer;
    private final NatsConfiguration natsConfig;
    private final ObjectMapper objectMapper;

    @Autowired
    public FirestorePoller(Firestore firestore, NatsProducer natsProducer, NatsConfiguration natsConfig, ObjectMapper objectMapper) {
        this.firestore = firestore;
        this.natsProducer = natsProducer;
        this.natsConfig = natsConfig;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRateString = "${firestore.polling-interval-ms:3000}")
    public void pollFirestore() {
        try{
            QuerySnapshot snapshot = firestore.collection("device_metrics")
                    .whereEqualTo("processed", false)
                    .limit(50)
                    .get()
                    .get(); // blocking call to resolve future to get snapshot synchronously
            List<QueryDocumentSnapshot> documents = snapshot.getDocuments();
            
            for (QueryDocumentSnapshot doc : documents){
                try{

                    Map<String, Object> messagePayload = new HashMap<>();
                    
                    String sourceName = doc.getString("device_id");
                    Long timestamp = doc.getLong("read_time") / 1000; 

                    Map<String, Double> metrics = new HashMap<>();
                    ((Map<String, Object>) doc.get("metrics")).forEach((k, v) -> metrics.put(k, ((Number) v).doubleValue())); // convert to Map<String, Double> for serialization

                    messagePayload.put("read_time", timestamp);
                    messagePayload.put("values", metrics);
                    messagePayload.put("source_properties", Map.of("producer_name", "android-" + sourceName));

                    byte[] messageBytes = objectMapper.writeValueAsBytes(messagePayload);
                    natsProducer.publish(natsConfig.getAndroidMetricsSubject(), messageBytes);
                    doc.getReference().update("processed", true);

                } catch (Exception e){
                    log.error("Error processing document {}: {}", doc.getId(), e.getMessage());
                }
            }
        }
        catch (Exception e){
            log.error("Error polling Firestore: {}", e.getMessage());
        }
    }
}