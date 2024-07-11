package com.pax.tlmanager.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.pax.tlmanager.App;
import com.pax.tlmanager.BuildConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Used to manage user passwords. To ensure that the password will not appear in the code in plain text,
 * the MD5 method can be used to store the password that has been set.
 * When the password is #, it use the Date with MMDDYYYY format today.
 */
public class PasswordUtils {
    public static final String KEY_SETTING = "KEY_SETTING";
    public static final String DEFAULT_PASSWORD = "#";
    public static final String DEFAULT_PASSWORD_DEBUG = "C4CA4238A0B923820DCC509A6F75849B";
    public static final String SP_FILE_NAME = "password";

    private PasswordUtils() {
    }

    public static boolean verifyPassword(String originPwd, String keyType) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        String md5Key = preferences.getString(keyType, BuildConfig.DEBUG ? DEFAULT_PASSWORD_DEBUG : DEFAULT_PASSWORD);
        if (DEFAULT_PASSWORD.equals(md5Key)) {
            return originPwd.equals(getDateStr());
        }
//        return md5(originPwd).equals(md5Key);
        return true;
    }

    public static boolean modifyPassword(String oldPwd, String newPwd, String keyType) {
        if (verifyPassword(oldPwd, keyType)) {
            SharedPreferences preferences = App.getApp().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
            if (DEFAULT_PASSWORD.equals(newPwd)) {
                preferences.edit().putString(keyType, DEFAULT_PASSWORD).apply();
            } else {
                preferences.edit().putString(keyType, md5(newPwd)).apply();
            }

            return true;
        }
        return false;
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void resetPwd(String password) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        if (DEFAULT_PASSWORD.equals(password) || TextUtils.isEmpty(password)) {
            preferences.edit().putString(KEY_SETTING, "#").apply();
        } else {
            preferences.edit().putString(KEY_SETTING, md5(password)).apply();
        }
    }

    public static String getDateStr() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("MMddyyyy", Locale.US);
        return format.format(date);
    }
}
