package com.task10.response;

import com.task10.model.Table;
import java.util.ArrayList;
import java.util.List;

public class GetTablesResponse {

    private List<Table> tables = new ArrayList<>();

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

}
