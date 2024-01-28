package com.task10.response;

import com.task10.model.Table;

import java.util.List;
import java.util.stream.Collectors;

public class TableResponse {
	private Integer id;
	private Integer number;
	private Integer places;
	private Boolean isVip;
	private Integer minOrder;

	public TableResponse() {
	}

	public TableResponse(Integer id, Integer number, Integer places, Boolean isVip, Integer minOrder) {
		this.id = id;
		this.number = number;
		this.places = places;
		this.isVip = isVip;
		this.minOrder = minOrder;
	}

	public static List<TableResponse> fromTableModel(List<Table> tables) {
		return tables.stream()
				.map(t -> new TableResponse(t.getId(), t.getNumber(), t.getPlaces(), t.getVip(), t.getMinOrder()))
				.collect(Collectors.toList());
	}

	public static TableResponse fromTableModel(Table table) {
		return new TableResponse(table.getId(), table.getNumber(), table.getPlaces(), table.getVip(), table.getMinOrder());
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
