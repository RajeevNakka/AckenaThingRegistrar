package com.ackena.thingregistrar;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;

public class AckenaProgressDialog {

    private final Context Context;
    private ProgressDialog progressDialog;

    public AckenaProgressDialog(Context context) {
        this.Context = context;
    }

    @NonNull
    public void show() {
        progressDialog = new ProgressDialog(Context, R.style.AlertDialogWindowTheme);//.show(this,null,null);
        progressDialog.setCancelable(false);
        //pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog_layout);
    }

    public void close() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}