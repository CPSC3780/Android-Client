package com.example.micah.cpsc3780_ndk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.IntentFilter;
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
import android.util.Log;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView chatmsg;
    LinearLayout loginUI, chatUI;
    EditText editTextUsername, editTextToSend;
    Button buttonConnect, buttonClear, buttonSend, buttonDisconnect, buttonBluetooth;
    RadioGroup radioGroup;
    int serverPort = -1;
    int serverIndex = -1;
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    public static String r_messages = "";
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
        editTextUsername = (EditText) findViewById(R.id.usernameEditText);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        buttonBluetooth = (Button) findViewById(R.id.connectBluetooth);
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
                        Toast.makeText(MainActivity.this,
                                "Alpha Server Selected",
                                Toast.LENGTH_LONG).show();
                        break;
                    case R.id.radio_bravo:
                        serverIndex = Constants.charToServerIndex('B');
                        serverPort = Constants.serverIndexToListeningPort(serverIndex);
                        Toast.makeText(MainActivity.this,
                                "Bravo Server Selected",
                                Toast.LENGTH_LONG).show();
                        break;
                    case R.id.radio_charlie:
                        serverIndex = Constants.charToServerIndex('C');
                        serverPort = Constants.serverIndexToListeningPort(serverIndex);
                        Toast.makeText(MainActivity.this,
                                "Charlie Server Selected",
                                Toast.LENGTH_LONG).show();
                        break;
                    case R.id.radio_delta:
                        serverIndex = Constants.charToServerIndex('D');
                        serverPort = Constants.serverIndexToListeningPort(serverIndex);
                        Toast.makeText(MainActivity.this,
                                "Delta Server Selected",
                                Toast.LENGTH_LONG).show();
                        break;
                    case R.id.radio_echo:
                        serverIndex = Constants.charToServerIndex('E');
                        serverPort = Constants.serverIndexToListeningPort(serverIndex);
                        Toast.makeText(MainActivity.this,
                                "Charlie Server Selected",
                                Toast.LENGTH_LONG).show();
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
                if ((serverIndex == -1) || (serverPort == -1)) {
                    Toast.makeText(MainActivity.this, "Please select a server to connect to",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                r_messages = "";

                chatmsg.setText(r_messages);
                loginUI.setVisibility(View.GONE);
                chatUI.setVisibility(View.VISIBLE);

                myClient = new Client(
                        MainActivity.this, serverIndex, serverPort, textUsername, false);
                myClient.execute();
            }
        });
        buttonBluetooth.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("Bluetooth protocol ", "initialized");
                String textUsername = editTextUsername.getText().toString();
//
//                if (textUsername.equals("")) {
//                    Toast.makeText(MainActivity.this, "Please enter a username",
//                            Toast.LENGTH_LONG).show();
//                    return;
//                }
//                if ((serverIndex == -1) || (serverPort == -1)) {
//                    Toast.makeText(MainActivity.this, "Please select a server to connect to",
//                            Toast.LENGTH_LONG).show();
//                    return;
//                }
//                r_messages = "";
//
//                chatmsg.setText(r_messages);
//                loginUI.setVisibility(View.GONE);
//                chatUI.setVisibility(View.VISIBLE);
                registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                if(adapter != null && adapter.isDiscovering()){
                    adapter.cancelDiscovery();
                }
                adapter.startDiscovery();
//                myClient = new Client(
//                        MainActivity.this, serverIndex, serverPort, textUsername, false);
//                myClient.execute();
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
                String destination = "broadcast";

                String [] parts = messageRelay.split(" ", 3);
                String temp = parts[0];

                Log.i("Command",temp);

                if ((temp.equals("/message")) || (temp.equals("/m")))
                {
                    destination = parts[1];

                }
                else
                {
                    Toast.makeText(MainActivity.this,
                            "Invalid command. (Use '/m' || '/message' <target> <message>)",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                DataMessage message = new DataMessage(
                        myClient.sequenceNumber(),
                        Constants.mt_CLIENT_SEND,
                        myClient.user_name,
                        destination,
                        parts[2]);
                r_messages = r_messages + "You say: " + parts[2] + "\n";
                chatmsg.setText(r_messages);
                myClient.sendMessage(message);
            }
        });

        buttonClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextUsername.setText("");
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
    private BluetoothSocket socket;
    private OutputStreamWriter os;
    private InputStream is;
    private BluetoothDevice remoteDevice;
    private BroadcastReceiver discoveryResult = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            android.util.Log.e("TrackingFlow", "WWWTTTFFF");
            unregisterReceiver(this);
            remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            new Thread(reader).start();
        }
    };

    private Runnable reader = new Runnable() {

        @Override
        public void run() {
            try {
                android.util.Log.e("TrackingFlow", "Found: " + remoteDevice.getName());
                UUID uuid = UUID.fromString("4e5d48e0-75df-11e3-981f-0800200c9a66");
                socket = remoteDevice.createRfcommSocketToServiceRecord(uuid);
                socket.connect();
                android.util.Log.e("TrackingFlow", "Connected...");
                os = new OutputStreamWriter(socket.getOutputStream());
                is = socket.getInputStream();
                android.util.Log.e("TrackingFlow", "WWWTTTFFF34243");
                new Thread(writter).start();
                android.util.Log.e("TrackingFlow", "WWWTTTFFF3wwgftggggwww4243: " + true);
                int bufferSize = 1024;
                int bytesRead = -1;
                byte[] buffer = new byte[bufferSize];
                //Keep reading the messages while connection is open...
                while(true){
                    android.util.Log.e("TrackingFlow", "WWWTTTFFF3wwwww4243");
                    final StringBuilder sb = new StringBuilder();
                    bytesRead = is.read(buffer);
                    if (bytesRead != -1) {
                        String result = "";
                        while ((bytesRead == bufferSize) && (buffer[bufferSize-1] != 0)){
                            result = result + new String(buffer, 0, bytesRead - 1);
                            bytesRead = is.read(buffer);
                        }
                        result = result + new String(buffer, 0, bytesRead - 1);
                        sb.append(result);
                    }

                    android.util.Log.e("TrackingFlow", "Read: " + sb.toString());

                    //Show message on UIThread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (IOException e) {e.printStackTrace();}
        }
    };

    private Runnable writter = new Runnable() {

        @Override
        public void run() {
            int index = 0;
            while (true) {
                try {
                    os.write("Message From Client" + (index++) + "\n");
                    os.flush();
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    static public String addMessageLog (String msg) {
        r_messages = r_messages + msg;
        return r_messages;
    }
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
