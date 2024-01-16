package com.task05;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@LambdaHandler(
		lambdaName = "api_handler",
		roleName = "api_handler-role"
)
public class ApiHandler implements RequestHandler<Request, Response> {

	private DynamoDBMapper dynamoDBMapper;

	public Response handleRequest(Request request, Context context) {
		this.initDynamoDbClient();

		Event event = new Event();
		event.setId(UUID.randomUUID().toString());
		event.setPrincipalId(request.getPrincipalId());
		event.setBody(request.getContent());
		event.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));

		dynamoDBMapper.save(event);

		return new Response(201, event);
	}

	private void initDynamoDbClient() {
		DynamoDBMapperConfig dynamoDBMapperConfig = DynamoDBMapperConfig.builder().build();

		AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
				.withRegion(Regions.EU_CENTRAL_1.getName())
				.build();

		this.dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB, dynamoDBMapperConfig);
	}
}
