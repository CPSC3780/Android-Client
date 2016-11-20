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

// TODO: this code from http://androidsrc.net/android-client-server-using-sockets-client-implementation/
// TODO: will act as a template for the client. Will adjust accordingly

public class Client extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String user_name;
    String response = "";
    TextView textResponse;

    Client(String addr, int port, String username,TextView textResponse) {
        dstAddress = addr;
        dstPort = port;
        user_name = username;
        this.textResponse = textResponse;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        DatagramSocket socket = null;
        String initiateMessage = this.user_name + " has connected.";
        String destination = "broadcast";

        DataMessage connectionMessage =
                new DataMessage(
                initiateMessage, this.user_name, destination, Constants.mt_CLIENT_CONNECT);

        try {
            socket = new DatagramSocket(dstPort);
            InetAddress IPAddress =  InetAddress.getByName(dstAddress);

            byte[] send_data = new byte[256];
            byte[] receiveData = new byte[256];

            while (true) {
                send_data = connectionMessage.asAString().getBytes();
                DatagramPacket send_packet = new DatagramPacket(send_data, connectionMessage.asAString().length(), IPAddress, dstPort);
                socket.send(send_packet);

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                response = new String(receivePacket.getData());
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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