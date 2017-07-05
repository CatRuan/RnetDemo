package com.rd.rnet.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;


/**
 * Created by ruand on 2017/6/7.
 * loading dialog from https://github.com/zyao89/ZLoading
 */

public class LoadingPage {

    private static LoadingAlertDialog mDialog;

    public static void hideLoadingDialog() {
        if (null != mDialog) {
            mDialog.dismiss();
        }
        mDialog = null;
    }


    public static void showLoadingDialog(final Context context) {
        mDialog = new LoadingAlertDialog(context);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                        hideLoadingDialog();
                    }
                }
                return false;
            }
        });
        mDialog.show("加载中…");
    }

    public static AlertDialog showLoadingView(final Context context) {
        LoadingAlertDialog dialog = new LoadingAlertDialog(context);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                        hideLoadingDialog();
                    }
                }
                return false;
            }
        });
        dialog.show("加载中…");
        return dialog;
    }

    public static void hideLoadingView(AlertDialog dialog) {
        if (null != dialog) {
            dialog.dismiss();
        }
        dialog = null;
    }

}
