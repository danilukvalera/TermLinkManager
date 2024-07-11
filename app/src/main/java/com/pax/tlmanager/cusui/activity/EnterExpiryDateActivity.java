package com.pax.tlmanager.cusui.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pax.customui.core.helper.EnterExpiryDateHelper;
import com.pax.tlmanager.R;
import com.pax.tlmanager.cusui.base.BaseActivity;
import com.pax.tlmanager.cusui.base.RespStatusImpl;


public class EnterExpiryDateActivity extends BaseActivity implements View.OnClickListener, EnterExpiryDateHelper.IEnterExpiryDateListener {

    TextView promptTv;
    EditText mEditText;
    Button confirmBtn;

    private EnterExpiryDateHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_info);

        promptTv = findViewById(R.id.prompt_tv);
        mEditText = findViewById(R.id.data_edt);
        confirmBtn = findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(this);

        promptTv.setText("Please input expiry date(MM/YY)");
        mEditText.setCursorVisible(false);
        mEditText.requestFocus();
        mEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        helper = new EnterExpiryDateHelper(this, new RespStatusImpl(this));
        helper.start(this, getIntent());

    }


    @Override
    public void onClick(View view) {
        helper.sendNext(mEditText.getText().toString());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            helper.sendAbort();
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            helper.sendNext(mEditText.getText().toString());
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        helper.stop();
        super.onDestroy();
    }

    @Override
    public void onShowMessage(@Nullable String transName, @Nullable String message) {
        //Display the message in your TextView or Toast etc.
    }
}
