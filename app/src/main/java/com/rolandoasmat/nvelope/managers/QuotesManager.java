package com.rolandoasmat.nvelope.managers;

import com.rolandoasmat.nvelope.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by rolandoasmat on 9/3/17.
 */

/*
https://theysaidso.com/api/#qodcatopt
https://quotes.rest/#!/quote/get_quote_categories
*/
public class QuotesManager {
    private static String BASE_URL = "http://quotes.rest/qod.json";

    private QuotesManager() { }

    public static String getInspirationalQuoteOfTheDay() throws Exception {
        String url = BASE_URL + "?category=inspire";
        String jsonString = NetworkUtils.httpRequest(url);
        return parseJson(jsonString);
    }

    private static String parseJson(String jsonString) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject contents = jsonObject.getJSONObject("contents");
        JSONArray quotes = contents.getJSONArray("quotes");
        if(quotes.length() > 0){
            JSONObject quote = quotes.getJSONObject(0);
            String quoteString = quote.getString("quote");
            return quoteString;
        }
        throw new Exception("No quotes found.");
    }
}
