package com.orderManagement.orderProcessing.Producer;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
public class SqsProducerService {

    @Value("${aws.sqs.order-processing-queue}")
    private String queueName;

    @Autowired
    private SqsClient sqsClient;

    private String queueUrl;

    @PostConstruct
    public void init() {
        this.queueUrl = "http://localhost:4566/000000000000/" + queueName;
    }

    public String sendMessage(String message) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();

        SendMessageResponse response = sqsClient.sendMessage(request);
        return response.messageId();
    }
}

