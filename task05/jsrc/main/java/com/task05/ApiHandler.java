package com.task05;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
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
		lambdaName = "api_handler",
		roleName = "api_handler-role"
)
@DependsOn(name = "Events", resourceType = ResourceType.DYNAMODB_TABLE)
public class ApiHandler implements RequestHandler<Request, Response> {

	public Response handleRequest(Request request, Context context) {
		AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
				.withRegion(Regions.EU_CENTRAL_1)
				.build();

		Map<String, AttributeValue> valueMap = new HashMap<>();
		valueMap.put("id", new AttributeValue().withS(UUID.randomUUID().toString()));
		valueMap.put("principalId", new AttributeValue().withN(String.valueOf(request.getPrincipalId())));
		valueMap.put("createdAt", new AttributeValue().withS(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)));

		HashMap<String, AttributeValue> collect = request.getContent().entrySet().stream().map(entry -> {
			AttributeValue attributeValue = new AttributeValue();
			attributeValue.setS(entry.getValue());
			return new HashMap.SimpleEntry<>(entry.getKey(), attributeValue);
		}).collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), Map::putAll);

		valueMap.put("body", new AttributeValue().withM(collect));

		amazonDynamoDB.putItem("cmtr-8efb0899-Events-test", valueMap);

		Event event = new Event();
		event.setId(UUID.randomUUID().toString());
		event.setPrincipalId(request.getPrincipalId());
		event.setBody(request.getContent());
		event.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));

		return new Response(201, event);
	}
}
