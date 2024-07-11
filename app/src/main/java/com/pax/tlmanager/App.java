package com.pax.tlmanager;

import android.app.Application;
import android.content.Intent;
import android.os.RemoteException;
import android.widget.Toast;

import com.pax.devicemanager.PaxDeviceUtils;
import com.pax.market.android.app.sdk.BaseApiService;
import com.pax.market.android.app.sdk.StoreSdk;
import com.pax.poslink.peripheries.MiscSettings;
import com.pax.tlmanager.cusui.UIActivityLifecycleCallbacks;

public class App extends Application {
    //PAXSTORE Key and app secret, see https://github.com/PAXSTORE/paxstore-3rd-app-android-sdk
    private static final String PAXSTORE_APP_KEY = "WBXD5EP2O90VA8J35N41";
    private static final String PAXSTORE_APP_SECRET = "QTU5879M0WDSW1ZZBR1EVL31E9MXYG7ICTX0IVGQ";

    //UI ActivityLifecycle for manage the CustomU Activity.
    private UIActivityLifecycleCallbacks lifecycleCallbacks;
    private static App mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        ParameterManager.getInstance(this).init();
        lifecycleCallbacks = new UIActivityLifecycleCallbacks();
        registerActivityLifecycleCallbacks(lifecycleCallbacks);
        initPaxStoreSdk();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            exitApp();
        });
    }

    public boolean isIdelActivity() {
        return lifecycleCallbacks.isIdleActivity();
    }

    public static App getApp() {
        return mApplication;
    }

    private void initPaxStoreSdk() {
        StoreSdk.getInstance().init(getApplicationContext(), PAXSTORE_APP_KEY, PAXSTORE_APP_SECRET,
        new BaseApiService.Callback() {
            @Override
            public void initSuccess() {
                StoreSdk.getInstance().initInquirer(() -> isIdelActivity());
            }

            @Override
            public void initFailed(RemoteException e) {
//                Toast.makeText(getApplicationContext(), "Cannot get API URL from PAXSTORE, Please install PAXSTORE first.", Toast.LENGTH_LONG).show();
                StoreSdk.getInstance().initInquirer(() -> isIdelActivity());
            }
        });
    }

    public void exitApp() {
        MiscSettings.setStatusBarEnable(this,true);
        MiscSettings.setNavigationBarVisible(this,true);
        if (PaxDeviceUtils.isLowResolutionLandDevice()) {
            MiscSettings.setStatusBarVisible(this, true);
        }
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(0);
    }
}
