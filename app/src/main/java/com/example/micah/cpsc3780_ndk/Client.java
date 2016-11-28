package com.example.micah.cpsc3780_ndk;
/**
 * Created by Micah on 2016-11-18.
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.AsyncTask;
import java.net.InetAddress;
import android.widget.TextView;
import android.util.Log;

public class Client extends AsyncTask<Void, Void, Void> {
    /**
     * MEMBER VARIABLES
     */

    String dstAddress;
    int dstPort;
    int m_sequenceNumber;
    int m_serverIndex;
    String user_name;
    String response = "";
    Boolean m_terminate = false;
    DatagramSocket UDPsocket = null;

    DataMessage messageToSend = null;
    DataMessage getRequestMessage = null;
    DataMessage ackMessage = null;
    Activity context;
    TextView chatmsg;

    public static String r_messages = "";

    Client(
            Activity context,
            int serverIndex,
            int port,
            String username) {
        this.m_serverIndex = serverIndex;
        this.dstPort = port;
        this.user_name = username;
        this.context = context;
        this.dstAddress = Constants.serverIP;
        this.m_sequenceNumber = 0;

        chatmsg = (TextView) this.context.findViewById(R.id.chatmsg);

        try {
            this.UDPsocket = new DatagramSocket(dstPort);
        } catch (IOException e) {
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }
    }

    public void sendMessage(DataMessage message) {
        this.messageToSend = message;
    }

    private void sendOverUDP ()
    {
        if(this.messageToSend != null) {
            try {
                Log.i("Server address", this.dstAddress);
                InetAddress IPAddress =  InetAddress.getByName(this.dstAddress);
                byte[] send_data = new byte[256];
                send_data = this.messageToSend.asAString().getBytes();
                DatagramPacket send_packet = new DatagramPacket(send_data, this.messageToSend.asAString().length(), IPAddress, this.dstPort);
                this.UDPsocket.send(send_packet);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }
            this.messageToSend = null;
        }

    }
    private void sendGetRequestsOverUDP ()
    {
        if(this.getRequestMessage != null) {
            try {
                Log.i("Server address", this.dstAddress);
                InetAddress IPAddress =  InetAddress.getByName(this.dstAddress);
                byte[] send_data = new byte[256];
                send_data = this.getRequestMessage.asAString().getBytes();
                DatagramPacket send_packet = new DatagramPacket(send_data, this.getRequestMessage.asAString().length(), IPAddress, this.dstPort);
                this.UDPsocket.send(send_packet);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }
            this.getRequestMessage = null;
        }

    }

    private void sendAckMessagesOverUDP ()
    {
        if(this.ackMessage != null) {
            try {
                InetAddress IPAddress =  InetAddress.getByName(this.dstAddress);
                byte[] send_data = new byte[256];
                send_data = this.ackMessage.asAString().getBytes();
                DatagramPacket send_packet = new DatagramPacket(send_data, this.ackMessage.asAString().length(), IPAddress, this.dstPort);
                this.UDPsocket.send(send_packet);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }
            this.ackMessage = null;
        }

    }

    public void receiveOverUDP ()
    {
        try {
            byte[] receiveData = new byte[256];

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            this.UDPsocket.receive(receivePacket);
            String receivedMessage = new String(receivePacket.getData());

            DataMessage message = new DataMessage(receivedMessage);

            if (receivedMessage.length() > 0) {
                int messageType  = message.viewMessageType();

                switch (messageType)
                {
                    case Constants.mt_CLIENT_CONNECT:
                    {
                        response = message.viewPayload();
                        this.context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                r_messages = r_messages + response + "\n";
                                chatmsg.setText(r_messages);
                            }
                        });
                        break;
                    }
                    case Constants.mt_CLIENT_DISCONNECT:
                    {
                        // Do nothing
                        break;
                    }
                    case Constants.mt_CLIENT_SEND:
                    {
                        response = message.viewSourceIdentifier() + " says: " + message.viewPayload();
                        response = message.viewPayload();
                        Log.i("CLIENT SEND", response);

                        DataMessage sendAckMessage = new DataMessage(
                                message.viewSequenceNumber(),
                                Constants.mt_CLIENT_ACK,
                                this.user_name,
                                Constants.serverIndexToServerName(this.m_serverIndex),
                                "blank"
                        );

                        this.ackMessage = sendAckMessage;
                        this.sendAckMessagesOverUDP();
                        this.context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                r_messages = r_messages + response + "\n";
                                chatmsg.setText(r_messages);
                            }
                        });
                        break;
                    }
                    default:
                    {
                        // unexpected type
                        assert(false);
                    }
                }
            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }
    }

    public void disconnect() {
        m_terminate = true;
    }

    public int sequenceNumber () { return ++this.m_sequenceNumber; }

    @Override
    protected Void doInBackground(Void... arg0) {
        String initiateMessage = this.user_name + " has connected.";
        String destination = Constants.serverIndexToServerName(this.m_serverIndex);

        DataMessage connectionMessage =
                new DataMessage(
                        this.sequenceNumber(),
                        Constants.mt_CLIENT_CONNECT,
                        this.user_name,
                        destination,
                        initiateMessage);

        this.messageToSend = connectionMessage;
        this.sendOverUDP();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!m_terminate) {
                    try {
                        Thread.sleep(1000);
                        receiveOverUDP();
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!m_terminate) {
                    try {
                        Thread.sleep(50);
                        sendOverUDP();
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!m_terminate) {
                    try {
                        Thread.sleep(1000);
                        DataMessage getRequest =
                            new DataMessage(
                                sequenceNumber(),
                                Constants.mt_CLIENT_GET,
                                user_name,
                                Constants.serverIndexToServerName(m_serverIndex),
                                "blank");
                        getRequestMessage = getRequest;
                        sendGetRequestsOverUDP();
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

}