package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText editTextViewPokemon = null;
    private Button buttonSubmit = null;

    private ImageView imageView = null;

    private TextView textViewAbilities = null;
    private TextView textViewTypes = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private SubmitButtonClickListener submitButtonClickListener = new SubmitButtonClickListener();
    private class SubmitButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = "localhost";
            String clientPort = "5656";
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String pokemon = editTextViewPokemon.getText().toString();
            if (pokemon == null || pokemon.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (POKEMON) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }
            textViewAbilities.setText("");
            textViewTypes.setText("");

            clientThread = new ClientThread(clientAddress, 5656, pokemon,textViewAbilities,textViewTypes);
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");
        setContentView(R.layout.activity_practical_test02_main);

        serverThread = new ServerThread(5656);
        if (serverThread.getServerSocket() == null) {
            Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
            return;
        }
        serverThread.start();
        textViewAbilities =findViewById(R.id.textViewAbilities);
        textViewTypes = findViewById(R.id.textViewTypes);

        editTextViewPokemon = (EditText)findViewById(R.id.editTextViewPokemon);
        buttonSubmit = (Button)findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(submitButtonClickListener);



    }
}