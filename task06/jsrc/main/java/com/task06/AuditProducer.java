package com.task06;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(
		lambdaName = "audit_producer",
		roleName = "audit_producer-role"
)
@DynamoDbTriggerEventSource(targetTable = "Configuration", batchSize = 1)
@DependsOn(name = "Configuration", resourceType = ResourceType.DYNAMODB_TABLE)
public class AuditProducer implements RequestHandler<DynamodbEvent, Void> {

	private DynamoDBMapper dynamoDBMapper;
	private ObjectMapper objectMapper;

	public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {
		this.initDynamoDbClient();

		for (DynamodbEvent.DynamodbStreamRecord record : dynamodbEvent.getRecords()) {
			this.objectMapper = new ObjectMapper();

			System.out.println("RECORD EVENT NAME: " + record.getEventName());
			System.out.println("RECORD EVENT SOURCE ARN: " + record.getEventSourceARN());

			if ("INSERT".equals(record.getEventName()) && record.getEventSourceARN().contains("Configuration")) {
				Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();

				System.out.println(newImage);

				Map<String, String> valueMap = new HashMap<>();
				valueMap.put("key", newImage.get("key").getS());
				valueMap.put("value", newImage.get("value").getS());

				NewEvent newEvent = new NewEvent();
				newEvent.setId(UUID.randomUUID().toString());
				newEvent.setModificationTime(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
				newEvent.setItemKey(newImage.get("key").getS());
				newEvent.setNewValue(valueMap);

				dynamoDBMapper.save(newEvent);

			}
			else if ("MODIFY".equals(record.getEventName()) && record.getEventSourceARN().contains("Configuration")) {
				Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();

				System.out.println(newImage);

				UpdatedEvent updatedEvent = new UpdatedEvent();
				updatedEvent.setId(UUID.randomUUID().toString());
				updatedEvent.setModificationTime(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
				updatedEvent.setItemKey(newImage.get("key").getS());
				updatedEvent.setUpdatedAttribute("value");
				updatedEvent.setOldValue(Integer.parseInt(record.getDynamodb().getOldImage().get("value").getN()));
				updatedEvent.setNewValue(Integer.parseInt(newImage.get("value").getN()));

				dynamoDBMapper.save(updatedEvent);
			}
		}
		return null;
	}

	private String convertObjectToJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Object cannot be converted to JSON: " + object);
		}
	}

	private void initDynamoDbClient() {
		DynamoDBMapperConfig dynamoDBMapperConfig = DynamoDBMapperConfig.builder().build();

		AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
				.withRegion(Regions.EU_CENTRAL_1)
				.enableEndpointDiscovery()
				.build();

		this.dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB, dynamoDBMapperConfig);
	}
}
