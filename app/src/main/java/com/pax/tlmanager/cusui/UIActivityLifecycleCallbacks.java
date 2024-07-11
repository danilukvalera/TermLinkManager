package com.pax.tlmanager.cusui;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.pax.tlmanager.MainActivity;
import com.pax.tlmanager.cusui.event.EndEvent;
import com.pax.tlmanager.cusui.event.EventBusUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

public class UIActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "EventBus";
    // save current activity
    private WeakReference<Activity> mCurrActivity = null;
    // save last activity
    private WeakReference<Activity> mLastActivity = null;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

        if (activity != null) {
            mCurrActivity = new WeakReference(activity);
            EventBusUtil.doEvent(new EndEvent());
            if (!mCurrActivity.get().getLocalClassName().contains("DialogActivity")) {
                Log.i(TAG, "register " + mCurrActivity.get().getLocalClassName());
                EventBusUtil.register(this);
                mLastActivity = new WeakReference(activity);
            }
        }

    }

    @Override
    public void onActivityStarted(Activity activity) {
        //When the ActivityStarted
    }

    @Override
    public void onActivityResumed(Activity activity) {
        //When the ActivityResumed
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //When the ActivityPaused
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //When ActivityStopped
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        //When ActivitySaveInstanceState
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //When ActivityDestroyed
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEndEvent(EndEvent event) {
        if (mLastActivity.get() != null && !mCurrActivity.get().getLocalClassName().contains("DialogActivity")) {
            Log.i(TAG, "end activity " + mLastActivity.get().getLocalClassName());
            if (!isIdleActivity()) {
                mLastActivity.get().finish();
                EventBusUtil.unregister(this);
            }
        }
    }

    public boolean isIdleActivity() {
        if (mLastActivity != null && mLastActivity.get() != null) {
            return mLastActivity.get().getLocalClassName().contains(MainActivity.class.getSimpleName());
        }
        return false;
    }

}
