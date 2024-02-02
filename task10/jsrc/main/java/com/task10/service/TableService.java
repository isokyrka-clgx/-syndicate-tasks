package com.task10.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task10.model.Table;
import com.task10.response.GetTablesResponse;
import com.task10.response.SaveTableResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class TableService {

    private Regions REGION = Regions.EU_CENTRAL_1;
    private final String TABLES_DB_TABLE_NAME = "cmtr-8efb0899-Tables-test";

    private AmazonDynamoDB amazonDynamoDB;

    private AmazonDynamoDB getAmazonDynamoDB() {
        if (amazonDynamoDB == null) {
            this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                    .withRegion(REGION)
                    .build();
        }
        return amazonDynamoDB;
    }

    public GetTablesResponse getTables() {
        ScanRequest scanRequest = new ScanRequest().withTableName(TABLES_DB_TABLE_NAME);
        ScanResult result = getAmazonDynamoDB().scan(scanRequest);
        GetTablesResponse getTablesResponse = new GetTablesResponse();
        for (Map<String, AttributeValue> item : result.getItems()) {
            Table table = new Table();
            table.setId(Integer.parseInt(item.get("id").getN()));
            table.setNumber(Integer.parseInt(item.get("number").getN()));
            table.setPlaces(Integer.parseInt(item.get("places").getN()));
            table.setVip(Boolean.parseBoolean(String.valueOf(item.get("isVip").getBOOL())));
            if (item.containsKey("minOrder")) {
                table.setMinOrder(Integer.parseInt(item.get("minOrder").getN()));
            }
            getTablesResponse.getTables().add(table);
        }
        return getTablesResponse;

    }

    public SaveTableResponse saveTable(String requestBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Table table = objectMapper.readValue(requestBody, Table.class);
        System.out.println("Table:" + table);
        Table savedTable = new Table();
        savedTable.setId(table.getId());
        savedTable.setNumber(table.getNumber());
        savedTable.setVip(table.isVip());
        savedTable.setPlaces(table.getPlaces());
        savedTable.setMinOrder(table.getMinOrder());
        DynamoDBMapper dbMapper = new DynamoDBMapper(getAmazonDynamoDB());
        try {
            dbMapper.save(savedTable);
            return new SaveTableResponse(table.getId());
        } catch (Exception e) {
            System.out.println(e);
            return new SaveTableResponse(1);
        }
    }


    public Table getTableById(int tableId) throws Exception {
        DynamoDB dynamoDB = new DynamoDB(getAmazonDynamoDB());
        com.amazonaws.services.dynamodbv2.document.Table dynamoTable = dynamoDB.getTable(TABLES_DB_TABLE_NAME);

        GetItemSpec getItemSpec = new GetItemSpec().withPrimaryKey("id", tableId);
        Item item = dynamoTable.getItem(getItemSpec);
        if (item == null) {
            throw new Exception("Table not found");
        }
        System.out.println("Table id:" + tableId);
        System.out.println("Item:" + item);
        Table table = new Table();
        table.setId(item.getInt("id"));
        table.setNumber(item.getInt("number"));
        table.setPlaces(item.getInt("places"));
        table.setVip(item.getNumber("isVip").equals(BigDecimal.ONE));
        if (item.isPresent("minOrder")) {
            table.setMinOrder(item.getInt("minOrder"));
        }
        return table;
    }

}
