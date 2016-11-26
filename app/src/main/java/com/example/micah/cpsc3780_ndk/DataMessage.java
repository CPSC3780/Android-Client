package com.example.micah.cpsc3780_ndk;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.util.Log;
/**
 * Created by Micah on 2016-11-19.
 */

public class DataMessage {
    /**
     * MEMBER VARIABLES
     */
    String m_payload;
    int m_messageType;
    int m_sequenceNumber;
    String m_sourceIdentifier;
    String m_destinationIdentifier;
    int  m_serverSyncPayloadOriginIndex;

    /**
     * CONSTRUCTORS
     */
    DataMessage(
            int sequenceNumber,
            int messageType,
            String SourceID,
            String inDestination,
            String inPayload)
    {
        this.m_payload = inPayload;
        this.m_sequenceNumber = sequenceNumber;
        this.m_sourceIdentifier = SourceID;
        this.m_destinationIdentifier = inDestination;
        this.m_messageType = messageType;
        this.m_serverSyncPayloadOriginIndex = -1;
    }

    DataMessage(String receivedStringMessage)
    {
        String receivedString = receivedStringMessage;

        String[] partsOfMessage = receivedString.split(Pattern.quote("/?"));

        this.m_sequenceNumber = Integer.parseInt(partsOfMessage[0]);

        String typePart = partsOfMessage[1];
        this.m_messageType = this.stringToMessageType(typePart);

        this.m_sourceIdentifier = partsOfMessage[2];
        this.m_destinationIdentifier = partsOfMessage[3];

        this.m_payload = partsOfMessage[4];

        String serverSyncPayloadOriginIndexString = partsOfMessage[5];
        this.m_serverSyncPayloadOriginIndex = Integer.parseInt(serverSyncPayloadOriginIndexString);
    }

    /**
     * PUBLIC METHODS
     */
    public int stringToMessageType (String inMessageTypeAsString) {

        if(inMessageTypeAsString.equals("client connect"))
        {
            return Constants.mt_CLIENT_CONNECT;
        }

        if(inMessageTypeAsString.equals("client disconnect"))
        {
            return Constants.mt_CLIENT_DISCONNECT;
        }

        if(inMessageTypeAsString.equals("client send"))
        {
            return Constants.mt_CLIENT_SEND;
        }

        if (inMessageTypeAsString.equals("client get"))
        {
            return Constants.mt_CLIENT_GET;
        }

        if(inMessageTypeAsString.equals("client ack"))
        {
            return Constants.mt_CLIENT_ACK;
        }

        assert(false);

        return Constants.mt_UNDEFINED;
    }

    public String viewMessageTypeAsString()  {
        String messageTypeAsString = "";

        switch (this.m_messageType)
        {

            case Constants.mt_CLIENT_CONNECT:
            {
                messageTypeAsString = "client connect";
                break;
            }
            case Constants.mt_CLIENT_DISCONNECT:
            {
                messageTypeAsString = "client disconnect";
                break;
            }
            case Constants.mt_CLIENT_SEND:
            {
                messageTypeAsString = "client send";
                break;
            }
            case Constants.mt_CLIENT_GET:
            {
                messageTypeAsString = "client get";
                break;
            }
            case Constants.mt_CLIENT_ACK:
            {
                messageTypeAsString = "client ack";
                break;
            }
            default:
            {
                assert(false);
            }

        }
        return messageTypeAsString;
    }

    public int viewMessageType() {
        return this.m_messageType;
    }

    public String viewPayload () {
        return this.m_payload;
    }

    public String viewSourceIdentifier () {
        return this.m_sourceIdentifier;
    }

    public String viewDestinationIdentifier () {
        return this.m_destinationIdentifier;
    }

    public int viewSequenceNumber () { return this.m_sequenceNumber; }

    public String asAString()
    {
        String messageAsString =
                String.valueOf(this.m_sequenceNumber) + Constants.messageDelimiter()
                 + this.viewMessageTypeAsString() + Constants.messageDelimiter()
                 + this.m_sourceIdentifier + Constants.messageDelimiter()
                 + this.m_destinationIdentifier + Constants.messageDelimiter()
                 + this.m_payload + Constants.messageDelimiter()
                 + String.valueOf(this.m_serverSyncPayloadOriginIndex) + Constants.messageDelimiter();

        return messageAsString;
    }

}
