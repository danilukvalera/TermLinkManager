/*
 * ============================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) 2018-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.

 * Module Date: 2019-3-7
 * Module Auth: Justin.Z
 * Description:

 * Revision History:
 * Date                   Author                       Action
 * 2019-3-7               Justin.Z                      Create
 * ============================================================================
 */
package com.pax.tlmanager.cusui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pax.customui.constant.status.SecurityStatus;
import com.pax.customui.core.helper.SecurityHelper;
import com.pax.tlmanager.R;
import com.pax.tlmanager.cusui.base.BaseActivity;
import com.pax.tlmanager.cusui.base.RespStatusImpl;
import com.pax.tlmanager.cusui.event.EventBusUtil;
import com.pax.tlmanager.cusui.event.PINEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class EnterCVVActivity extends BaseActivity implements View.OnClickListener, SecurityHelper.ISecurityListener {

    TextView promptTitle;
    TextView pwdInputText;
    Button confirmBtn;


    private SecurityHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_cvv);
        EventBusUtil.register(this);
        promptTitle = findViewById(R.id.prompt_title);
        pwdInputText = findViewById(R.id.pin_input_text);
        confirmBtn = findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(this);


        promptTitle.setText("Please Input V-Code");
        helper = new SecurityHelper(this, new RespStatusImpl(this));
        helper.start(this, getIntent());

    }


    @Override
    public void onClick(View view) {
        helper.sendNext();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            helper.sendAbort();
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            helper.sendNext();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        EventBusUtil.unregister(this);
        helper.stop();
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        ViewTreeObserver observer = pwdInputText.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                pwdInputText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int[] location = new int[2];
                pwdInputText.getLocationInWindow(location);
                int x = location[0];
                int y = location[1];
                int barHeight = 0;
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    barHeight = getResources().getDimensionPixelSize(resourceId);
                }
                helper.setSecurityArea(x, y - barHeight, pwdInputText.getWidth(),
                        pwdInputText.getHeight(),"Please Input CVV.",33,null);
            }
        });
    }

    @Override
    public void onShowMessage(@Nullable String transName, @Nullable String message) {
        //Display the message in your TextView.
    }

    @Override
    public void onShowAmount(long amount) {
        //Display the amount in your TextView.
    }

    @Override
    public void onShowCurrency(@Nullable String currency, @Nullable String currencySymbol, boolean isPoint) {
        //Display the currency in your TextView.
    }

    /**
     * @param event When show inputArea, the input state will be send by PINEvent.
     *              See {@link com.pax.tlmanager.cusui.receiver.PinStatusReceiver}
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAsync(PINEvent event) {
        String action = event.getStatus();
        switch (action) {
            case SecurityStatus.SECURITY_ENTERING:
                Log.i("OnEvent", "SECURITY_ENTERING");
                break;
            case SecurityStatus.SECURITY_DELETE:
                Log.i("OnEvent", "SECURITY_DELETE");
                break;
            case SecurityStatus.SECURITY_FORMAT_ERROR:
                Log.i("OnEvent", "SECURITY_FORMAT_ERROR");
                break;
            default:
                break;
        }
    }

}
