package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.syndicate.deployment.annotations.LambdaUrlConfig;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.ArrayList;
import java.util.List;

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
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, Response> {

	@Override
	public Response handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		return new Response();
	}
}

class Response {

	public Response() {
		temperature_2m.add("-2.4");
		temperature_2m.add("-2.8");
		temperature_2m.add("-3.2");
		temperature_2m.add("...");
	}

	private Double latitude = 50.4375;
	private Double longitude = 30.5;
	private Double generationtime_ms = 0.025033950805664062;
	private Integer utc_offset_seconds = 7200;
	private String timezone = "Europe/Kiev";
	private String timezone_abbreviation = "EET";
	private Double elevation = 188.0;
	private HourlyUnits hourly_units = new HourlyUnits();
	private Hourly hourly = new Hourly();
	private List<String> temperature_2m = new ArrayList<>();

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getGenerationtime_ms() {
		return generationtime_ms;
	}

	public void setGenerationtime_ms(Double generationtime_ms) {
		this.generationtime_ms = generationtime_ms;
	}

	public Integer getUtc_offset_seconds() {
		return utc_offset_seconds;
	}

	public void setUtc_offset_seconds(Integer utc_offset_seconds) {
		this.utc_offset_seconds = utc_offset_seconds;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getTimezone_abbreviation() {
		return timezone_abbreviation;
	}

	public void setTimezone_abbreviation(String timezone_abbreviation) {
		this.timezone_abbreviation = timezone_abbreviation;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	public HourlyUnits getHourly_units() {
		return hourly_units;
	}

	public void setHourly_units(HourlyUnits hourly_units) {
		this.hourly_units = hourly_units;
	}

	public Hourly getHourly() {
		return hourly;
	}

	public void setHourly(Hourly hourly) {
		this.hourly = hourly;
	}

	public List<String> getTemperature_2m() {
		return temperature_2m;
	}

	public void setTemperature_2m(List<String> temperature_2m) {
		this.temperature_2m = temperature_2m;
	}
}

class Hourly {

	public Hourly() {
		time.add("2023-12-04T00:00");
		time.add("2023-12-04T01:00");
		time.add("2023-12-04T02:00");
		time.add("...");
	}

	private List<String> time = new ArrayList<>();

	public List<String> getTime() {
		return time;
	}

	public void setTime(List<String> time) {
		this.time = time;
	}
}

class HourlyUnits {

	public HourlyUnits() {
	}

	private String time = "iso8601";
	private String temperature_2m = "°C";

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTemperature_2m() {
		return temperature_2m;
	}

	public void setTemperature_2m(String temperature_2m) {
		this.temperature_2m = temperature_2m;
	}
}
