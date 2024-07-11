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
import android.inputmethodservice.Keyboard;

public class SoftKeyBoard extends Keyboard {

    public SoftKeyBoard(Context context, int layoutTemplateResId, CharSequence characters, int columns,
                        int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    public SoftKeyBoard(Context context, int xmlLayoutResId, int modeId, int width, int height) {
        super(context, xmlLayoutResId, modeId, width, height);
        setSpecialKey(57420);
    }

    public SoftKeyBoard(Context context, int xmlLayoutResId, int modeId) {
        super(context, xmlLayoutResId, modeId);
    }

    public SoftKeyBoard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    private void setSpecialKey(int keyCode) {
        for (int i = 0; i < getKeys().size(); i++) {
            if (getKeys().get(i).codes[0] == keyCode) {
                getKeys().get(i).height += getVerticalGap();
                return;
            }
        }
    }
}
