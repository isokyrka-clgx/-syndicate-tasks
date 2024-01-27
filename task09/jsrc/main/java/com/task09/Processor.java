package com.task09;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.LambdaUrlConfig;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.TracingMode;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

@LambdaHandler(
		lambdaName = "processor",
		roleName = "processor-role",
		tracingMode = TracingMode.Active,
		layers = { "sdk-layer" }
)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)
@LambdaLayer(
		layerName = "sdk-layer",
		libraries = { "lib/jackson-databind-2.10.0.jar", "lib/jackson-annotations-2.10.0.jar" },
		runtime = DeploymentRuntime.JAVA8,
		artifactExtension = ArtifactExtension.ZIP
)
@DynamoDbTriggerEventSource(targetTable = "Weather", batchSize = 1)
@DependsOn(name = "Weather", resourceType = ResourceType.DYNAMODB_TABLE)
public class Processor implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard()
			.withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
			.build();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		try {
			String weatherData = getWeatherForecast(50.4375, 30.5);
			JsonNode jsonNode = objectMapper.readTree(weatherData);
			String id = UUID.randomUUID().toString();

			Map<String, AttributeValue> item = new HashMap<>();
			item.put("id", new AttributeValue().withS(id));
			item.put("forecast", new AttributeValue().withS(jsonNode.toString()));

			dynamoDB.putItem(new PutItemRequest().withTableName("cmtr-8efb0899-Weather-test").withItem(item));

			return response
					.withStatusCode(200)
					.withBody("Weather data stored successfully with ID: " + id);
		}
		catch (IOException e) {
			return response
					.withStatusCode(500)
					.withBody("Error: " + e.getMessage());
		}
	}

	private String getWeatherForecast(double latitude, double longitude) throws IOException {
		String baseUrl = "https://api.open-meteo.com/v1/forecast";
		URL url = new URL(baseUrl + "?latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		int responseCode = conn.getResponseCode();
		if (responseCode != 200) {
			throw new RuntimeException("HttpResponseCode: " + responseCode);
		}
		else {
			Scanner scanner = new Scanner(url.openStream());
			String response = scanner.useDelimiter("\\A").next();
			scanner.close();
			return response;
		}
	}
}
