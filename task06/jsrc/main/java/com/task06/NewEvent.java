package com.task06;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "cmtr-8efb0899-Audit-test")
public class NewEvent {

	@DynamoDBHashKey(attributeName = "id")
	private String id;

	private String itemKey;

	private String modificationTime;

	private String newValue;

	public NewEvent() {
	}

	public NewEvent(String id, String itemKey, String modificationTime, String newValue) {
		this.id = id;
		this.itemKey = itemKey;
		this.modificationTime = modificationTime;
		this.newValue = newValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemKey() {
		return itemKey;
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	public String getModificationTime() {
		return modificationTime;
	}

	public void setModificationTime(String modificationTime) {
		this.modificationTime = modificationTime;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}
