/*
 * ============================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) 2018-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.

 * Module Date: 2019/3/6
 * Module Auth: Justin.Z
 * Description:

 * Revision History:
 * Date                   Author                       Action
 * 2019/3/6               Justin.Z                      Create
 * ============================================================================
 */
package com.pax.tlmanager.cusui.receiver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.pax.tlmanager.cusui.event.EventBusUtil;
import com.pax.tlmanager.cusui.event.PINEvent;


public class PinStatusReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("PinStatusReceiver", "onReceive: " + intent.getAction());
        if (TextUtils.isEmpty(intent.getAction()))
            return;
        String action = intent.getAction();
        long length = intent.getLongExtra("pinLength", 0);
        /*
        This event is triggered when a button is pressed on the security keyboard or PED,
         which determines how many digits are displayed on the screen to prompt the user.
        */
        EventBusUtil.doEvent(new PINEvent(action, length));
        Toast toast = Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();

    }
}
