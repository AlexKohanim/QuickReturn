package org.appcelerator.titanium;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Process;
import android.support.p000v4.view.ViewCompat;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.LinkedList;
import org.appcelerator.kroll.KrollApplication;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollExceptionHandler;
import org.appcelerator.kroll.KrollExceptionHandler.ExceptionMessage;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.CurrentActivityListener;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;

public class TiExceptionHandler implements Callback, KrollExceptionHandler {
    private static final int MSG_OPEN_ERROR_DIALOG = 10011;
    private static final String TAG = "TiExceptionHandler";
    /* access modifiers changed from: private */
    public static boolean dialogShowing = false;
    /* access modifiers changed from: private */
    public static LinkedList<ExceptionMessage> errorMessages = new LinkedList<>();
    private static Handler mainHandler;

    public void printError(String title, String message, String sourceName, int line, String lineSource, int lineOffset) {
        Log.m32e(TAG, "----- Titanium Javascript " + title + " -----");
        Log.m32e(TAG, "- In " + sourceName + ":" + line + "," + lineOffset);
        Log.m32e(TAG, "- Message: " + message);
        Log.m32e(TAG, "- Source: " + lineSource);
    }

    public TiExceptionHandler() {
        mainHandler = new Handler(TiMessenger.getMainMessenger().getLooper(), this);
    }

    public void openErrorDialog(ExceptionMessage error) {
        if (TiApplication.isUIThread()) {
            handleOpenErrorDialog(error);
        } else {
            TiMessenger.sendBlockingMainMessage(mainHandler.obtainMessage(MSG_OPEN_ERROR_DIALOG), error);
        }
    }

    /* access modifiers changed from: protected */
    public void handleOpenErrorDialog(ExceptionMessage error) {
        KrollApplication application = KrollRuntime.getInstance().getKrollApplication();
        if (application != null) {
            Activity activity = application.getCurrentActivity();
            if (activity == null || activity.isFinishing()) {
                Log.m44w(TAG, "Activity is null or already finishing, skipping dialog.");
                return;
            }
            KrollDict dict = new KrollDict();
            dict.put(TiC.PROPERTY_TITLE, error.title);
            dict.put("message", error.message);
            dict.put("sourceName", error.sourceName);
            dict.put("line", Integer.valueOf(error.line));
            dict.put("lineSource", error.lineSource);
            dict.put("lineOffset", Integer.valueOf(error.lineOffset));
            TiApplication.getInstance().fireAppEvent("uncaughtException", dict);
            printError(error.title, error.message, error.sourceName, error.line, error.lineSource, error.lineOffset);
            if (TiApplication.getInstance().getDeployType().equals(TiApplication.DEPLOY_TYPE_PRODUCTION)) {
                return;
            }
            if (!dialogShowing) {
                dialogShowing = true;
                final ExceptionMessage fError = error;
                application.waitForCurrentActivity(new CurrentActivityListener() {
                    public void onCurrentActivityReady(Activity activity) {
                        TiExceptionHandler.createDialog(fError);
                    }
                });
                return;
            }
            errorMessages.add(error);
        }
    }

    protected static void createDialog(ExceptionMessage error) {
        KrollApplication application = KrollRuntime.getInstance().getKrollApplication();
        if (application != null) {
            Context context = application.getCurrentActivity();
            FrameLayout layout = new FrameLayout(context);
            layout.setBackgroundColor(Color.rgb(128, 0, 0));
            LinearLayout vlayout = new LinearLayout(context);
            vlayout.setOrientation(1);
            vlayout.setPadding(10, 10, 10, 10);
            layout.addView(vlayout);
            TextView sourceInfoView = new TextView(context);
            sourceInfoView.setBackgroundColor(-1);
            sourceInfoView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
            sourceInfoView.setPadding(4, 5, 4, 0);
            sourceInfoView.setText("[" + error.line + "," + error.lineOffset + "] " + error.sourceName);
            TextView messageView = new TextView(context);
            messageView.setBackgroundColor(-1);
            messageView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
            messageView.setPadding(4, 5, 4, 0);
            messageView.setText(error.message);
            TextView sourceView = new TextView(context);
            sourceView.setBackgroundColor(-1);
            sourceView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
            sourceView.setPadding(4, 5, 4, 0);
            sourceView.setText(error.lineSource);
            TextView infoLabel = new TextView(context);
            infoLabel.setText("Location: ");
            infoLabel.setTextColor(-1);
            infoLabel.setTextScaleX(1.5f);
            TextView messageLabel = new TextView(context);
            messageLabel.setText("Message: ");
            messageLabel.setTextColor(-1);
            messageLabel.setTextScaleX(1.5f);
            TextView sourceLabel = new TextView(context);
            sourceLabel.setText("Source: ");
            sourceLabel.setTextColor(-1);
            sourceLabel.setTextScaleX(1.5f);
            vlayout.addView(infoLabel);
            vlayout.addView(sourceInfoView);
            vlayout.addView(messageLabel);
            vlayout.addView(messageView);
            vlayout.addView(sourceLabel);
            vlayout.addView(sourceView);
            OnClickListener clickListener = new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == -1) {
                        Process.killProcess(Process.myPid());
                    } else if (which == -3 || which == -2) {
                    }
                    if (!TiExceptionHandler.errorMessages.isEmpty()) {
                        TiExceptionHandler.createDialog((ExceptionMessage) TiExceptionHandler.errorMessages.removeFirst());
                    } else {
                        TiExceptionHandler.dialogShowing = false;
                    }
                }
            };
            new Builder(context).setTitle(error.title).setView(layout).setPositiveButton("Kill", clickListener).setNeutralButton("Continue", clickListener).setCancelable(false).create().show();
        }
    }

    protected static void reload(String sourceName) {
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_OPEN_ERROR_DIALOG /*10011*/:
                AsyncResult asyncResult = (AsyncResult) msg.obj;
                handleOpenErrorDialog((ExceptionMessage) asyncResult.getArg());
                asyncResult.setResult(null);
                return true;
            default:
                return false;
        }
    }

    public void handleException(ExceptionMessage error) {
        openErrorDialog(error);
    }
}
