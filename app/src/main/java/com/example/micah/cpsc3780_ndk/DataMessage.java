package com.example.micah.cpsc3780_ndk;

import java.util.ArrayList;

/**
 * Created by Micah on 2016-11-19.
 */

public class DataMessage {
    /**
     * MEMBER VARIABLES
     */
    String m_payload;
    int m_messageType;
    String m_sourceIdentifier;
    String m_destinationIdentifier;
    boolean m_relayToAdjacentServers;

    /**
     * CONSTRUCTORS
     */
    DataMessage(
            String inPayload,
            String SourceID,
            String inDestination,
            int messageType
            )
    {
        this.m_payload = inPayload;
        this.m_sourceIdentifier = SourceID;
        this.m_destinationIdentifier = inDestination;
        this.m_messageType = messageType;
        this.m_relayToAdjacentServers = true;
    }

    DataMessage(
            ArrayList<Character> inCharVector
    )
    {
        StringBuilder builder = new StringBuilder(inCharVector.size());
        for(Character ch: inCharVector)
        {
            builder.append(ch);
        }
        String asString = builder.toString();

        String[] partsOfMessage = asString.split(Constants.messageDelimiter());
        this.m_payload = partsOfMessage[0];
        this.m_sourceIdentifier = partsOfMessage[1];
        this.m_destinationIdentifier = partsOfMessage[2];

        String typePart = partsOfMessage[3];
        this.m_messageType = this.stringToMessageType(typePart);

        String relayStatus = partsOfMessage[4];
        this.m_relayToAdjacentServers = Boolean.valueOf(relayStatus);
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

        if(inMessageTypeAsString.equals("private chat"))
        {
            return Constants.mt_CLIENT_PRIVATE_CHAT;
        }

        if(inMessageTypeAsString.equals("target not found"))
        {
            return Constants.mt_CLIENT_TARGET_NOT_FOUND;
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
            case Constants.mt_CLIENT_PRIVATE_CHAT:
            {
                messageTypeAsString = "private chat";
                break;
            }
            case Constants.mt_CLIENT_TARGET_NOT_FOUND:
            {
                messageTypeAsString = "target not found";
                break;
            }
            case Constants.mt_RELAY_CHAT:
            {
                messageTypeAsString = "relay chat";
                break;
            }
            default:
            {
                assert(false);
            }

        }
        return messageTypeAsString;
    }

    public String getRelayServerStatusAsIntString () {
        // TODO: this is kind of dumb, need to do some more research on this
        return this.m_relayToAdjacentServers ? "1" : "0";
    }

    public String asAString()
    {
        String messageAsString =
                this.m_payload + Constants.messageDelimiter()
                 + this.m_sourceIdentifier + Constants.messageDelimiter()
                 + this.m_destinationIdentifier + Constants.messageDelimiter()
                 + this.viewMessageTypeAsString() + Constants.messageDelimiter()
                 + this.getRelayServerStatusAsIntString() + Constants.messageDelimiter();

        return messageAsString;
    }

}
