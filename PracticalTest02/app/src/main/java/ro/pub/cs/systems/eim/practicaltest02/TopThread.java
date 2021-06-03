package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.Integer.min;

public class TopThread extends Thread{

    private TextView textView3 = null;

    public TopThread(TextView textView3) {
        this.textView3 = textView3;
    }

    @Override
    public void run() {
        String pageSourceCode = "";
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.WEB_SERVICE_ADDRESS).newBuilder();
        String url = urlBuilder.build().toString();

        Log.e(Constants.TAG,url );

        Request request = new Request.Builder()
                .url(url)
                .build();
        try (
                Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);

            Headers responseHeaders = response.headers();
            pageSourceCode = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (pageSourceCode == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
            return;
        } else {
            Log.i(Constants.TAG, pageSourceCode);
        }

        try {
            JSONObject reader = new JSONObject(pageSourceCode);
            JSONArray data = reader.getJSONArray("results");
            String result = "";
            for(int i= 0; i < min(data.length(),20);i++){
                String name = data.getJSONObject(i).getString("name");
                result = result.concat(name+",");
            }
            textView3.setText(result);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }
}


