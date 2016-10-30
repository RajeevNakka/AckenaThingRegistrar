package com.ackena.thingregistrar;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ackena.thingregistrar.other.TaskResult;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;

/**
 * Represents an asynchronous registration task used to authenticate
 * the user.
 */
public class UserSignUpTask extends AsyncTask<Void, Void, TaskResult<Void>> {

    private Context context;
    private final String mUserName;
    private final String mServer;
    private final String mPassword;

    UserSignUpTask(Context context, String jid, String password) {
        this.context = context;
        mPassword = password;
        int index = jid.indexOf('@');

        mUserName = jid.substring(0, index);
        Log.i("ATR", "UserDao Name:" + mUserName);
        mServer = jid.substring(index + 1);
        Log.i("ATR", "Server:" + mServer);
    }

    UserSignUpTask(Context context, String username, String password, String server) {
        this.context = context;
        mUserName = username;
        mPassword = password;
        mServer = server;
    }

    @Override
    protected TaskResult<Void> doInBackground(Void... params) {

        TaskResult<Void> result = new TaskResult<>();

        try {
            // Creating a connection
            XMPPTCPConnectionConfiguration connConfig =
                    XMPPTCPConnectionConfiguration.builder()
                            .setServiceName(mServer)  // Name of your Host
                            .build();
            Session.connection = new XMPPTCPConnection(connConfig);
            Session.connection.connect();
            Log.i("ATR", "Connected to " + Session.connection.getHost());

            // Registering the user
            AccountManager accountManager = AccountManager.getInstance(Session.connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);

            Log.i("ATR", "Creating account....");
            accountManager.createAccount(mUserName, mPassword);   // Skipping optional fields like jid, first name, last name, etc..
            Log.i("ATR", "Account created.");

            Log.i("ATR", "Logging in");
            Session.connection.login(mUserName, mPassword);
            Log.i("ATR", "Logged in successfully");

            Session.loggedIn = true;

            Session.saveToPreferences(context, mUserName, mPassword, mServer);
            result.IsSuccess = true;
        } catch (XMPPException e) {
            Log.e("SmackDemo", e.getMessage());
            e.printStackTrace();
            result.exception = e;
        } catch (SmackException e) {
            e.printStackTrace();
            Log.e("SmackDemo", e.getMessage());
            result.exception = e;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SmackDemo", e.getMessage());
            result.exception = e;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SmackDemo", e.getMessage());
            result.exception = e;
        }

        return result;
    }
}
