package com.task10.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task10.model.Reservation;
import com.task10.model.Table;
import com.task10.response.GetReservationsResponse;
import com.task10.response.GetTablesResponse;
import com.task10.response.SaveReservationResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReservationService {

    private Regions REGION = Regions.EU_CENTRAL_1;
    private final String RESERVATION_DB_TABLE_NAME = "cmtr-8efb0899-Reservations-test";

    private AmazonDynamoDB amazonDynamoDB;
    private TableService tableService = new TableService();

    private AmazonDynamoDB getAmazonDynamoDB() {
        if (amazonDynamoDB == null) {
            this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                    .withRegion(REGION)
                    .build();
        }
        return amazonDynamoDB;
    }

    public SaveReservationResponse saveReservation(String requestBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Reservation reservation = objectMapper.readValue(requestBody, Reservation.class);
        System.out.println("Save reservation: " + reservation);
        GetTablesResponse getTablesResponse = tableService.getTables(); // assuming tableService.getTables() returns a list of all tables
        Table table = getTablesResponse.getTables().stream()
                .filter(t -> t.getNumber() == reservation.getTableNumber())
                .findFirst()
                .orElse(null);

        if (table == null) {
            throw new IllegalArgumentException("Attempting to reserve a non-existent table");
        }
        List<Reservation> reservationsForTable = getReservations().getReservations();
        for (Reservation existingReservation : reservationsForTable) {
            if (hasOverlap(reservation, existingReservation)) {
                throw new IllegalArgumentException("Conflicting reservation");
            }
        }
        reservation.setId(UUID.randomUUID().toString());
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(getAmazonDynamoDB());
        dynamoDBMapper.save(reservation);

        return new SaveReservationResponse(reservation.getId());
    }

    private boolean hasOverlap(Reservation newReservation, Reservation existingReservation) {
        return newReservation.getTableNumber() == existingReservation.getTableNumber()
                && newReservation.getDate().equals(existingReservation.getDate())
                && (newReservation.getSlotTimeStart().compareTo(existingReservation.getSlotTimeStart()) <= 0
                && newReservation.getSlotTimeEnd().compareTo(existingReservation.getSlotTimeEnd()) >= 0);
    }

    public GetReservationsResponse getReservations() {
        ScanRequest scanRequest = new ScanRequest().withTableName(RESERVATION_DB_TABLE_NAME);
        ScanResult result = getAmazonDynamoDB().scan(scanRequest);
        GetReservationsResponse getReservationsResponse = new GetReservationsResponse();

        for (Map<String, AttributeValue> item : result.getItems()) {
            Reservation reservation = new Reservation();
            reservation.setId(item.get("id").getN());
            reservation.setTableNumber(Integer.parseInt(item.get("tableNumber").getN()));
            reservation.setClientName(item.get("clientName").getS());
            reservation.setPhoneNumber(item.get("phoneNumber").getS());
            reservation.setDate(item.get("date").getS());
            reservation.setSlotTimeStart(item.get("slotTimeStart").getS());
            reservation.setSlotTimeEnd(item.get("slotTimeEnd").getS());
            getReservationsResponse.getReservations().add(reservation);
        }
        return getReservationsResponse;
    }


}
