package com.example.micah.cpsc3780_ndk;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Micah on 2016-11-19.
 */

public class Constants {

    public static final int mt_UNDEFINED = 0;
    public static final int mt_CLIENT_CONNECT = 1;
    public static final int mt_CLIENT_DISCONNECT = 2;
    public static final int mt_CLIENT_SEND = 3;
    public static final int mt_CLIENT_GET = 4;
    public static final int mt_CLIENT_ACK = 5;

    private static List<Integer> serverListeningPorts = Arrays.asList(
            8080, 8081, 8082, 8083, 8084);

    private static List <String> serverNames = Arrays.asList(
            "Alpha", "Bravo", "Charlie", "Delta", "Echo");

    public static String serverIndexToServerName(int inServerIndex)
    {
        return serverNames.get(inServerIndex);
    }

    public static int serverIndexToListeningPort(int inServerIndex)
    {
        return serverListeningPorts.get(inServerIndex);

    }

    public static String messageDelimiter()
    {
        return "/?";
    }

    public static int charToServerIndex(char inChar)
    {
        char inCharAsLower = Character.toLowerCase(inChar);

        return inCharAsLower - 'a';
    }
}
