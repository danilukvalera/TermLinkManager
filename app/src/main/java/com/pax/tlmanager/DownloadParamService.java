package com.pax.tlmanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.pax.market.android.app.sdk.StoreSdk;
import com.pax.market.android.app.sdk.util.NotificationUtils;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.DownloadResultObject;
import com.pax.market.api.sdk.java.base.exception.NotInitException;
import com.pax.tlmanager.setting.PasswordUtils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Receive and set the parameters from PAXSTORE.
 */
public class DownloadParamService extends Service {
    public static final String INTENT_UPDATE_RESOURCE = "com.pax.us.paxstore.updateresource";
    private static final String TAG = "DownloadParamService";
    Thread downloadThread;
    private String filePath = App.getApp().getFilesDir().getAbsolutePath() + "/paxstore/";

    private void setParameters() {
        try {
            File parameterFile = new File(filePath + "tm_cap.p");
            //Now, we set the params from PAXSTORE are XML format.
            LinkedHashMap<String, String> resultMap = StoreSdk.getInstance().paramApi().parseDownloadParamXmlWithOrder(parameterFile);
            if (resultMap != null && !resultMap.isEmpty()) {
                ParameterManager parameterManager = ParameterManager.getInstance(getApplicationContext());
                for (Map.Entry<String, String> item : resultMap.entrySet()) {
                    switch (item.getKey()) {
                        case "tm.cap.commtype":
                            parameterManager.setCommType(item.getValue());
                            break;
                        case "tm.cap.hostaddress":
                            parameterManager.setParameter(ParameterManager.KEY_COMM_HOST, item.getValue());
                            break;
                        case "tm.cap.port":
                            parameterManager.setParameter(ParameterManager.KEY_COMM_PORT, item.getValue());
                            break;
                        case "tm.cap.baudrate":
                            parameterManager.setParameter(ParameterManager.KEY_COMM_RATE, item.getValue());
                            break;
                        case "tm.cap.showmessage":
                            parameterManager.setParameter(ParameterManager.KEY_PROMPT_MESSAGE, "Y".equals(item.getValue()));
                            break;
                        case "tm.cap.customuisupport":
                            parameterManager.setParameter(ParameterManager.KEY_CUSTOM_UI_SUPPORT, "Y".equals(item.getValue()));
                            break;
                        case "tm.cap.defaultpassword":
                            PasswordUtils.resetPwd(item.getValue());
                            break;
                        case "tm.cap.idlepicture1":
                            parameterManager.setImgPath1(item.getValue());
                            break;
                        case "tm.cap.idlepicture2":
                            parameterManager.setImgPath2(item.getValue());
                            break;
                        case "tm.cap.idlepicture3":
                            parameterManager.setImgPath3(item.getValue());
                            break;
                        default:
                            break;
                    }
                }
                sendBroadcast(new Intent(INTENT_UPDATE_RESOURCE)); //Send Broadcast to notify the idle interface to switch the connection mode
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        NotificationUtils.showForeGround(this, R.mipmap.ic_launcher, "Downloading params from PAXSTORE");
        downloadThread = new Thread(() -> {
            DownloadResultObject downloadResult = null;
            try {
                Log.i(TAG, "call sdk API to download parameter");
                downloadResult = StoreSdk.getInstance().paramApi().downloadParamToPath(getApplication().getPackageName(), BuildConfig.VERSION_CODE, filePath);
                Log.i(TAG, downloadResult.toString());
            } catch (NotInitException e) {
                Log.e(TAG, "e:" + e);
            }

            if (downloadResult != null) {
                if (downloadResult.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
                    Log.i(TAG, "download successful.");
                    setParameters();
                } else {
                    Log.e(TAG, "ErrorCode: " + downloadResult.getBusinessCode() + "ErrorMessage: " + downloadResult.getMessage());
                }
            }
        },"DownloadParamThread");
        downloadThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadThread != null) {
            downloadThread = null;
        }
    }
}
