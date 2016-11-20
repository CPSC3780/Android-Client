package com.example.micah.cpsc3780_ndk;

/**
 * Created by Micah on 2016-11-19.
 */

public class Constants {

    public static final int mt_UNDEFINED = 0;
    public static final int mt_CLIENT_CONNECT = 1;
    public static final int mt_CLIENT_DISCONNECT = 2;
    public static final int mt_CLIENT_PRIVATE_CHAT = 3;
    public static final int mt_CLIENT_TARGET_NOT_FOUND = 4;
    public static final int mt_RELAY_CHAT = 5;

    public static String messageDelimiter()
    {
        return "/?";
    }
}
