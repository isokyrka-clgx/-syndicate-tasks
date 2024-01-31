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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(
		lambdaName = "api_handler",
		roleName = "api_handler-role"
)
@DependsOn(name = "Tables", resourceType = ResourceType.DYNAMODB_TABLE)
@DependsOn(name = "Reservations", resourceType = ResourceType.DYNAMODB_TABLE)
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, Object> {

	private static final String UNSUPPORTED_METHOD_RESPONSE = "Unsupported method";
	private CognitoIdentityProviderClient identityProviderClient;
	private DynamoDBMapper dynamoDBMapper;
	private ObjectMapper objectMapper;

	@Override
	public Object handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		this.initDynamoDbClient();
		this.objectMapper = new ObjectMapper();

		String httpMethod = request.getHttpMethod();
		String resourcePath = request.getResource();

		System.out.println("httpMethod: " + httpMethod);
		System.out.println("resourcePath: " + resourcePath);

		switch (resourcePath) {
		case "/reservations":
			if ("POST".equals(httpMethod)) {
				System.out.println("Handling POST request reservation");
				return handleCreateReservation(request);
			}
			else if ("GET".equals(httpMethod)) {
				System.out.println("Handling GET request reservation");
				return handleGetReservations();
			}
			else {
				return UNSUPPORTED_METHOD_RESPONSE;
			}
		case "/tables":
			if ("GET".equals(httpMethod)) {
				System.out.println("Handling GET request tables");
				return handleGetTables();
			}
			else if ("POST".equals(httpMethod)) {
				System.out.println("Handling POST request tables");
				return handleCreateTable(request);
			}
			else {
				return UNSUPPORTED_METHOD_RESPONSE;
			}
		case "/signup":
			if ("POST".equals(httpMethod)) {
				System.out.println("Handling POST request signup");
				return handleSignup(request);
			}
			else {
				return UNSUPPORTED_METHOD_RESPONSE;
			}
		case "/signin":
			if ("POST".equals(httpMethod)) {
				System.out.println("Handling POST request signin");
				return handleSignin(request);
			}
			else {
				return UNSUPPORTED_METHOD_RESPONSE;
			}
		default:
			return UNSUPPORTED_METHOD_RESPONSE;
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

	private String handleSignin(APIGatewayProxyRequestEvent request) {
		JSONParser parser = new JSONParser();
		JSONObject bodyJson = null;
		try {
			bodyJson = (JSONObject) parser.parse(request.getBody());
			System.out.println("Successfully parsed body json");
		}
		catch (ParseException exc) {
			System.out.println("Failed to parse body json" + exc.getMessage());
			throw new RuntimeException(exc);
		}
		String email = (String) bodyJson.get("email");
		String password = (String) bodyJson.get("password");
		Map<String, String> authParameters = new HashMap<>();
		authParameters.put("USERNAME", email);
		authParameters.put("PASSWORD", password);
		try {
			System.out.println("Trying to authenticate user");

			AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
					.clientId(getClientId())
					.userPoolId(getPoolId())
					.authParameters(authParameters)
					.authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
					.build();

			System.out.println("Sending request to cognito");

			AdminInitiateAuthResponse response = getCognitoIdentityProviderClient().adminInitiateAuth(authRequest);
			String accessToken = response.authenticationResult().accessToken();
			JSONObject responseBody = new JSONObject();
			responseBody.put("accessToken", accessToken);

			return response.sdkHttpResponse().isSuccessful() ?
					responseBody.toJSONString() : "Error";
		}
		catch (CognitoIdentityProviderException exc) {
			System.out.println(exc.awsErrorDetails().errorMessage());
		}
		return null;
	}

	private String handleSignup(APIGatewayProxyRequestEvent request) {
		JSONParser parser = new JSONParser();
		JSONObject bodyJson = null;
		try {
			bodyJson = (JSONObject) parser.parse(request.getBody());
			System.out.println("Successfully parsed body json");
		}
		catch (ParseException exc) {
			System.out.println("Failed to parse body json" + exc.getMessage());
			throw new RuntimeException(exc);
		}
		String firstName = (String) bodyJson.get("firstName");
		String lastName = (String) bodyJson.get("lastName");
		String email = (String) bodyJson.get("email");
		String password = (String) bodyJson.get("password");
		try {
			System.out.println("Registering user in cognito");

			AdminConfirmSignUpResponse createUserResponse = registerUserInCognito(email, password, firstName, lastName);
			return createUserResponse.sdkHttpResponse().isSuccessful() ?
					"User registered successfully" :
					"User registration failed";
		}
		catch (CognitoIdentityProviderException exc) {
			System.out.println(exc.awsErrorDetails().errorMessage());
		}
		return null;
	}

	private AdminConfirmSignUpResponse registerUserInCognito(String email, String password, String firstName,
			String lastName) {
		AttributeType userAttrs = AttributeType.builder()
				.name("name").value(firstName + " " + lastName)
				.name("email").value(email)
				.build();
		SignUpRequest signUpRequest = SignUpRequest.builder()
				.userAttributes(userAttrs)
				.username(email)
				.clientId(getClientId())
				.password(password)
				.build();
		getCognitoIdentityProviderClient().signUp(signUpRequest);
		AdminConfirmSignUpRequest confirmSignUpRequest = AdminConfirmSignUpRequest.builder()
				.userPoolId(getPoolId())
				.username(email)
				.build();
		return getCognitoIdentityProviderClient().adminConfirmSignUp(confirmSignUpRequest);
	}

	private String getClientId() {
		ListUserPoolClientsRequest listUserPoolClientsRequest = ListUserPoolClientsRequest.builder()
				.userPoolId(getPoolId())
				.build();
		ListUserPoolClientsResponse listUserPoolClientsResponse = getCognitoIdentityProviderClient()
				.listUserPoolClients(listUserPoolClientsRequest);
		return listUserPoolClientsResponse.userPoolClients().get(0).clientId();
	}

	private String getPoolId() {
		String userPoolName = "cmtr-8efb0899-simple-booking-userpool-test";
		ListUserPoolsRequest listUserPoolsRequest = ListUserPoolsRequest.builder()
				.maxResults(10)
				.build();
		ListUserPoolsResponse listUserPoolsResponse = getCognitoIdentityProviderClient()
				.listUserPools(listUserPoolsRequest);
		return listUserPoolsResponse.userPools().stream()
				.filter(pool -> userPoolName.equals(pool.name()))
				.findFirst()
				.map(UserPoolDescriptionType::id)
				.orElse(null);
	}

	private CognitoIdentityProviderClient getCognitoIdentityProviderClient() {
		if (identityProviderClient == null) {
			this.identityProviderClient = CognitoIdentityProviderClient.builder()
					.region(Region.EU_CENTRAL_1)
					.build();
		}
		return identityProviderClient;
	}
}
