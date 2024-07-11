package com.pax.tlmanager.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pax.devicemanager.PaxDeviceUtils;
import com.pax.tlmanager.R;
import com.pax.tlmanager.keyboard.KeyBoardUtils;

/**
 * Set the password for the user to log out or goto setting.
 */
public class SettingPasswordFragment extends Fragment{
    private EditText etOldPin;
    private EditText etNewPin;
    private EditText etNewPin2;
    private TextView tvWarning;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_password, container, false);
        etOldPin = contentView.findViewById(R.id.et_old_pin);
        etNewPin = contentView.findViewById(R.id.et_new_pin);
        etNewPin2 = contentView.findViewById(R.id.et_new_pin_again);
        tvWarning = contentView.findViewById(R.id.tv_warning);
        contentView.findViewById(R.id.btn_ok).setOnClickListener(this::onClick);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etOldPin.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etNewPin.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etNewPin2.getWindowToken(), 0);
        etOldPin.setFocusable(true);
        etNewPin.setFocusable(true);
        etNewPin2.setFocusable(true);
        if (PaxDeviceUtils.hasPhysicalKeyboard()) {
            etOldPin.setShowSoftInputOnFocus(false);
            etNewPin.setShowSoftInputOnFocus(false);
            etNewPin2.setShowSoftInputOnFocus(false);
        } else {
            KeyBoardUtils utils = new KeyBoardUtils(getContext(), new EditText[]{etOldPin,etNewPin,etNewPin2}, contentView);
            utils.showKeyboardLayout();
        }
        etOldPin.requestFocus();
        return contentView;
    }

    public void onClick(View v) {
        if (TextUtils.isEmpty(etOldPin.getText())) {
            tvWarning.setText(R.string.enter_old_pwd);
            return;
        }
        if (TextUtils.isEmpty(etNewPin.getText())) {
            tvWarning.setText(R.string.enter_new_pwd);
            return;
        }
        if (TextUtils.isEmpty(etNewPin2.getText())) {
            tvWarning.setText(R.string.enter_new_pwd_again);
            return;
        }
        if (!etNewPin.getText().toString().equals(etNewPin2.getText().toString())) {
            tvWarning.setText(R.string.new_pwd_different);
            return;
        }
        if (PasswordUtils.modifyPassword(etOldPin.getText().toString(), etNewPin.getText().toString(),
                PasswordUtils.KEY_SETTING)) {
            Toast.makeText(getContext(), R.string.reset_pwd_ok,Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {
            tvWarning.setText(R.string.old_password_incorrect);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        hideInputKeyboard();
    }

    public void hideInputKeyboard() {
        Activity activity = getActivity();
        if(activity!=null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager mInputKeyBoard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (activity.getCurrentFocus() != null) {
                        mInputKeyBoard.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    }
                }
            });
        }
    }
}
