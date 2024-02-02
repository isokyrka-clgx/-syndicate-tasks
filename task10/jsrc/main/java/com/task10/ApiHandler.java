package com.task10;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.task10.model.Table;
import com.task10.response.GetReservationsResponse;
import com.task10.response.GetTablesResponse;
import com.task10.response.ResponseHandler;
import com.task10.response.SaveReservationResponse;
import com.task10.service.ReservationService;
import com.task10.service.TableService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminConfirmSignUpResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolClientsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolClientsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolDescriptionType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "api_handler",
		roleName = "api_handler-role"
)
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, Object> {

	private CognitoIdentityProviderClient identityProviderClient;
	private TableService tableService = new TableService();
	private ReservationService reservationService = new ReservationService();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		String httpMethod = request.getHttpMethod();
		String resource = request.getResource();
		APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
		System.out.println("Request: " + request);
		switch (resource) {
		case "/signup":
			if (httpMethod.equals("POST")) {
				responseEvent = signUp(request);
			}
			break;
		case "/signin":
			if (httpMethod.equals("POST")) {
				responseEvent = signIn(request);
			}
			break;
		case "/tables":
			if (httpMethod.equals("GET")) {
				responseEvent = getTables();
			}
			if (httpMethod.equals("POST")) {
				responseEvent = saveTable(request.getBody());
			}
			break;
		case "/tables/{tableId}":
			if (httpMethod.equals("GET")) {
				int tableId = Integer.parseInt(request.getPathParameters().get("tableId"));
				System.out.println("Table id: " + tableId);
				responseEvent = getTablesById(tableId);
			}
			break;
		case "/reservations":
			if (httpMethod.equals("GET")) {
				responseEvent = getReservations(request);
			}
			if (httpMethod.equals("POST")) {
				responseEvent = saveReservations(request);
			}

		}
		return responseEvent;
	}

	private APIGatewayProxyResponseEvent saveReservations(APIGatewayProxyRequestEvent request) {
		Gson gson = new Gson();
		try {
			SaveReservationResponse response = reservationService.saveReservation(request.getBody());
			return ResponseHandler.successResponse(gson.toJson(response));
		} catch (IllegalArgumentException | IOException e) {
			return ResponseHandler.errorResponse(e.getMessage());
		}
	}

	private APIGatewayProxyResponseEvent getReservations(APIGatewayProxyRequestEvent request) {
		Gson gson = new Gson();
		GetReservationsResponse response = reservationService.getReservations();
		return ResponseHandler.successResponse(gson.toJson(response));
	}

	private APIGatewayProxyResponseEvent getTablesById(int id) {
		Gson gson = new Gson();
		try {
			System.out.println("Get table by id....");
			Table response = tableService.getTableById(id);
			System.out.println("Get table by id response: " + response);
			System.out.println("Geted table by id.");
			return ResponseHandler.successResponse(gson.toJson(response));
		} catch (Exception exp) {
			return ResponseHandler.errorResponse(exp.getMessage());
		}
	}

	private APIGatewayProxyResponseEvent saveTable(String request) {
		Gson gson = new Gson();
		try {
			return ResponseHandler.successResponse(gson.toJson(tableService.saveTable(request)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private APIGatewayProxyResponseEvent getTables() {
		GetTablesResponse response = tableService.getTables();
		Gson gson = new Gson();
		return new APIGatewayProxyResponseEvent()
				.withBody(gson.toJson(response))
				.withStatusCode(200);
	}

	private APIGatewayProxyResponseEvent signIn(APIGatewayProxyRequestEvent request) {
		JSONParser parser = new JSONParser();
		JSONObject bodyJson = null;
		try {
			bodyJson = (JSONObject) parser.parse(request.getBody());
		} catch (ParseException exc) {
			throw new RuntimeException(exc);
		}
		String email = (String) bodyJson.get("email");
		String password = (String) bodyJson.get("password");
		Map<String, String> authParameters = new HashMap<>();
		authParameters.put("USERNAME", email);
		authParameters.put("PASSWORD", password);
		try {
			AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
					.clientId(getClientId())
					.userPoolId(getPoolId())
					.authParameters(authParameters)
					.authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
					.build();


			AdminInitiateAuthResponse response = getCognitoIdentityProviderClient().adminInitiateAuth(authRequest);
			String accessToken = response.authenticationResult().idToken();
			JSONObject responseBody = new JSONObject();
			responseBody.put("accessToken", accessToken);


			return response.sdkHttpResponse().isSuccessful() ?
					new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(responseBody.toJSONString()) :
					ResponseHandler.errorResponse("User signIn failed");
		} catch (CognitoIdentityProviderException exc) {
			return ResponseHandler.errorResponse("User signIn failed " + exc);
		}
	}


	private APIGatewayProxyResponseEvent signUp(APIGatewayProxyRequestEvent request) {
		JSONParser parser = new JSONParser();
		JSONObject bodyJson = null;
		try {
			bodyJson = (JSONObject) parser.parse(request.getBody());
		} catch (ParseException exc) {
			throw new RuntimeException(exc);
		}
		String firstName = (String) bodyJson.get("firstName");
		String lastName = (String) bodyJson.get("lastName");
		String email = (String) bodyJson.get("email");
		String password = (String) bodyJson.get("password");
		try {
			AdminConfirmSignUpResponse createUserResponse = registerUserInCognito(email, password, firstName, lastName);
			return createUserResponse.sdkHttpResponse().isSuccessful() ?
					ResponseHandler.successResponse("User registered successfully") :
					ResponseHandler.errorResponse("User registration failed");
		} catch (CognitoIdentityProviderException exc) {
			return ResponseHandler.errorResponse("User registration failed " + exc);
		}
	}


	private AdminConfirmSignUpResponse registerUserInCognito(String email, String password, String firstName, String lastName) {
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
		ListUserPoolClientsResponse listUserPoolClientsResponse = getCognitoIdentityProviderClient().listUserPoolClients(listUserPoolClientsRequest);
		return listUserPoolClientsResponse.userPoolClients().get(0).clientId();
	}

	private String getPoolId() {
		String userPoolName = "cmtr-8efb0899-simple-booking-userpool-test";
		ListUserPoolsRequest listUserPoolsRequest = ListUserPoolsRequest.builder()
				.maxResults(10)
				.build();
		ListUserPoolsResponse listUserPoolsResponse = getCognitoIdentityProviderClient().listUserPools(listUserPoolsRequest);
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