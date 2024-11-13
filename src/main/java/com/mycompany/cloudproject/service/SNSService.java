package com.mycompany.cloudproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
public class SNSService {

    private final SnsClient snsClient;
    private final String topicArn;

    
    public SNSService(@Value("${AWS_S3_REGION}") String region,
                      @Value("${sns.topicArn}") String topicArn) {
       
        this.snsClient = SnsClient.builder()
                .region(Region.of(region))  // Dynamically set AWS region
                .credentialsProvider(DefaultCredentialsProvider.create())  // Default credentials provider
                .build();
        this.topicArn = topicArn;
    }

    // Method to publish the message to the SNS topic
    public void publishMessage(String sendTo, String tokenId, String activationLink) {
        try {
            // Build the message payload in JSON format
            String message = String.format(
                    "{\n" +
                    "    \"tokenId\":\"%s\",\n" +
                    "    \"email\":\"%s\",\n" +
                    "    \"activationLink\":\"%s\"\n" +
                    "}", tokenId, sendTo, activationLink);

            // Create a publish request with the message and topic ARN
            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)  // Use the dynamic SNS topic ARN
                    .message(message)    // The message to be published
                    .build();

            // Publish the message to SNS
            PublishResponse response = snsClient.publish(request);
            System.out.println("Message published with Message ID: " + response.messageId());
        } catch (Exception e) {
            System.err.println("Error publishing message to SNS: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
