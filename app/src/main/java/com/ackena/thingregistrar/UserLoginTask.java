package com.ackena.thingregistrar;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ackena.thingregistrar.dao.UserDao;
import com.ackena.thingregistrar.other.TaskResult;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

/**
 * Represents an asynchronous login task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, TaskResult<Void>> {

    private Context context;
    private final String mUserName;
    private final String mServer;
    private final String mPassword;


    UserLoginTask(Context context, String jid, String password) {
        this.context = context;
        mPassword = password;
        int index = jid.indexOf('@');

        mUserName = jid.substring(0, index);
        Log.i("ATR","UserDao Name:"+mUserName);
        mServer = jid.substring(index+1);
        Log.i("ATR","Server:"+mServer);
    }

    UserLoginTask(Context context, String username, String password, String server) {
        this.context = context;
        mUserName = username;
        mPassword = password;
        mServer = server;
    }

    @Override
    protected TaskResult<Void> doInBackground(Void... params) {

            TaskResult<Void> result = new TaskResult<>();

        try {
            UserDao.connect(context,mUserName,mPassword,mServer);
            result.IsSuccess = true;
        } catch (XMPPException e) {
            Log.e("SmackDemo-XMP",e.getMessage());
            e.printStackTrace();
            result.exception = e;
        } catch (SmackException e) {
            e.printStackTrace();
            Log.e("SmackDemo-Sm",e.getMessage());
            result.exception = e;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SmackDemo-IO",e.getMessage());
            result.exception = e;
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("SmackDemo-Ex",e.getMessage());
            result.exception = e;
        }
        return result;
    }

}
