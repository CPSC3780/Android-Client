package com.example.micah.cpsc3780_ndk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.RadioGroup;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView chatmsg;
    LinearLayout loginUI, chatUI;
    EditText editTextAddress, editTextPort, editTextUsername, editTextToSend;
    Button buttonConnect, buttonClear, buttonSend, buttonDisconnect;
    RadioGroup radioGroup;
    String r_messages = "";

    int serverPort;
    int serverIndex;

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
        editTextUsername = (EditText) findViewById(R.id.usernameEditText);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        buttonClear = (Button) findViewById(R.id.clearButton);
        radioGroup = (RadioGroup) findViewById(R.id.server_radioGroup);

        // Chat Panel

        chatmsg = (TextView) findViewById(R.id.chatmsg);
        buttonSend = (Button) findViewById(R.id.send);
        editTextToSend= (EditText) findViewById(R.id.say);
        buttonDisconnect = (Button) findViewById(R.id.disconnect);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_alpha:
                        serverIndex = Constants.charToServerIndex('A');
                        serverPort = Constants.serverIndexToListeningPort(serverIndex);
                        break;
                    case R.id.radio_bravo:
                        serverIndex = Constants.charToServerIndex('B');
                        serverPort = Constants.serverIndexToListeningPort(serverIndex);
                        break;
                    case R.id.radio_charlie:
                        serverIndex = Constants.charToServerIndex('C');
                        serverPort = Constants.serverIndexToListeningPort(serverIndex);
                        break;
                    case R.id.radio_delta:
                        serverIndex = Constants.charToServerIndex('D');
                        serverPort = Constants.serverIndexToListeningPort(serverIndex);
                        break;
                    case R.id.radio_echo:
                        serverIndex = Constants.charToServerIndex('E');
                        serverPort = Constants.serverIndexToListeningPort(serverIndex);
                        break;
                    default:
                        serverIndex = -1;
                        serverPort = -1;
                        break;
                }
            }
        });

        buttonConnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String textUsername = editTextUsername.getText().toString();

                if (textUsername.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter a username",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                r_messages = "";

                chatmsg.setText(r_messages);
                loginUI.setVisibility(View.GONE);
                chatUI.setVisibility(View.VISIBLE);

                myClient = new Client(
                        MainActivity.this, serverIndex, serverPort, textUsername);
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

                r_messages = r_messages + "You say: " + messageRelay + "\n";

                DataMessage message = new DataMessage(messageRelay, textUsername, destination, Constants.mt_RELAY_CHAT);
                chatmsg.setText(r_messages);
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
