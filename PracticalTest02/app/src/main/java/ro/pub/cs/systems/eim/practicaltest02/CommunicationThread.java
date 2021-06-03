package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommunicationThread extends Thread {

    private ServerThread serverThread = null;
    private Socket socket = null;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }
    @Override
    public void run() {
        if (socket == null){
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try{
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (POKEMON)");
            String pokemon = bufferedReader.readLine();
            if (pokemon == null || pokemon.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
            String pageSourceCode = "";
            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.WEB_SERVICE_ADDRESS).newBuilder();
            String url = urlBuilder.build().toString();

            url = url.concat(pokemon);
            Log.e(Constants.TAG,url );



            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                pageSourceCode = response.body().string();
            }
            if (pageSourceCode == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            } else {
                Log.i(Constants.TAG, pageSourceCode);
            }

            JSONObject reader = new JSONObject(pageSourceCode);
            String res_abilities = "";
            JSONArray abilities = reader.getJSONArray("abilities");
            for(int i= 0; i < abilities.length(); i++){
                JSONObject ability = abilities.getJSONObject(i);
                res_abilities = res_abilities.concat(ability.getJSONObject("ability").getString("name")+",");
            }
//            printWriter.println(res_abilities);
//            printWriter.flush();

            JSONArray types = reader.getJSONArray("types");
            String res_types = "";
            for(int i= 0; i < types.length(); i++){
                String type = types.getJSONObject(i).getJSONObject("type").getString("name");
                res_types = res_types.concat(type+",");
            }

            printWriter.println(res_abilities+" | "+res_types);
            printWriter.flush();

//            JSONArray abilities = reader.getJSONArray("abilities").;
            Log.e(Constants.TAG,res_abilities+"|"+res_types );

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}