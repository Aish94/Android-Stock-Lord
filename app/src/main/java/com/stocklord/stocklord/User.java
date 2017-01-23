package com.stocklord.stocklord;

import java.util.HashMap;

/**
 * Created by aishwaryaverghese on 1/15/17.
 */

public class User
{
    public String name;
    public String email; //unique ID
    public float cash;
    public HashMap company_stocks;

    public User()
    {

    }

    public User(String name, String email, float cash) {
        this.name = name;
        this.email = email;
        this.cash = cash;
        company_stocks = new HashMap();
        company_stocks.put("YHOO",new Integer(0));
        company_stocks.put("GOOG",new Integer(0));
        company_stocks.put("AMZN",new Integer(0));
        company_stocks.put("MSFT",new Integer(0));
        company_stocks.put("AAPL",new Integer(0));
        company_stocks.put("FB",new Integer(0));
        company_stocks.put("TWTR",new Integer(0));
        company_stocks.put("ORCL",new Integer(0));
        company_stocks.put("SNDK",new Integer(0));
        company_stocks.put("CSCO",new Integer(0));
    }
}
