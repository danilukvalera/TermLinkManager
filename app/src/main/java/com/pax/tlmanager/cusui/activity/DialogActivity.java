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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.pax.customui.constant.entry.EntryExtraData;
import com.pax.customui.constant.status.CardStatus;
import com.pax.customui.constant.status.ManagerStatus;
import com.pax.tlmanager.R;
import com.pax.tlmanager.cusui.MessageDialog;


public class DialogActivity extends AppCompatActivity {

    private MessageDialog messageDialog;

    public static void start(Context context, Intent intent) {
        Intent starter = new Intent(context, DialogActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        starter.setAction(intent.getAction());
        if (intent.getExtras() != null)
            starter.putExtras(intent.getExtras());
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (TextUtils.isEmpty(action)) {
            finish();
            return;
        }
        Log.i("StatusReceiver", "action :" + action);
        /*
        According to different Actions, different dialog boxes are displayed.
         Currently supported items:
         Warning RemoveCard
        */
        switch (action) {
            case CardStatus.CARD_PROCESS_STARTED:
                showProcessDialog("CARD PROCESS...");
                break;
            case ManagerStatus.REMOVE_CARD_STARTED:
                String message1 = bundle.getString(EntryExtraData.PARAM_MESSAGE_1);
                String messaeg2 = bundle.getString(EntryExtraData.PARAM_MESSAGE_2);
                String msg = (message1 != null ? message1 : "") + "\n"
                        + (messaeg2 != null ? messaeg2 : "");
                showProcessDialog(msg);
                break;
            case CardStatus.CARD_PROCESS_COMPLETED:
            case ManagerStatus.REMOVE_CARD_FINISHED:
                hideDialog();
                finish();
                break;
            default:
                break;
        }
    }

    private void showProcessDialog(String message) {
        showMessage(message);
    }

    private void hideDialog() {
        if (messageDialog != null) {
            messageDialog.dismiss();
            messageDialog = null;
        }
    }

    private void showMessage(String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable run = () -> {
            if (messageDialog != null) {
                messageDialog.setContent(msg);
            } else {
                messageDialog = new MessageDialog(DialogActivity.this);
                messageDialog.setContent(msg);
                messageDialog.show();
            }
        };
        handler.post(run);
    }
}
