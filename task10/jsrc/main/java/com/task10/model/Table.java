package com.task10.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "cmtr-8efb0899-Tables-test")
public class Table {
	@DynamoDBHashKey(attributeName = "id")
	private Integer id;
	private Integer number;
	private Integer places;
	private Boolean isVip;
	private Integer minOrder;

	public Table() {
	}

	public Table(Integer id, Integer number, Integer places, Boolean isVip, Integer minOrder) {
		this.id = id;
		this.number = number;
		this.places = places;
		this.isVip = isVip;
		this.minOrder = minOrder;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getPlaces() {
		return places;
	}

	public void setPlaces(Integer places) {
		this.places = places;
	}

	public Boolean getVip() {
		return isVip;
	}

	public void setVip(Boolean vip) {
		isVip = vip;
	}

	public Integer getMinOrder() {
		return minOrder;
	}

	public void setMinOrder(Integer minOrder) {
		this.minOrder = minOrder;
	}
}
