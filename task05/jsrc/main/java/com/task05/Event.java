package com.task05;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;

import java.util.Map;

@DynamoDBTable(tableName = "Events")
public class Event {

	public Event() {
	}

	public Event(String id, int principalId, String createdAt, Map<String, String> body) {
		this.id = id;
		this.principalId = principalId;
		this.createdAt = createdAt;
		this.body = body;
	}

	@DynamoDBHashKey(attributeName = "id")
	private String id;

	private int principalId;

	private String createdAt;

	@DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.M)
	private Map<String, String> body;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(int principalId) {
		this.principalId = principalId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public Map<String, String> getBody() {
		return body;
	}

	public void setBody(Map<String, String> body) {
		this.body = body;
	}
}
