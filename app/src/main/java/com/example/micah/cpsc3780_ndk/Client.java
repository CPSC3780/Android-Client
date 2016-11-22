package com.example.micah.cpsc3780_ndk;
/**
 * Created by Micah on 2016-11-18.
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.util.Log;
import java.net.InetAddress;

public class Client extends AsyncTask<Void, Void, Void> {
    /**
     * MEMBER VARIABLES
     */

    String dstAddress;
    int dstPort;
    String user_name;
    String response = "";
    Boolean m_terminate = false;
    DatagramSocket UDPsocket = null;

    DataMessage messageToSend = null;

    Client(String addr, int port, String username) {
        this.dstAddress = addr;
        this.dstPort = port;
        this.user_name = username;

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

    public String receiveOverUDP ()
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
                        break;
                    }
                    case Constants.mt_CLIENT_DISCONNECT:
                    {
                        // Do nothing
                        break;
                    }
                    case Constants.mt_CLIENT_PRIVATE_CHAT:
                    {
                        response = message.viewSourceIdentifier() + " whispers: " + message.viewPayload();
                        break;
                    }
                    case Constants.mt_CLIENT_TARGET_NOT_FOUND:
                    {
                        response = "Server: Could not deliver message \"" + message.viewDestinationIdentifier() + "\"" + "\n";
                        break;
                    }
                    case Constants.mt_RELAY_CHAT:
                    {
                        response = message.viewSourceIdentifier() + " says: " + message.viewPayload() + "\n";
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

        return response;
    }

    public void disconnect() {
        m_terminate = true;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        String initiateMessage = this.user_name + " has connected.";
        String destination = "broadcast";

        DataMessage connectionMessage = new DataMessage(initiateMessage, this.user_name, destination, Constants.mt_CLIENT_CONNECT);
        this.messageToSend = connectionMessage;
        this.sendOverUDP();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!m_terminate) {
                    try {
                        Thread.sleep(50);
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
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

}