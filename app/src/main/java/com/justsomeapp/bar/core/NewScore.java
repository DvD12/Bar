package com.justsomeapp.bar.core;

import android.os.AsyncTask;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewScore
{
    JSONParser jsonParser = new JSONParser();

    private static String url_create_score = "http://www.justsomeapp.co.nf/phpdb/createscore.php";
    private int today;
    private String name;
    private int score;

    public boolean success;
    public boolean finishedSubmitting;

    private static final String TAG_SUCCESS = "success";

    public NewScore(String name, int score)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        today = Integer.parseInt(dateFormat.format(date));
        this.name = name;
        this.score = score;

        success = true;
        finishedSubmitting = false;

        new CreateNewScore().execute();
    }

    public void execute(String name, int score)
    {
        this.name = name;
        this.score = score;
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        today = Integer.parseInt(dateFormat.format(date));
        this.name = name;
        this.score = score;

        success = true;
        finishedSubmitting = false;
        new CreateNewScore().execute();
    }

    class CreateNewScore extends AsyncTask<String, String, String>
    {
        protected void onPreExecute()
        {

        }

        protected String doInBackground(String... args)
        {
            return "";
            /*
            // INITIAL STATE: success = T, finishedSubmitting = F
            success = true;
            finishedSubmitting = false;

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("score", score + ""));
            params.add(new BasicNameValuePair("data", today + ""));

            JSONObject json = jsonParser.makeHttpRequest(url_create_score, "POST", params);

            if (json == null)
            {
                // NO CONNECTION STATE: success = F, finishedSubmitting = T
                success = false; finishedSubmitting = true;
                return null;
            }

            try
            {
                int success = json.getInt(TAG_SUCCESS);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            // ALL OK STATE: success = T, finishedLoading = T
            success = true; finishedSubmitting = true;

            return null;

             */
        }

        protected void onPostExecute() {}
    }
}
