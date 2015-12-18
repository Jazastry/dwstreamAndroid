package com.jazastry.diamondwaystreaming.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jazastry.diamondwaystreaming.R;

/**
 * Created by yasen.lazarov on 18-12-2015.
 */
public class RequestHandler extends AsyncTask<Void, Void, String> {
    private void makeGetRequest(TextView view, Context context) {
        final TextView mTextView = view;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://streaming.dwbn.org/streaming/schedule.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mTextView.setText("Response is: " + response.substring(0, response.length()));
                        Log.v("FROm HTTP REQUEST", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    protected String doInBackground(Void... params) {
        return null;
    }
}
