package com.task06;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "cmtr-8efb0899-Audit-test")
public class UpdatedEvent {

	@DynamoDBHashKey(attributeName = "id")
	private String id;

	private String itemKey;

	private String modificationTime;

	private String updatedAttribute;

	private String oldValue;

	private String newValue;

	public UpdatedEvent() {
	}

	public UpdatedEvent(String id, String itemKey, String modificationTime, String updatedAttribute, String oldValue, String newValue) {
		this.id = id;
		this.itemKey = itemKey;
		this.modificationTime = modificationTime;
		this.updatedAttribute = updatedAttribute;
		this.oldValue = oldValue;
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

	public String getUpdatedAttribute() {
		return updatedAttribute;
	}

	public void setUpdatedAttribute(String updatedAttribute) {
		this.updatedAttribute = updatedAttribute;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}
