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
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private static final String URL_LOGIN = "https://sso.dwbn.org/accounts/login/";
    private static final String CHAR_SET_UTF_8 = "UTF-8";
    private String email = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onButtonClicked(View view) {
        // get values of email and password
        final EditText emailEditText = (EditText) findViewById(R.id.email);
        final EditText passwordEditText = (EditText) findViewById(R.id.password);
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();


        if(view.getId() == R.id.submit_button) {
            new LoginTask().execute();
        }

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
        String crsftokenCookie = getCookie("csrftoken", cookies);

        String[] crsftokenArr = crsftokenCookie.split("=");
        /*
        * crsftokenArr = [crsftoken, <actual token value>];
        * */
        return crsftokenArr[1];
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private String getLoginCrsfToken() throws IOException, KeyManagementException {
        HttpURLConnection urlConnection = null;

        //Set the default cookie manager used for all connections.
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

        URL url = new URL(URL_LOGIN);
        urlConnection = (HttpURLConnection) url.openConnection();

        // Get csrf token from Set-Cookie header
        String csrfToken = getCsrfToken(urlConnection);

        // transform to URLencoded string
        String urlParameters =
                "csrfmiddlewaretoken=" + URLEncoder.encode(csrfToken, CHAR_SET_UTF_8) +
                        "&username=" + URLEncoder.encode(email, CHAR_SET_UTF_8) +
                        "&password=" + URLEncoder.encode(password, CHAR_SET_UTF_8);
        urlConnection.disconnect();

        return urlParameters;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Boolean postLoginInfoUrlConnection() throws IOException, KeyManagementException {
        String loginUrl = URL_LOGIN;

        HttpURLConnection urlConnection = null;

        //Set the default cookie manager used for all connections.
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

        URL url = new URL(loginUrl);
        urlConnection = (HttpURLConnection) url.openConnection();
        // Gather all cookies.
        List<String> cookies = urlConnection.getHeaderFields().get("Set-Cookie");

        // transform to URLencoded string
        String crsftoken = getCsrfToken(urlConnection);
        String urlParameters =
                "csrfmiddlewaretoken=" + URLEncoder.encode(crsftoken, CHAR_SET_UTF_8) +
                        "&username=" + URLEncoder.encode(email, CHAR_SET_UTF_8) +
                        "&password=" + URLEncoder.encode(password, CHAR_SET_UTF_8);

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Accept", "*/*");
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible )");
        urlConnection.setRequestProperty("Transfer-Encoding", "chunked");
        urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("Content-Encoding", "UTF-8");
        urlConnection.setConnectTimeout(5000);

        for (String cookie : cookies) {
            urlConnection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
        }

        urlConnection.setUseCaches(false);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
//        urlConnection.setDoInput(true);

        String meth = urlConnection.getRequestMethod().toString();
//            urlConnection.setRequestProperty("Accept-Charset", charSet);


        // Write parameters to request.
//        try (OutputStream output = urlConnection.getOutputStream()) {
//            output.write(urlParameters.getBytes(CHAR_SET_UTF_8));
//            output.flush();
//            output.close();
//        }
        OutputStream os = urlConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(urlParameters.toCharArray());
        writer.flush();
        writer.close();
        os.close();


        int responseCode = urlConnection.getResponseCode();

        InputStream error = ((HttpURLConnection) urlConnection).getErrorStream();
        String mess = error.toString();

        // Fire the request.
        InputStream response = urlConnection.getInputStream();

        StringBuilder headersBuilder = new StringBuilder();
        for (Map.Entry<String, List<String>> header : urlConnection.getHeaderFields().entrySet()) {
            headersBuilder.append(header.getKey() + "=" + header.getValue() + "\n\r");
        }
        String headers = headersBuilder.toString();
        Log.v("loginPost-headers : ", headers);

        return true;
    }

    private void postLoginHttpClient() {
//        HttpClient httpClient = new DefaultHttpClient();
//        // replace with your url
//        HttpPost httpPost = new HttpPost("www.example.com");
    }

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private String crsftoken = "";
        private Boolean loggedIn = false;

        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected Boolean doInBackground(Void... params) {
            try {
                loggedIn = postLoginInfoUrlConnection();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            return loggedIn;
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
