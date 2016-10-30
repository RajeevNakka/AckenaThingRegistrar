package com.ackena.thingregistrar;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ackena.thingregistrar.other.TaskResult;
import com.ackena.thingregistrar.other.Util;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

public class ATR extends AppCompatActivity {

    public static final int REQUEST_CODE = 1;
    public static final int RESULT_CODE_CONNECTION_RECREATED = 1;
    public static final int RESULT_CODE_CONNECTION_NOT_CREATED = 2;

    public static final String USER_ID = "USER-ID";
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    Button scanButton;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    private boolean loggedIn = false;

    private XMPPTCPConnection connection;
    private Chat chat;

    ProgressDialog dialog;
    private String userJID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atr);

        setupScanner();
        setupXMPPChat();
        setResult(RESULT_CODE_CONNECTION_NOT_CREATED);
        userJID = getIntent().getStringExtra(USER_ID);
    }

    private void setupScanner() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        scanButton = (Button) findViewById(R.id.ScanButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (barcodeScanned) {
                    startScanning();
                }
            }
        });
    }

    private void startScanning() {
        barcodeScanned = false;
        scanButton.setText("Scanning...");
        mCamera.setPreviewCallback(previewCb);
        mCamera.startPreview();
        previewing = true;
        mCamera.autoFocus(autoFocusCB);
    }

    private void setupXMPPChat() {
        connection = Session.connection;

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    createChat();
                    Log.i("SmackDemo", "after connect....");
                } catch (Exception e) {
                    e.printStackTrace();
                    String message = e.getMessage();
                    Log.e("SmackDemo", message==null?e.toString():message);
                }
                return null;
            }
        };

        AsyncTaskCompat.executeParallel(task);
    }

    private void createChat() {
        connection = Session.connection;
        chat = ChatManager.getInstanceFor(connection)
                .createChat(userJID, new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, final Message message) {
                        System.out.println("Received message: " + message.getStanzaId() + "--" + message.getBody());

                        /*if (message.getStanzaId() != null) {*/
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ATR.this, "Server : " + message.getBody(), Toast.LENGTH_LONG).show();
                                }
                            });
                        /*}*/
                    }
                });
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                stopScanning();
                scanButton.setText("Scan");
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    //scanText.setText("barcode result " + sym.getData());
                    Log.i("Scanner", sym.getData());
                    barcodeScanned = true;

                    checkConnectionAndSendMessage(sym.getData());
                }

                //added by Rajeev
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
            }
        }
    };

    private void stopScanning() {
        previewing = false;
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
    }

    AckenaProgressDialog progressDialog = new AckenaProgressDialog(this);

    private void checkConnectionAndSendMessage(final String data) {

        if (Session.loggedIn == false) {
            if (Util.isNetworkAvailable(this)) {
                Toast.makeText(ATR.this, "Connection lost, reconnecting....", Toast.LENGTH_SHORT).show();
                progressDialog.show();
                UserLoginTask loginTask = new UserLoginTask(this, Session.mUsername, Session.mPassword,Session.mServer) {
                    @Override
                    protected void onPostExecute(TaskResult<Void> result) {
                        progressDialog.close();
                        setResult(RESULT_CODE_CONNECTION_RECREATED);
                        if (result.IsSuccess) {
                            Session.loggedIn = true;
                            createChat();
                            progressDialog.close();
                            send(data);
                        } else {
                            Toast.makeText(ATR.this, "Error sending message, try again", Toast.LENGTH_LONG).show();
                        }
                    }
                };
                AsyncTaskCompat.executeParallel(loginTask);
            } else {
                Toast.makeText(ATR.this, "Network not available", Toast.LENGTH_LONG).show();
            }
        } else {
            send(data);
        }
    }

    private void send(String data) {
        try {
            dialog = new ProgressDialog(ATR.this);
            dialog.setTitle("Sending Message Please Wait....");
            dialog.setCancelable(false);
            dialog.show();
            chat.sendMessage(data);
            Toast.makeText(ATR.this, "Message sent successfully...", Toast.LENGTH_LONG).show();
        } catch (SmackException.NotConnectedException e) {
            Log.e("SmackDemo", e.getMessage());
            Toast.makeText(ATR.this, "Error sending message, check your internet connection...", Toast.LENGTH_LONG).show();
            Session.loggedIn = false;
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("SmackDemo", e.getMessage());
            Toast.makeText(ATR.this, "Error sending message, try again..", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        dialog.dismiss();
    }

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
}
