package com.rolandoasmat.nvelope.models;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by rolandoasmat on 9/1/17.
 */

@SimpleSQLTable(table = "categories", provider = "NvelopeProvider")
public class Category {

    @SimpleSQLColumn("name")
    public String mName;

    public Category() { }

    public Category(String name) {
        this.mName = name;
    }
}
