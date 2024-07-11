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

package com.pax.tlmanager.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.pax.devicemanager.PaxDeviceUtils;
import com.pax.tlmanager.R;
import com.pax.tlmanager.keyboard.KeyBoardUtils;

public class InputPwdDialog extends Dialog {

    private String title;
    private String prompt;

    private TextView titleTv;
    private TextView subtitleTv;
    private EditText pwdEdt;
    private OnPwdListener listener;
    private Context mContext;

    public InputPwdDialog(Context context, String title, String prompt) {
        super(context, R.style.popup_dialog);
        this.title = title;
        this.prompt = prompt;
        this.mContext = context;
    }

    public static void showPasswordDialog(Context context, String title, String prompt, OnPwdListener listener) {
        InputPwdDialog dialog = new InputPwdDialog(context, title, prompt);
        dialog.setPwdListener(listener);
        dialog.show();
    }

    public void setPwdListener(OnPwdListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View convertView = getLayoutInflater().inflate(R.layout.dialog_input_pwd, null);
        setContentView(convertView);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
        initViews(convertView);
    }

    private void initViews(View view) {
        titleTv = view.findViewById(R.id.prompt_title);
        titleTv.setText(title);

        subtitleTv = view.findViewById(R.id.prompt_no_pwd);
        if (prompt != null) {
            subtitleTv.setText(prompt);
        } else {
            subtitleTv.setVisibility(View.INVISIBLE);
        }

        pwdEdt = view.findViewById(R.id.pwd_input_text);
        pwdEdt.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                Log.d("testKeyboard", "onKey2: Confirm");
                String content = pwdEdt.getText().toString();
                if (listener != null) {
                    listener.onEnter(content);
                }
                dismiss();
            }
            return false;
        });
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(pwdEdt.getWindowToken(), 0);
        pwdEdt.setFocusable(true);
        if (PaxDeviceUtils.hasPhysicalKeyboard()) {
            pwdEdt.setShowSoftInputOnFocus(false);
        } else {
            KeyBoardUtils utils = new KeyBoardUtils(mContext, new EditText[]{pwdEdt}, view);
            utils.showKeyboardLayout();
        }
        pwdEdt.requestFocus();
    }

    @Override
    public void show() {
        super.show();

    }

    public interface OnPwdListener {
        void onEnter(String data);
    }
}
