package com.pax.tlmanager.cusui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.pax.customui.core.helper.ShowMessageHelper;
import com.pax.customui.widget.PaxTextView;
import com.pax.tlmanager.R;
import com.pax.tlmanager.cusui.base.BaseActivity;
import com.pax.tlmanager.cusui.base.ManagerStatusImpl;

/**
 * Created by Justin.Z on 2019-12-19
 */
public class ShowMessageCenterActivity extends BaseActivity implements ShowMessageHelper.IShowMessageCenterListener {

    private PaxTextView mTitle;
    private PaxTextView mMessage1;
    private PaxTextView mMessage2;

    private ShowMessageHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message_center);
        mTitle = findViewById(R.id.thx_msg_title);
        mMessage1 = findViewById(R.id.thx_msg_01);
        mMessage2 = findViewById(R.id.thx_msg_02);
        mTitle.setTextSize((int) getResources().getDimension(R.dimen.font_button));
        mMessage1.setTextSize((int) getResources().getDimension(R.dimen.font_size_prompt));
        mMessage2.setTextSize((int) getResources().getDimension(R.dimen.font_size_value));
        mTitle.setCenterAlign(true);
        mMessage1.setCenterAlign(true);
        mMessage2.setCenterAlign(true);
        mTitle.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        mMessage1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        mMessage2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        helper = new ShowMessageHelper(this, new ManagerStatusImpl(this) {
            @Override
            public void onReset() {
                Log.d("showMessage", "onReset");
                ShowMessageCenterActivity.super.finish();
            }

            @Override
            public void onClearMsg() {
                mTitle.setText(null);
            }

            @Override
            public void onAccepted() {
                ShowMessageCenterActivity.super.finish();
            }
        });
        helper.start(this, getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("onNewIntent", intent.getAction());
        setIntent(intent);
        helper.start(this, intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            helper.sendAbort();
        }
        return false;
    }

    @Override
    public void finish() {
        if (helper != null && helper.isNoBlocking()) {
            return;
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        helper.stop();
        super.onDestroy();
    }

    @Override
    public void showTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void showMessage1(String msg) {
        mMessage1.setText(msg);
    }

    @Override
    public void showMessage2(String mes) {
        mMessage2.setText(mes);
    }

    @Override
    public void onShowMessage(@Nullable String transName, @Nullable String message) {
        //Show message to your TextView
    }
}
