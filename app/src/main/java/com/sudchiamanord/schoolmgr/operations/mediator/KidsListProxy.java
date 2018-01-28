package com.sudchiamanord.schoolmgr.operations.mediator;

import android.util.Log;

import com.google.gson.Gson;
import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.info.KidInfo;
import com.sudchiamanord.schoolmgr.operations.mediator.requests.Action;
import com.sudchiamanord.schoolmgr.operations.mediator.requests.Details;
import com.sudchiamanord.schoolmgr.operations.mediator.requests.Request;
import com.sudchiamanord.schoolmgr.operations.results.DownloadResult;
import com.sudchiamanord.schoolmgr.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rita on 12/10/15.
 */
class KidsListProxy
{
    private final String TAG = KidsListProxy.class.getSimpleName();

    private HttpURLConnection httpConn;
    private String schoolYear;

    KidsListProxy (String requestURL) throws IOException
    {
        URL url = new URL (requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput (true);
        httpConn.setRequestMethod ("POST");
        httpConn.setConnectTimeout (10000);
        httpConn.setRequestProperty ("Content-Type", "application/json");
    }

    void setSchoolYear (String schoolYear)
    {
        this.schoolYear = schoolYear;
    }

    void request (String sessionKey) throws IOException
    {
        Gson gson = new Gson();
        Request request = new Request();
        request.setAction (Action.kidList.value());
        request.setSessionKey (sessionKey);
        Details details = new Details();
        details.setStato ("*");     // TODO: generalize this with *, 1 or 0
        request.setDetails (details);
        String dataRequest = gson.toJson (request);
        try {
            OutputStreamWriter wr = new OutputStreamWriter (httpConn.getOutputStream());
            wr.write (dataRequest);
            wr.flush();
        }
        catch (IOException e) {
            Log.e (TAG, "Problem in opening the connection", e);
            throw new IOException (e);
        }
    }

    DownloadResult getResponse() throws IOException
    {
        int responseCode = httpConn.getResponseCode();
        Log.d (TAG, "Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = new BufferedInputStream (httpConn.getInputStream());
            String response = Utils.convertStreamToString (is);
            //Log.d (TAG, "Response: " + response);

            is.close();
            httpConn.disconnect();

            try {
                JSONObject jsonResponse = new JSONObject (response);

                boolean res = jsonResponse.getBoolean ("res");
                if (!res) {
                    String num = jsonResponse.getString ("num");
                    switch (num) {
                        // TODO: 12/10/15: MANAGE ERRORS! (the download was not successful because res=false

                        default:
                            throw new JSONException ("Wrong num value " + num);
                    }
                }

                return new DownloadResult (true, R.string.downloadSuccessful, true,
                        extractKidsInfo (jsonResponse.getJSONArray ("tec")), schoolYear);
            }
            catch (JSONException e) {
                Log.e (TAG, "Problem in parsing the response", e);
                throw new IOException (e);
            }
        }

        throw new IOException ("Received response code " + responseCode + " instead of " +
                HttpURLConnection.HTTP_OK);
    }

    private List<KidInfo> extractKidsInfo (JSONArray tec) throws JSONException
    {
        List<KidInfo> kidsInfo = new ArrayList<>();

        Gson gson = new Gson();
        for (int i=0; i<tec.length(); i++) {
            JSONObject el = tec.getJSONObject (i);
            kidsInfo.add (gson.fromJson (el.toString(), KidInfo.class));
        }

        return kidsInfo;
    }
}
