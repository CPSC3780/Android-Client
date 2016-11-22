package com.example.micah.cpsc3780_ndk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView response, chatmsg;
    LinearLayout loginUI, chatUI;
    EditText editTextAddress, editTextPort, editTextUsername, editTextToSend;
    Button buttonConnect, buttonClear, buttonSend, buttonDisconnect;

    String r_messages = "";

    Client myClient = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    /**
     * App entry point, here we are instantiating UI elements
     * and setting event handlers accordingly
     */
        loginUI = (LinearLayout) findViewById(R.id.loginUI);
        chatUI = (LinearLayout) findViewById(R.id.chatUI);

        // Login Panel
        editTextAddress = (EditText) findViewById(R.id.addressEditText);
        editTextPort = (EditText) findViewById(R.id.portEditText);
        editTextUsername = (EditText) findViewById(R.id.usernameEditText);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        buttonClear = (Button) findViewById(R.id.clearButton);

        // Chat Panel

        chatmsg = (TextView) findViewById(R.id.chatmsg);
        buttonSend = (Button) findViewById(R.id.send);
        editTextToSend= (EditText) findViewById(R.id.say);
        buttonDisconnect = (Button) findViewById(R.id.disconnect);

        buttonConnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String textUsername = editTextUsername.getText().toString();

                if (textUsername.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter a username",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String textAddress = editTextAddress.getText().toString();
                if (textAddress.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter an address to connect to",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String portText = editTextPort.getText().toString();
                if (portText.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter the port",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                r_messages = "";

                chatmsg.setText(r_messages);
                loginUI.setVisibility(View.GONE);
                chatUI.setVisibility(View.VISIBLE);

                myClient = new Client(
                        textAddress, Integer.parseInt(portText), textUsername);
                myClient.execute();
            }
        });

        buttonSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextToSend.getText().toString().equals("")) {
                    return;
                }

                if (myClient == null) {
                    return;
                }

                String messageRelay = editTextToSend.getText().toString();
                String textUsername = editTextUsername.getText().toString();
                String destination = "broadcast";

                DataMessage message = new DataMessage(messageRelay, textUsername, destination, Constants.mt_RELAY_CHAT);
                chatmsg.setText(messageRelay);
                myClient.sendMessage(message);
            }
        });

        buttonClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextUsername.setText("");
                editTextAddress.setText("");
                editTextPort.setText("");
            }
        });

        buttonDisconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myClient == null) {
                    return;
                }
                myClient.disconnect();
                chatUI.setVisibility(View.GONE);
            }
        });
    }
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
