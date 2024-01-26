package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.syndicate.deployment.annotations.LambdaUrlConfig;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

@LambdaHandler(
		lambdaName = "api_handler",
		roleName = "api_handler-role",
		layers = { "sdk-layer" }
)
@LambdaLayer(
		layerName = "sdk-layer",
		libraries = { "lib/open-meteo-sdk-1.4.0.jar" },
		runtime = DeploymentRuntime.JAVA8,
		artifactExtension = ArtifactExtension.ZIP
)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, String> {

	@Override
	public String handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		return "{"
				+ "\"latitude\": 50.4375, "
				+ "\"longitude\": 30.5, "
				+ "\"generationtime_ms\": 0.025033950805664062, "
				+ "\"utc_offset_seconds\": 7200, "
				+ "\"timezone\": \"Europe/Kiev\", "
				+ "\"timezone_abbreviation\": \"EET\", "
				+ "\"elevation\": 188.0, "
				+ "\"hourly_units\": {\"time\": \"iso8601\", \"temperature_2m\": \"°C\"}, "
				+ "\"hourly\": {"
				+ "\"time\": [\"2023-12-04T00:00\", \"2023-12-04T01:00\", \"2023-12-04T02:00\", \"...\"], "
				+ "\"temperature_2m\": [-2.4, -2.8, -3.2, \"...\"]"
				+ "}}";
	}
}
