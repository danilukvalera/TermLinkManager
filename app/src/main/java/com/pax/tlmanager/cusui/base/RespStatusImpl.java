package com.pax.tlmanager.cusui.base;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.pax.customui.constant.status.CardStatus;
import com.pax.customui.core.api.IRespStatus;
import com.pax.tlmanager.cusui.activity.DialogActivity;

import java.lang.ref.WeakReference;

public class RespStatusImpl implements IRespStatus {
    private static final String TAG = "RespStatusImpl";

    private final WeakReference<Activity> activityWeakReference;

    public RespStatusImpl(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void onAccepted() {
        DialogActivity.start(activityWeakReference.get(), new Intent(CardStatus.CARD_PROCESS_COMPLETED));
        finshActivity();
    }

    @Override
    public void onDeclined(long code, @Nullable String message) {
        Log.d(TAG, "onDeclined: " + code + "-" + message);
        Activity activity = activityWeakReference.get();
        if (activity != null) {
            activity.runOnUiThread(() -> {
                String buff;
                if (TextUtils.isEmpty(message))
                    buff = "Trans Failed! Error Code : " + code;
                else
                    buff = message + "\n Error Code : " + code;
                Toast.makeText(activity, buff, Toast.LENGTH_LONG).show();
            });
        }
    }

    @Override
    public void onMessage(@Nullable String message) {
        Log.d(TAG, "onMessage: " + message);
        Activity activity = activityWeakReference.get();
        if (activity != null) {
            activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onReset() {
        Log.d(TAG, "onReset");
        //When execute RESET command will call this method.
        finshActivity();
    }

    @Override
    public void onClearMsg() {
        Log.d(TAG, "onClearMsg");
        //When need clear the screen message.
    }

    protected void finshActivity() {
        Activity activity = activityWeakReference.get();
        if (activity != null) {
            activity.finish();
        }
    }
}
