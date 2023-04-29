package com.justsomeapp.bar.core;

import android.os.AsyncTask;

//import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


// http://www.androidhive.info/2012/05/how-to-connect-android-with-php-mysql/
public class AllScores
{
    JSONParser jParser = new JSONParser();

    // makeHttpRequest doesn't like underscores apparently
    private static String url_all_scores         = "http://www.justsomeapp.co.nf/phpdb/getallscores.php";
    private static String url_all_scores_by_date = "http://www.justsomeapp.co.nf/phpdb/getallscoresbydate.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SCORES = "scores";
    private static final String TAG_SCORE = "score";
    private static final String TAG_NAME = "name";
    private static final String TAG_DATE = "data";

    public ArrayList<String[]> scoresList;
    public ArrayList<String[]> scoresListToday;

    JSONArray scores = null;
    public boolean success;
    public boolean finishedLoading;

    JSONObject json;

    public AllScores()
    {
        scoresList = new ArrayList<>();
        scoresListToday = new ArrayList<>();
        json = null;
        success = true;
        finishedLoading = false;
    }

    public void execute()
    {
        new LoadAllScores().execute();
    }

    class LoadAllScores extends AsyncTask<String, String, String>
    {
        protected void onPreExecute()
        {
            // INITIAL STATE: success = T, finishedLoading = F
            scoresList.clear();
            scoresListToday.clear();
            success = true;
            finishedLoading = false;
            ////////////////////////
        }
        protected String doInBackground(String... args)
        {
            return "";
            /*
            List<NameValuePair> params = new ArrayList<>();
            // Results are already ordered by score by SQL command
            // so we're guaranteed that the list we create is populated with the greatest scores first
            json = jParser.makeHttpRequest(url_all_scores, "GET", params);

            if (json == null)
            {
                // NO CONNECTION STATE: success = F, finishedLoading = T
                success = false; finishedLoading = true;

                return null;
            }

            try
            {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1)
                {
                    scores = json.getJSONArray(TAG_SCORES);

                    for (int i = 0; i < scores.length(); i++)
                    {
                        JSONObject c = scores.getJSONObject(i);

                        String name = c.getString(TAG_NAME);
                        String score = c.getString(TAG_SCORE);

                        scoresList.add(new String[]{name, score});
                    }
                }
                else
                {

                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            params = new ArrayList<>();
            // Results are already ordered by score by SQL command
            // so we're guaranteed that the list we create is populated with the most recent date and greatest scores first
            json = jParser.makeHttpRequest(url_all_scores_by_date, "GET", params);

            if (json == null)
            {
                // NO CONNECTION STATE: success = F, finishedLoading = T
                success = false; finishedLoading = true;
                return null;
            }

            try
            {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1)
                {
                    scores = json.getJSONArray(TAG_SCORES);

                    JSONObject j = scores.getJSONObject(0);
                    int date = Integer.parseInt(j.getString(TAG_DATE)); // most recent day

                    for (int i = 0; i < scores.length(); i++)
                    {
                        JSONObject c = scores.getJSONObject(i);
                        if (Integer.parseInt(c.getString(TAG_DATE)) < date) { break; } // we only want most recent (i.e. last day) scores

                        String name = c.getString(TAG_NAME);
                        String score = c.getString(TAG_SCORE);

                        scoresListToday.add(new String[]{name, score});
                    }
                }
                else
                {}
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            // ALL OK STATE: success = T, finishedLoading = T
            success = true; finishedLoading = true;

            return null;
             */
        }

        protected void onPostExecute()
        {}
    }
}
