package com.ackena.thingregistrar;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.webkit.WebView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.stringencoder.StringEncoder;

import java.io.IOException;

/**
 * Created by Light on 11-10-2016.
 */

public class Session {
    public static String mUsername;
    public static String mPassword;
    public static String mServer;

    public static XMPPTCPConnection connection;
    public static boolean loggedIn = false;


    public static void loadPreferences(Context context)
    {
        String fileKey = context.getString(R.string.preference_file_key);
        String usernameKey = context.getString(R.string.preference_username_key);
        String passwordKey = context.getString(R.string.preference_password_key);
        String serverKey = context.getString(R.string.preference_server_key);

        SharedPreferences preferences = context.getSharedPreferences(fileKey, Context.MODE_PRIVATE);
        mUsername = preferences.getString(usernameKey,null);
        mPassword = preferences.getString(passwordKey,null);
        mServer = preferences.getString(serverKey,null);
    }

    public static void saveToPreferences(Context context, String mUserName, String mPassword, String mServer) {

        Session.mUsername = mUserName;
        Session.mPassword = mPassword;
        Session.mServer = mServer;

        String fileKey = context.getString(R.string.preference_file_key);
        String usernameKey = context.getString(R.string.preference_username_key);
        String passwordKey = context.getString(R.string.preference_password_key);
        String serverKey = context.getString(R.string.preference_server_key);

        SharedPreferences.Editor editor = context.getSharedPreferences(fileKey, Context.MODE_PRIVATE).edit();
        editor.putString(usernameKey,mUserName);
        editor.putString(passwordKey,mPassword);
        editor.putString(serverKey,mServer);

        editor.commit();
    }

    public static void clearPreferences(Context context) {
        String fileKey = context.getString(R.string.preference_file_key);
        SharedPreferences.Editor editor = context.getSharedPreferences(fileKey, Context.MODE_PRIVATE).edit();

        editor.clear();
        editor.commit();
    }

}
