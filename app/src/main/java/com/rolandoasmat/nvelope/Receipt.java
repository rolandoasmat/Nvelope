package com.rolandoasmat.nvelope;

import java.util.Date;
import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by rolandoasmat on 8/31/17.
 */
@SimpleSQLTable(table = "receipts", provider = "NvelopeProvider")
public class Receipt {
    @SimpleSQLColumn("name_of_location")
    public String mNameOfLocation;

    @SimpleSQLColumn("category")
    public String mCategory;

    @SimpleSQLColumn("method_of_payment")
    public String mMethodOfPayment;

    @SimpleSQLColumn("date")
    public Date mDate;

    @SimpleSQLColumn("amount")
    public double mAmount;
}
