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

import com.pax.tlmanager.cusui.event.ClssLightEvent;
import com.pax.tlmanager.cusui.event.EventBusUtil;

public class ClssStatusReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.isEmpty(intent.getAction()))
            return;
        /*
        After receiving the command to change the contactless indicator light,
        for devices without physical lights, an effect similar to
         the contactless light can be drawn on the screen.
        */
        EventBusUtil.doEvent(new ClssLightEvent(intent.getAction()));

    }

}
