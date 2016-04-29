package com.jacob.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.sax.StartElementListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.securepreferences.SecurePreferences;
import com.facebook.FacebookSdk;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Details extends AppCompatActivity {

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_details);
            prefs = new SecurePreferences(MainActivity.This, "01827711125", "token");
            MobileServiceUser user = loadUserTokenCache(prefs);

            getJSONAzure taskAzure = new getJSONAzure();
            taskAzure.execute(user.getAuthenticationToken());
            String Json = taskAzure.get();

            JSONArray topArray = new JSONArray(Json);
            JSONObject topObject = topArray.getJSONObject(0);
            JSONArray innerArray = topObject.getJSONArray("user_claims");

            JSONObject innerObject = innerArray.getJSONObject(2);
            TextView textView = (TextView) findViewById(R.id.name);
            String name = innerObject.getString("val");
            textView.setText(name);

            JSONObject innerObject2 = innerArray.getJSONObject(7);
            Date birthday = new Date(innerObject2.getString("val"));
            String[] birthdayS = birthday.toString().split(" ");
            Date today = new Date();
            String[] todayS = today.toString().split(" ");
            TextView textView1 = (TextView) findViewById(R.id.birth);
            if (birthdayS[1].equals(todayS[1]) && birthdayS[2].equals(todayS[2])) {
                textView1.setText("It is your birthday!");
            }
            else {
                textView1.setText("It is not your birthday");
            }

            String Facebook = topObject.getString("access_token");
            getJSONFacebook taskFacebook = new getJSONFacebook();
            taskFacebook.execute(Facebook);
            Json = taskFacebook.get();

            topObject = new JSONObject(Json);
            JSONObject middleObject = topObject.getJSONObject("picture");
            innerObject = middleObject.getJSONObject("data");
            String pictureURL = innerObject.getString("url");

            ImageView imageView = (ImageView)findViewById(R.id.profile);
            Picasso.with(this).load(pictureURL).into(imageView);
        }
        catch (Exception e) {
            Log.e("Error in Details: ", e.toString());
        }
    }

    private MobileServiceUser loadUserTokenCache(SharedPreferences prefs) {
        String userId = prefs.getString("uid", "undefined");
        if (userId == "undefined")
            userId = "";
        String token = prefs.getString("tkn", "undefined");
        if (token == "undefined")
            token = "";

        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);

        return user;
    }

    public void onClickExit(View view) {
        finish();
    }

    public void onClickSignOut(View view) {
        trimCache();
        finish();
    }

    public static void trimCache() {
        try {
            File dir = new File("/data/user/0/com.jacob.authentication/");
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            Log.e("Deletion Error: ", e.toString());
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public class getJSONAzure extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... Parameters) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://mobilecomputingauthentication.azurewebsites.net/.auth/me");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.addRequestProperty("X-ZUMO-AUTH", Parameters[0]);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                    return null;
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    Log.v("Line: ", "NULL ERROROR");
                    return null;
                }
                return buffer.toString();

            } catch (MalformedURLException ex) {
                Log.e("Error", "Malformed URL ");
            } catch (IOException ex) {
                Log.e("Error", "IO Exception ");
            } catch (Exception e) {
                Log.d("Error", String.valueOf(e) + ": error");
            }
            return null;
        }
    }

    public class getJSONFacebook extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... Parameters) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://graph.facebook.com/v2.6/me?fields=id%2Cname%2Cpicture.width(1000)&access_token="+Parameters[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                    return null;
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    Log.v("Line: ", "NULL ERROROR");
                    return null;
                }
                return buffer.toString();

            } catch (MalformedURLException ex) {
                Log.e("Error", "Malformed URL ");
            } catch (IOException ex) {
                Log.e("Error", "IO Exception ");
            } catch (Exception e) {
                Log.d("Error", String.valueOf(e) + ": error");
            }
            return null;
        }
    }

    public void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
}