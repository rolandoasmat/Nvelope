package com.rolandoasmat.nvelope.models;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by rolandoasmat on 9/1/17.
 */

@SimpleSQLTable(table = "payment_methods", provider = "NvelopeProvider")
public class PaymentMethod {

    @SimpleSQLColumn("name")
    public String mName;

    public PaymentMethod() { }

    public PaymentMethod(String name) {
        this.mName = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else {
            if( (String.valueOf(obj)).equals(mName) ) {
                return true;
            } else {
                return false;
            }
        }
    }
}
