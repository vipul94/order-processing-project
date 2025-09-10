package com.orderManagement.orderProcessing.Consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderManagement.orderProcessing.Service.OrderService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;

import java.util.List;
import java.util.Map;

@Service
public class SqsConsumerService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SqsClient sqsClient;

    @Value("${aws.sqs.order-processing-queue}")
    private String queueName;

    private static final Logger logger = LoggerFactory.getLogger(SqsConsumerService.class);

    private String queueUrl;

    @PostConstruct
    public void init() {
        queueUrl = "http://localhost:4566/000000000000/" + queueName;
    }

    // Poll SQS every 5 seconds
    @Scheduled(fixedDelay = 5000)
    public void pollMessages() {
        try {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(10)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(request).messages();

            for (Message message : messages) {
                try {
                    logger.info("Received message: {}", message.body());

                    // Example: convert JSON string back to Map
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, String> map = objectMapper.readValue(
                            message.body(),
                            new TypeReference<Map<String, String>>() {}
                    );

                    logger.info("Converted message map: {}", map);

                    // Process your message here
                    processMessage(map);

                    // Delete message after processing
                    sqsClient.deleteMessage(DeleteMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build());

                } catch (Exception e) {
                    logger.error("Error processing message: {}", message.body(), e);
                }
            }

        } catch (Exception e) {
            logger.error("Error receiving messages from SQS", e);
        }
    }

    private void processMessage(Map<String, String> messageMap) {
        orderService.updateOrderStatusSync(Long.valueOf(messageMap.get("orderId")) ,messageMap.get("orderStatus"));
        logger.info("Processing orderId={} done!", messageMap.get("orderId"));
    }
}
