package com.task10.response;

import java.util.List;

public class TablesResponse {
	private List<TableResponse> tables;

	public TablesResponse() {
	}

	public TablesResponse(List<TableResponse> tables) {
		this.tables = tables;
	}

	public List<TableResponse> getTables() {
		return tables;
	}

	public void setTables(List<TableResponse> tables) {
		this.tables = tables;
	}
}
