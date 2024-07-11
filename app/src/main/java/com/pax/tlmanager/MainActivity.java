package com.pax.tlmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pax.customui.constant.status.ManagerStatus;
import com.pax.devicemanager.PaxDeviceUtils;
import com.pax.poslink.peripheries.MiscSettings;
import com.pax.termlinkmanager.ConnectManager;
import com.pax.termlinkmanager.ServiceConnectionCallBack;
import com.pax.termlinkmanager.TLCommSetting;
import com.pax.tlmanager.dialog.InputPwdDialog;
import com.pax.tlmanager.setting.PasswordUtils;
import com.pax.tlmanager.setting.SettingActivity;
import com.pax.tlmanager.utils.CheckUtil;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView tvInformation;
    private Banner<String, BannerImageAdapter<String>> banner;
    private int settingClickTime = 0;
    private boolean isServiceConnected = false;
    private AlertDialog mDailog;
    private BannerImageAdapter<String> mAdapter;
    private ServiceConnectionCallBack connectionCallBack = new ServiceConnectionCallBack() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "onSuccess");
            isServiceConnected = true;
            SystemClock.sleep(50);
            MiscSettings.setStatusBarEnable(MainActivity.this, false);
            MiscSettings.setNavigationBarVisible(MainActivity.this, false);
            if (PaxDeviceUtils.isLowResolutionLandDevice()) {
                MiscSettings.setStatusBarVisible(MainActivity.this, false);
            }
            updateBanner();
            dismissDialog();
        }

        @Override
        public void onDisconnected() {
            isServiceConnected = false;
            sendBroadcast(new Intent(ManagerStatus.RESET));
            Log.d(TAG, "onDisconnected");
            showMessageDialog("service not running", "TermLink service is not running.");
        }

        @Override
        public void onFailed(String message) {
//            isServiceConnected = false;
//            Log.d(TAG, "onFailed: " + message);
//            showMessageDialog("service not running", message);
            Log.d(TAG, "onSuccess");
            isServiceConnected = true;
            SystemClock.sleep(50);
            MiscSettings.setStatusBarEnable(MainActivity.this, false);
            MiscSettings.setNavigationBarVisible(MainActivity.this, false);
            if (PaxDeviceUtils.isLowResolutionLandDevice()) {
                MiscSettings.setStatusBarVisible(MainActivity.this, false);
            }
            updateBanner();
            dismissDialog();
        }
    };
    Handler checkHandler = new Handler(Looper.getMainLooper(), this::checkStatus);
    //When has parameter download from PAXSTORE, the termLink service should be restart. And the banner should be update.
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadParamService.INTENT_UPDATE_RESOURCE.equals(intent.getAction())) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Params Updated")
                        .setMessage("The params has been updated from PAXSTORE, please click OK to restart the TermLink service.")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss();
                            startTermLinkService();
                            updateMessageShown();
                            updateBanner();
                        })
                        .setNegativeButton("Exit", (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        }).show();
            }
        }
    };

    private void updateBanner() {
        banner.setLoopTime(ParameterManager.getInstance(this).getBannerInterval());
        banner.setDatas(ParameterManager.getInstance(MainActivity.this).getIdleImgPaths());
        banner.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(receiver, new IntentFilter(DownloadParamService.INTENT_UPDATE_RESOURCE));
        tvInformation = findViewById(R.id.tv_Information);
        new Thread(() -> {//every 500ms check the IP address.
            int count = 0;
            while (true) {
                Message message = Message.obtain();
                message.what = 2021;
                checkHandler.sendMessage(message);
                SystemClock.sleep(500);
                if (count >= 2) {
                    Message m2 = Message.obtain();
                    m2.what = 2022;
                    checkHandler.sendMessage(m2);
                    count = 0;
                }
                count++;
            }
        }).start();

        banner = findViewById(R.id.banner);
        mAdapter = new BannerImageAdapter<String>(ParameterManager.getInstance(this).getIdleImgPaths()) {
            @Override
            public void onBindView(BannerImageHolder holder, String path, int position, int size) {
                Log.d(TAG, "displayImage: " + path);
                Glide.with(MainActivity.this).load(path).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.imageView);
            }
        };
        banner.setAdapter(mAdapter).addBannerLifecycleObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        //Need to ensure that the displayed content or TermLink is working with the latest settings.
        MiscSettings.setStatusBarEnable(this, false);
        MiscSettings.setNavigationBarVisible(this, false);
        updateMessageShown();
        //The default banner picture can from PAXSTORE(3 pictures support now).
        updateBanner();
        startTermLinkService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    private void updateMessageShown() {
        ParameterManager parameterManager = ParameterManager.getInstance(this);
        if (parameterManager.showPrompt()) {
            tvInformation.setVisibility(View.VISIBLE);
            if (parameterManager.isEth()) {
                if (parameterManager.isClientMode()) {
                    tvInformation.setText(ParameterManager.getInstance(this).getCommType() + "  " + parameterManager.getHost());
                } else {
                    tvInformation.setText(ParameterManager.getInstance(this).getCommType() + "  " + CheckUtil.getIpAddress(this));
                }

            } else if (parameterManager.isUART()) {
                tvInformation.setText("UART");
            } else if (parameterManager.isUSB()) {
                tvInformation.setText("USB");
            } else if (parameterManager.isBlueTooth()) {
                tvInformation.setText("Bluetooth");
            } else {
                tvInformation.setText("" + ParameterManager.getInstance(this).getCommType());
            }
        } else {
            tvInformation.setVisibility(View.GONE);
        }
    }

    public TLCommSetting getCommSetting() {
        TLCommSetting commSetting = new TLCommSetting();
        commSetting.setCommType(ParameterManager.getInstance(this).getCommType());
        commSetting.setBaudRate(ParameterManager.getInstance(this).getBaudRate());
        commSetting.setPort(ParameterManager.getInstance(this).getEthernetPort());
        commSetting.setHost(ParameterManager.getInstance(this).getHost());
        commSetting.setCustomUISupport(ParameterManager.getInstance(this).customerUiSupport());
        return commSetting;
    }

    public void startTermLinkService() {
        TLCommSetting commSetting = getCommSetting();
        if (!commSetting.equals(ConnectManager.getInstance(this).getCurrentSetting())) {
            stopTermLinkService();
        }
        ConnectManager.getInstance(this).startListeningService(commSetting, connectionCallBack);
    }

    public void stopTermLinkService() {
        ConnectManager.getInstance(this).stopListeningService();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Cancel button can not finish it.
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTermLinkService();
        unregisterReceiver(receiver);
        //When exiting the application, make sure that the navigation bar can be restored normally.
        MiscSettings.setStatusBarEnable(this, true);
        MiscSettings.setNavigationBarVisible(this, true);
        if (PaxDeviceUtils.isLowResolutionLandDevice()) {
            MiscSettings.setStatusBarVisible(this, true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        banner.stop();
    }

    @Override
    public void finish() {
        App.getApp().exitApp();
    }

    private boolean checkStatus(Message msg) {
        if (msg.what == 2021 && ParameterManager.getInstance(this).isEth()
                && !ParameterManager.getInstance(this).isClientMode()) {
            String ip = CheckUtil.getIpAddress(this);
            tvInformation.setText(String.format("%s  %s",
                    ParameterManager.getInstance(this).getCommType(),
                    ip == null ? "Please Check NetWork" : ip));
        }
        if (msg.what == 2022 && !isServiceConnected) {
            startTermLinkService();
        }
        return false;
    }

    private void showMessageDialog(String title, String message) {
        runOnUiThread(() -> {
            if (mDailog == null) {
                mDailog = new AlertDialog.Builder(MainActivity.this).setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Try again", (dialog, which) -> {
                            dialog.dismiss();
                            startTermLinkService();
                        })
                        .setNegativeButton("Exit", (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        }).show();
            } else if (!mDailog.isShowing()) {
                mDailog.setTitle(title);
                mDailog.setMessage(message);
                mDailog.show();
            }
        });
    }

    public void dismissDialog() {
        if (mDailog != null && mDailog.isShowing()) {
            runOnUiThread(() -> mDailog.dismiss());
        }
    }


    public void clickCorner1(View view) {
        if (settingClickTime == 0) {
            settingClickTime++;
        } else {
            settingClickTime = 0;
        }
    }

    public void clickCorner2(View view) {
        if (settingClickTime == 1) {
            settingClickTime++;
        } else {
            settingClickTime = 0;
        }
    }

    public void clickCorner3(View view) {
        if (settingClickTime == 2) {
            settingClickTime++;
        } else {
            settingClickTime = 0;
        }
    }

    public void clickCorner4(View view) {
        if (settingClickTime == 3) {
            InputPwdDialog.showPasswordDialog(MainActivity.this, "Enter Password", "Please enter password to setting",
                    data -> {
                        if (PasswordUtils.verifyPassword(data, PasswordUtils.KEY_SETTING)) {
                            new Thread(() -> {
                                stopTermLinkService();
                                SystemClock.sleep(70);//dismiss the dialog before start setting activity.
                                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                            }).start();
                        } else {
                            Toast.makeText(MainActivity.this, "Password invalid", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        settingClickTime = 0;
    }
}