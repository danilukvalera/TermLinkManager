package com.pax.tlmanager.cusui.base;

import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pax.customui.core.api.IUIListener;
import com.pax.tlmanager.App;

/**
 * Created by Justin.Z on 2019-12-19
 */
public abstract class BaseActivity extends AppCompatActivity implements IUIListener {

    @Override
    public void showDetail(String msg) {
        Log.d("BaseActivity", msg);
        Toast toast = Toast.makeText(App.getApp(), msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }
}
