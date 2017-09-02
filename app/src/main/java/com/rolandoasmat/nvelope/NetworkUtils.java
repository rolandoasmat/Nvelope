package com.rolandoasmat.nvelope;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by rolandoasmat on 9/2/17.
 */

public class NetworkUtils {
    public static void httpRequest(String url, Context context, final CompletionClosure<String> closure) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                closure.onComplete(response);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                closure.onComplete(null);
            }
        });
        queue.add((Request)stringRequest);
    }
}
