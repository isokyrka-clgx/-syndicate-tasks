package com.task10.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "cmtr-8efb0899-Reservations-test")
public class Reservation {
	@DynamoDBHashKey(attributeName = "id")
	private String id;
	private Integer tableNumber;
	private String clientName;
	private String phoneNumber;
	private String date;
	private String slotTimeStart;
	private String slotTimeEnd;

	public Reservation() {
	}

	public Reservation(String id, Integer tableNumber, String clientName, String phoneNumber, String date,
			String slotTimeStart, String slotTimeEnd) {
		this.id = id;
		this.tableNumber = tableNumber;
		this.clientName = clientName;
		this.phoneNumber = phoneNumber;
		this.date = date;
		this.slotTimeStart = slotTimeStart;
		this.slotTimeEnd = slotTimeEnd;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(Integer tableNumber) {
		this.tableNumber = tableNumber;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSlotTimeStart() {
		return slotTimeStart;
	}

	public void setSlotTimeStart(String slotTimeStart) {
		this.slotTimeStart = slotTimeStart;
	}

	public String getSlotTimeEnd() {
		return slotTimeEnd;
	}

	public void setSlotTimeEnd(String slotTimeEnd) {
		this.slotTimeEnd = slotTimeEnd;
	}
}
