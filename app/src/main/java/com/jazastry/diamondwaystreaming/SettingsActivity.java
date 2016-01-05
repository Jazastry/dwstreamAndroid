package com.jazastry.diamondwaystreaming;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class SettingsActivity extends AppCompatActivity {
    private String crsftoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button button = (Button) findViewById(R.id.submit_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                new LoginTask().execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
        } else if (id == R.id.action_schedule) {
            Intent intent = new Intent(this, ScheduleActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getCsrfToken(URLConnection connection) throws IOException, KeyManagementException {
        String cookies = connection.getHeaderField("Set-Cookie");
        return getCookie("csrftoken", cookies);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private Boolean postLoginInfo() throws IOException, KeyManagementException {
        String loginUrl = "https://sso.dwbn.org/accounts/login/";
        URL url = new URL(loginUrl);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        // get values of email and password
        final EditText emailEditText = (EditText) findViewById(R.id.email);
        final EditText passwordEditText = (EditText) findViewById(R.id.password);
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // transform to URLencoded string
        String crsftoken = getCsrfToken(urlConnection);
        String urlParameters =
                "csrfmiddlewaretoken=" + URLEncoder.encode(crsftoken, "UTF-8") +
                "username=" + URLEncoder.encode(email, "UTF-8") +
                "password=" + URLEncoder.encode(password, "UTF-8");

        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("Content-Length", "" +
                Integer.toString(urlParameters.getBytes().length));
        urlConnection.setFixedLengthStreamingMode(urlParameters.getBytes().length);
        urlConnection.setUseCaches (false);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        //send the POST out
        PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
        out.print(urlParameters);
        out.close();

        //build the string to store the response text from the server
        String response= "";

        //start listening to the stream
        Scanner inStream = new Scanner(urlConnection.getInputStream());

        //process the stream and store it in StringBuilder
        while(inStream.hasNextLine()){
            response+=(inStream.nextLine());
        }
        Log.v("postLoginInfo : ", response);

        return true;
    }

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private String crsftoken = "";
        private Boolean loggedIn = false;

        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected Boolean doInBackground(Void... params) {
            try {
                return postLoginInfo();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            return true;
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(Boolean loggedIn) {

        }
    }

    private String getCookie(String name, String cookies) {
        String result = "";
        String[] cookiesArr = cookies.split(";");

        for (int i = 0; i < cookiesArr.length; i++) {
            if (cookiesArr[i].contains(name)) {
                result = cookiesArr[i].replaceAll("\\s+","");
                break;
            } else {
                continue;
            }
        }

        return result;
    }
}
