/*
 * ============================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) 2018-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 *
 * Module Date: 2019/7/30
 * Module Auth: Frank.W
 * Description:
 *
 * Revision History:
 * Date                   Author                       Action
 * 2019/7/30              Frank.W                       Create
 * ============================================================================
 */

package com.pax.tlmanager.keyboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.pax.tlmanager.R;

public class SoftKeyboardView extends RelativeLayout {

    public SoftKeyboardView(Context context) {
        super(context);
        init();
    }

    private void init() {
        View keyboardView = LayoutInflater.from(getContext()).inflate(R.layout.pax_layout_keyboard, this, false);
        LayoutParams layoutParamsKeyboard = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(keyboardView, layoutParamsKeyboard);
    }
}
