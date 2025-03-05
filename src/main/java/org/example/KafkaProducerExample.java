package org.example;

import org.apache.kafka.clients.producer.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

public class KafkaProducerExample {
    private static final String BOOTSTRAP_SERVERS = System.getenv("KAFKA_BROKER") != null ? System.getenv("KAFKA_BROKER") : "localhost:19092";
    private static final String TOPIC_NAME = System.getenv("KAFKA_TOPIC") != null ? System.getenv("KAFKA_TOPIC") : "test-topic";
    private static final int MESSAGE_INTERVAL = System.getenv("MESSAGE_INTERVAL") != null ? Integer.parseInt(System.getenv("MESSAGE_INTERVAL")) : 1000;

    private static final Random random = new Random();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Producer<String, String> createProducer() {
        try {
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringSerializer");
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringSerializer");

            return new KafkaProducer<>(props);
        } catch (Exception e) {
            System.err.println("Error creating producer: " + e.getMessage());
            return null;
        }
    }

    private static Map<String, Object> generateSampleData(long counter) {
        Map<String, Object> data = new HashMap<>();
        data.put("message_id", counter);
        data.put("timestamp", Instant.now().getEpochSecond());
        data.put("temperature", Math.round(random.nextDouble() * 10 + 20) / 100.0); // 20.0 to 30.0
        data.put("humidity", Math.round(random.nextDouble() * 50 + 30) / 100.0);    // 30.0 to 80.0
        data.put("status", random.nextInt(3) == 0 ? "OK" :
                random.nextInt(2) == 0 ? "WARNING" : "ERROR");
        return data;
    }

    public static void main(String[] args) {
        Producer<String, String> producer = createProducer();
        if (producer == null) {
            return;
        }

        long counter = 0;
        try {
            System.out.println("Starting to produce messages...");
            while (true) {
                Map<String, Object> message = generateSampleData(counter);

                String jsonMessage = objectMapper.writeValueAsString(message);

                ProducerRecord<String, String> record =
                        new ProducerRecord<>(TOPIC_NAME, null, jsonMessage);

                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        System.err.println("Error sending message: " + exception.getMessage());
                    }
                });

                System.out.println("Sent message " + counter + ": " + jsonMessage);

                producer.flush();

                counter++;

                Thread.sleep(MESSAGE_INTERVAL); // Wait 1 second
            }
        } catch (InterruptedException e) {
            System.out.println("\nStopping producer...");
        } catch (Exception e) {
            System.err.println("Error producing messages: " + e.getMessage());
        } finally {
            producer.close();
            System.out.println("Producer closed");
        }
    }
}