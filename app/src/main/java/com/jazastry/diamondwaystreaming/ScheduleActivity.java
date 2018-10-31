package com.jazastry.diamondwaystreaming;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.jazastry.diamondwaystreaming.classes.ScheduleXMLParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_schedule);



    makeGetRequest();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_schedule, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_streaming) {
      Intent intent = new Intent(this, StreamingActivity.class);
      startActivity(intent);
      return true;
    } else if (id == R.id.action_home) {
      Intent intent = new Intent(this, MainActivity.class);
      startActivity(intent);
      return true;
    } else if (id == R.id.action_settings) {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void parseResponse(String input) throws IOException, XmlPullParserException, ParseException {
    List res = ScheduleXMLParser.parse(input);
  }

  private void makeGetRequest() {
    final TextView mTextView = (TextView) findViewById(R.id.schedule_text_view);
    String url = "http://streaming.dwbn.org/streaming/schedule.php";

    // Instantiate the RequestQueue.
    RequestQueue queue = Volley.newRequestQueue(this);

    // Request a string response from the provided URL.
    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            // Display the first 500 characters of the response string.
            try {
              parseResponse(response);
            } catch (IOException e) {
              e.printStackTrace();
            } catch (XmlPullParserException e) {
              e.printStackTrace();
            } catch (ParseException e) {
              e.printStackTrace();
            }
            mTextView.setText("Response is: " + response);
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
}
