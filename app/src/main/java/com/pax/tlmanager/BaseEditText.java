/*
 *
 *  ============================================================================
 *  = COPYRIGHT
 *           PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *    This software is supplied under the terms of a license agreement or nondisclosure
 *    agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *    disclosed except in accordance with the terms in that agreement.
 *       Copyright (C) 2018-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 *
 *  Module Date: 2020/04/26
 *  Module Auth: Frank.W
 *  Description:
 *
 *  Revision History:
 *  Date                   Author                       Action
 *  2020/04/26             Frank.W                      Create
 *  ============================================================================
 */

package com.pax.tlmanager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import java.lang.reflect.Field;

/**
 * the base class is used to avoid cache leak.
 * D/LeakCanary: * ↳ InputConnectionWrapper.!(mTarget)!
 * D/LeakCanary: * ↳ EditableInputConnection.!(mTextView)!
 * D/LeakCanary: * ↳ AppCompatEditText.mContext
 */
public class BaseEditText extends AppCompatEditText {

    private static Field mParent;

    static {
        try {
            mParent = View.class.getDeclaredField("mParent");
            mParent.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public BaseEditText(Context context) {
        super(context);
    }

    public BaseEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            if (mParent != null)
                mParent.set(this, null);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDetachedFromWindow();
    }
}
