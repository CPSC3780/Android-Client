package com.example.micah.cpsc3780_ndk;
/**
 * Created by Micah on 2016-11-18.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.widget.TextView;
import java.net.InetAddress;

public class Client extends AsyncTask<Void, Void, Void> {
    /**
     * MEMBER VARIABLES
     */

    String dstAddress;
    int dstPort;
    String user_name;
    String response = "";
    TextView textResponse;
    Boolean m_terminate = false;
    DatagramSocket UDPsocket = null;

    Client(String addr, int port, String username,TextView textResponse) {
        this.dstAddress = addr;
        this.dstPort = port;
        this.user_name = username;
        this.textResponse = textResponse;

        try {
            this.UDPsocket = new DatagramSocket(dstPort);
        } catch (IOException e) {
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }
    }

    private void sendOverUDP (DataMessage message)
    {
        try {
            InetAddress IPAddress =  InetAddress.getByName(this.dstAddress);
            byte[] send_data = new byte[256];
            send_data = message.asAString().getBytes();
            DatagramPacket send_packet = new DatagramPacket(send_data, message.asAString().length(), IPAddress, this.dstPort);
            this.UDPsocket.send(send_packet);
        } catch (UnknownHostException e) {
        e.printStackTrace();
        response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            String initiateMessage = this.user_name + " has connected.";
            String destination = "broadcast";

            DataMessage connectionMessage =
                    new DataMessage(
                            initiateMessage, this.user_name, destination, Constants.mt_CLIENT_CONNECT);

            this.sendOverUDP(connectionMessage);

            byte[] receiveData = new byte[256];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            this.UDPsocket.receive(receivePacket);
            response = new String(receivePacket.getData());

        } catch (UnknownHostException e) {
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        textResponse.setText(response);
        super.onPostExecute(result);
    }

}