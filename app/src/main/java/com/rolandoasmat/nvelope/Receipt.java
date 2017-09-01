package com.rolandoasmat.nvelope;

import java.util.Date;
import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by rolandoasmat on 8/31/17.
 */
@SimpleSQLTable(table = "receipts", provider = "NvelopeProvider")
public class Receipt {
    @SimpleSQLColumn("location")
    public String mLocation;

    @SimpleSQLColumn("category")
    public String mCategory;

    @SimpleSQLColumn("method_of_payment")
    public String mMethodOfPayment;

    @SimpleSQLColumn("date")
    public Date mDate;

    @SimpleSQLColumn("amount")
    public double mAmount;

    public Receipt() {}

    public Receipt(String mLocation, String mCategory, String mMethodOfPayment, Date mDate, double mAmount) {
        this.mLocation = mLocation;
        this.mCategory = mCategory;
        this.mMethodOfPayment = mMethodOfPayment;
        this.mDate = mDate;
        this.mAmount = mAmount;
    }

    @Override
    public String toString() {
        return String.format("Location: " + mLocation +
                ", Category: " + mCategory +
                ", Method of Payment: " + mMethodOfPayment +
                ", Date: " + mDate.toString() +
                ", Amount: " + mAmount);
    }
}
