package com.task07;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.CloudWatchLogsEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@LambdaHandler(
		lambdaName = "uuid_generator",
		roleName = "uuid_generator-role"
)
@RuleEventSource(targetRule = "uuid_trigger")
@DependsOn(name = "uuid_trigger", resourceType = ResourceType.CLOUDWATCH_RULE)
public class UuidGenerator implements RequestHandler<CloudWatchLogsEvent, Void> {

	private AmazonS3 s3Client;
	private ObjectMapper objectMapper;

	public Void handleRequest(CloudWatchLogsEvent request, Context context) {

		System.out.println(request.getAwsLogs());

		this.s3Client = AmazonS3Client.builder().withRegion(Regions.EU_CENTRAL_1).build();
		this.objectMapper = new ObjectMapper();

		String bucketName = "cmtr-8efb0899-uuid_trigger-test";

		Holder holder = new Holder(Stream.generate(UUID::randomUUID)
				.limit(10).collect(Collectors.toList()));

		String fileName = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT) + ".json";

		s3Client.putObject(bucketName, fileName, convertObjectToJson(holder));

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
}

class Holder {
	private List<UUID> ids;

	public Holder(List<UUID> ids) {
		this.ids = ids;
	}

	public List<UUID> getIds() {
		return ids;
	}

	public void setIds(List<UUID> ids) {
		this.ids = ids;
	}
}
