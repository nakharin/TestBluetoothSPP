package com.emcsthai.bluetooth.sppr210.testbluetoothspp.MyUtility;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialogHandler {

    private static LoadingDialogHandler instance = null;

    public ProgressDialog dialog = null;
    public ProgressDialog progressDialog = null;

    private int percent = 0;

    private LoadingDialogHandler() {

    }

    public static LoadingDialogHandler getInstance() {
        if (instance == null) {
            instance = new LoadingDialogHandler();
        }

        return instance;
    }

    public void showLoadingDialog(Context context, String title, String message) {

        dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void closeLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void showProgressDialog(Context context, String title, String message) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        if (message != null) {
            progressDialog.setMessage(message);
        } else {
            progressDialog.setMessage("Connecting Please Wait...");
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    public void showProgressDialog(Context context, String title, String message, int start, int max) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        if (message != null) {
            progressDialog.setMessage(message);
        } else {
            progressDialog.setMessage("Connecting Please Wait...");
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setProgress(start);
        progressDialog.setMax(max);
        progressDialog.show();
    }

    public void setProgressDialog(int pro) {
        if (progressDialog != null) {
            if (pro != percent) {
                percent = pro;
            }
            progressDialog.setProgress(pro);
        }
    }

    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
