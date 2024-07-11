package com.pax.tlmanager;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.pax.devicemanager.PaxDeviceUtils;
import com.pax.termlinkmanager.TLCommSetting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ParameterManager {
    public static final String KEY_COMM_UART = "setting_comm_uart";
    public static final String KEY_COMM_USB = "setting_comm_usb";
    public static final String KEY_COMM_BLE = "setting_comm_ble";
    public static final String KEY_COMM_ETHERNET = "setting_comm_net";
    public static final String KEY_COMM_HOST = "setting_comm_net_host";
    public static final String KEY_COMM_PORT = "setting_comm_net_port";
    public static final String KEY_COMM_ETH_TYPE = "setting_comm_net_protocol";
    public static final String KEY_COMM_RATE = "setting_comm_uart_rate";
    public static final String KEY_PROMPT_MESSAGE = "setting_prompt_msg";
    public static final String KEY_CUSTOM_UI_SUPPORT = "setting_custom_ui_support";
    public static final String KEY_CLIENT_MODE = "setting_comm_net_client";

    public static final String KEY_BANNER_INTERVAL = "setting_banner_interval";
    public static final String KEY_IDLE_IMG_1 = "KEY_IDLE_IMG_1";
    public static final String KEY_IDLE_IMG_2 = "KEY_IDLE_IMG_2";
    public static final String KEY_IDLE_IMG_3 = "KEY_IDLE_IMG_3";

    public static final String DEFAULT_PIC1 = "img1.png";

    private static ParameterManager instance;

    private SharedPreferences preferences;

    private String internalPath = App.getApp().getFilesDir().getAbsolutePath() + "/paxstore/";

    private ParameterManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static ParameterManager getInstance(Context context) {
        if (instance == null) {
            instance = new ParameterManager(context);
        }
        return instance;
    }

    public String getCommType() {
        if (isEth()) {
            return preferences.getString(KEY_COMM_ETH_TYPE, TLCommSetting.TCP);
        }
        if (preferences.getBoolean(KEY_COMM_BLE, false)) {
            return TLCommSetting.BLE;
        }
        if (preferences.getBoolean(KEY_COMM_USB, false)) {
            return TLCommSetting.USB;
        }
        if (isUART()) {
            return TLCommSetting.UART;
        }
        return TLCommSetting.TCP;
    }

    public void setCommType(String value) {
        switch (value) {
            case TLCommSetting.TCP_CLIENT:
            case TLCommSetting.SSL_CLIENT:
                preferences.edit().putBoolean(KEY_CLIENT_MODE, true)
                        .putBoolean(KEY_COMM_ETHERNET, true)
                        .putBoolean(KEY_COMM_BLE, false)
                        .putBoolean(KEY_COMM_USB, false)
                        .putBoolean(KEY_COMM_UART, false)
                        .putString(KEY_COMM_ETH_TYPE, value)
                        .apply();
                break;
            case TLCommSetting.TCP:
            case TLCommSetting.SSL:
            case TLCommSetting.HTTP:
            case TLCommSetting.HTTPS:
                preferences.edit().putBoolean(KEY_CLIENT_MODE, false)
                        .putBoolean(KEY_COMM_ETHERNET, true)
                        .putBoolean(KEY_COMM_BLE, false)
                        .putBoolean(KEY_COMM_USB, false)
                        .putBoolean(KEY_COMM_UART, false)
                        .putString(KEY_COMM_ETH_TYPE, value)
                        .apply();
                break;
            case TLCommSetting.UART:
                preferences.edit().putBoolean(KEY_COMM_UART, true)
                        .putBoolean(KEY_COMM_ETHERNET, false)
                        .putBoolean(KEY_COMM_BLE, false)
                        .putBoolean(KEY_COMM_USB, false)
                        .apply();
                break;
            case TLCommSetting.USB:
                preferences.edit().putBoolean(KEY_COMM_USB, true)
                        .putBoolean(KEY_COMM_ETHERNET, false)
                        .putBoolean(KEY_COMM_BLE, false)
                        .putBoolean(KEY_COMM_UART, false)
                        .apply();
                break;
            case TLCommSetting.BLE:
                preferences.edit().putBoolean(KEY_COMM_BLE, true)
                        .putBoolean(KEY_COMM_USB, false)
                        .putBoolean(KEY_COMM_ETHERNET, false)
                        .putBoolean(KEY_COMM_UART, false)
                        .apply();
                break;
            default:
                break;
        }
    }

    public boolean isEth() {
        return preferences.getBoolean(KEY_COMM_ETHERNET, true);
    }

    public boolean isClientMode() {
        return preferences.getBoolean(KEY_CLIENT_MODE, false);
    }

    public boolean isUART() {
        return preferences.getBoolean(KEY_COMM_UART, false);
    }

    public boolean isUSB() {
        return preferences.getBoolean(KEY_COMM_USB, false);
    }

    public String getBaudRate() {
        return preferences.getString(KEY_COMM_RATE, "9600");
    }

    public boolean isBlueTooth() {
        return preferences.getBoolean(KEY_COMM_BLE, false);
    }

    public String getEthernetPort() {
        return preferences.getString(KEY_COMM_PORT, "10009");
    }

    public boolean showPrompt() {
        return preferences.getBoolean(KEY_PROMPT_MESSAGE, false);
    }

    public String getHost() {
        return preferences.getString(KEY_COMM_HOST, "127.0.0.1");
    }

    public boolean customerUiSupport() {
        return preferences.getBoolean(KEY_CUSTOM_UI_SUPPORT, false);
    }

    public void setParameter(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public void setParameter(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public String getImgPath1() {
        return preferences.getString(KEY_IDLE_IMG_1, null);
    }

    public void setImgPath1(String imgPath1) {
        preferences.edit().putString(KEY_IDLE_IMG_1, imgPath1).apply();
    }

    public String getImgPath2() {
        return preferences.getString(KEY_IDLE_IMG_2, null);
    }

    public void setImgPath2(String imgPath2) {
        preferences.edit().putString(KEY_IDLE_IMG_2, imgPath2).apply();
    }

    public String getImgPath3() {
        return preferences.getString(KEY_IDLE_IMG_3, null);
    }

    public void setImgPath3(String imgPath3) {
        preferences.edit().putString(KEY_IDLE_IMG_3, imgPath3).apply();
    }

    public List<String> getIdleImgPaths() {
        String imgPath1 = getImgPath1();
        String imgPath2 = getImgPath2();
        String imgPath3 = getImgPath3();

        ArrayList<String> strings = new ArrayList<>();
        if (!isEmpty(imgPath1)) {
            strings.add(internalPath + imgPath1);
        }
        if (!isEmpty(imgPath2)) {
            strings.add(internalPath + imgPath2);
        }
        if (!isEmpty(imgPath3)) {
            strings.add(internalPath + imgPath3);
        }
        return strings;
    }

    private boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public String getInternalPath() {
        return internalPath;
    }

    public int getBannerInterval() {
        int second = preferences.getInt(KEY_BANNER_INTERVAL, 5);
        return second * 1000;
    }

    public void setBannerInterval(int intervalSecond) {
        preferences.edit().putInt(KEY_BANNER_INTERVAL, intervalSecond).apply();
    }

    private void deleteFile(String fileName) {
        File f = new File(internalPath + fileName);
        if (f.exists() && f.isFile()) {
            f.delete();
        }
    }

    public void deleteAllPic() {
        if (getImgPath1() != null) {
            copyDefaultPic();
        }
        if (getImgPath2() != null) {
            deleteFile(getImgPath2());
        }
        if (getImgPath3() != null) {
            deleteFile(getImgPath3());
        }
        setImgPath1(DEFAULT_PIC1);
        setImgPath2("");
        setImgPath3("");
        setBannerInterval(3);
    }

    public void copyDefaultPic() {
        String fileName = "pic_ad1.png";
        if (PaxDeviceUtils.isLowResolutionLandDevice()) {
            fileName = "pic_ad1_q10a.png";
        } else if (PaxDeviceUtils.isLandscapeScreen()) {
            fileName = "pic_ad1_h.png";
        }
        try (InputStream inputStream = App.getApp().getAssets().open(fileName);
             OutputStream outputStream = new FileOutputStream(getInternalPath() + DEFAULT_PIC1)) {
            byte[] buffer = new byte[4096];
            int length = inputStream.read(buffer);
            while (length > 0) {
                outputStream.write(buffer, 0, length);
                length = inputStream.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        File path = new File(getInternalPath());
        if (!path.exists()) {
            if (!path.mkdirs()) {
                return;
            }
        }
        File defaultPic = new File(getInternalPath() + DEFAULT_PIC1);
        if (!defaultPic.exists()) {
            copyDefaultPic();
            setImgPath1(DEFAULT_PIC1);
        }
    }
}
