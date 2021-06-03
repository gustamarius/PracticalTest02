package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClientThread extends Thread {

    private String clientAddress = null;
    private int clientPort = 0;
    private String pokemon = null;
    private TextView textViewAbilities = null;
    private TextView textViewTypes = null;

    private Socket socket;

    public ClientThread(String clientAddress, int clientPort, String pokemon, TextView textViewAbilities, TextView textViewTypes) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.pokemon = pokemon;
        this.textViewAbilities = textViewAbilities;
        this.textViewTypes = textViewTypes;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(clientAddress, clientPort);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(pokemon);
            printWriter.flush();
            String information;
            while ((information = bufferedReader.readLine()) != null) {
                final String abilities = information.split("\\.")[0];
                Log.e(Constants.TAG, abilities);
                textViewAbilities.setText(abilities);
                final String types = information.split("\\.")[1];
                Log.e(Constants.TAG, types);
                textViewTypes.setText(types);
//                Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);

//                textViewAbilities.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        textViewAbilities.setText(abilities);
//                    }
//                });
//                textViewTypes.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        textViewTypes.setText(types);
//                    }
//                });

            }



        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
