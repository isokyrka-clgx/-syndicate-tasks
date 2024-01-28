package com.task10;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.task10.model.Reservation;
import com.task10.model.Table;
import com.task10.response.*;

import java.util.List;
import java.util.UUID;

@LambdaHandler(
		lambdaName = "api_handler",
		roleName = "api_handler-role"
)
@DependsOn(name = "Tables", resourceType = ResourceType.DYNAMODB_TABLE)
@DependsOn(name = "Reservations", resourceType = ResourceType.DYNAMODB_TABLE)
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, Object> {

	private static final String UNSUPPORTED_METHOD_RESPONSE = "Unsupported method";
	private DynamoDBMapper dynamoDBMapper;
	private ObjectMapper objectMapper;

	@Override
	public Object handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		this.initDynamoDbClient();
		this.objectMapper = new ObjectMapper();

		System.out.println(request);

		String httpMethod = request.getHttpMethod();
		String resourcePath = request.getResource();

		switch (resourcePath) {
		case "/reservations":
			if ("POST".equals(httpMethod)) {
				return handleCreateReservation(request);
			}
			else if ("GET".equals(httpMethod)) {
				return handleGetReservations();
			}
			else {
				return UNSUPPORTED_METHOD_RESPONSE;
			}
		case "/tables":
			if ("GET".equals(httpMethod)) {
				return handleGetTables();
			}
			else if ("POST".equals(httpMethod)) {
				return handleCreateTable(request);
			}
			else {
				return UNSUPPORTED_METHOD_RESPONSE;
			}
		case "/signup":
			if ("POST".equals(httpMethod)) {
				return handleSignup(request);
			}
			else {
				return UNSUPPORTED_METHOD_RESPONSE;
			}
		case "/signin":
			if ("POST".equals(httpMethod)) {
				return handleSignin(request);
			}
			else {
				return UNSUPPORTED_METHOD_RESPONSE;
			}
		default:
			return UNSUPPORTED_METHOD_RESPONSE;
		}
	}

	private Object handleSignin(APIGatewayProxyRequestEvent request) {
		return null;
	}

	private Object handleSignup(APIGatewayProxyRequestEvent request) {
		return null;
	}

	private void initDynamoDbClient() {
		DynamoDBMapperConfig dynamoDBMapperConfig = DynamoDBMapperConfig.builder().build();

		AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
				.withRegion(Regions.EU_CENTRAL_1)
				.enableEndpointDiscovery()
				.build();

		this.dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB, dynamoDBMapperConfig);
	}

	private ReservationCreatedResponse handleCreateReservation(APIGatewayProxyRequestEvent request) {
		final String id = UUID.randomUUID().toString();
		Reservation reservation = parseObjectFromRequest(request.getBody(), Reservation.class);
		reservation.setId(id);

		System.out.println(String.format("Creating reservation with id %s", id));

		dynamoDBMapper.save(reservation);

		return new ReservationCreatedResponse(id);
	}

	private ReservationsResponse handleGetReservations() {
		System.out.println("Getting all reservations");

		List<Reservation> reservations = dynamoDBMapper.scan(Reservation.class, new DynamoDBScanExpression());
		return new ReservationsResponse(ReservationResponse.fromReservationModel(reservations));
	}

	private TableCreatedResponse handleCreateTable(APIGatewayProxyRequestEvent request) {
		System.out.println("Creating table");

		Table table = parseObjectFromRequest(request.getBody(), Table.class);

		dynamoDBMapper.save(table);

		return new TableCreatedResponse(table.getId());
	}

	private TablesResponse handleGetTables() {
		System.out.println("Getting all tables");

		List<Table> tables = dynamoDBMapper.scan(Table.class, new DynamoDBScanExpression());
		return new TablesResponse(TableResponse.fromTableModel(tables));
	}

	private TableResponse handleGetTable(String id) {
		System.out.println(String.format("Getting table with id %s", id));

		Table table = dynamoDBMapper.load(Table.class, id);
		return TableResponse.fromTableModel(table);//TODO add handle case
	}

	private <T> T parseObjectFromRequest(String body, Class<T> targetClass) {
		try {
			return objectMapper.readValue(body, targetClass);
		}
		catch (Exception e) {
			throw new RuntimeException("Error parsing data", e);
		}
	}
}
