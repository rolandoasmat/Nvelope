package com.rolandoasmat.nvelope;

import java.util.Date;
import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by rolandoasmat on 8/31/17.
 */
@SimpleSQLTable(table = "receipts", provider = "NvelopeProvider")
public class Receipt {
    @SimpleSQLColumn("title")
    public String mTitle;

    @SimpleSQLColumn("category")
    public String mCategory;

    @SimpleSQLColumn("payment_method")
    public String mPaymentMethod;

    @SimpleSQLColumn("date")
    public Date mDate;

    @SimpleSQLColumn("amount")
    public double mAmount;
}
