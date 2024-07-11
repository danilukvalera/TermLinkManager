package com.pax.tlmanager.cusui.base;

import android.app.Activity;

public class ManagerStatusImpl extends RespStatusImpl {

    public ManagerStatusImpl(Activity activity) {
        super(activity);
    }

    @Override
    public void onReset() {
        //when reset view, now it not used.
    }

    @Override
    public void onClearMsg() {
        //when need clear the message on screen, now it not used.
    }
}
