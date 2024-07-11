package com.pax.tlmanager.cusui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pax.customui.constant.entry.enumeration.PinStyles;
import com.pax.customui.constant.status.PINStatus;
import com.pax.customui.core.helper.EnterPinHelper;
import com.pax.tlmanager.R;
import com.pax.tlmanager.cusui.base.BaseActivity;
import com.pax.tlmanager.cusui.base.RespStatusImpl;
import com.pax.tlmanager.cusui.event.EventBusUtil;
import com.pax.tlmanager.cusui.event.PINEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EnterPinActivity extends BaseActivity implements EnterPinHelper.IEnterPinListener {
    private static final String TAG = "EnterPinEvent";
    TextView amountTv;
    TextView promptTitle;
    TextView pwdInputText;
    private int pinLen;


    private EnterPinHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);
        EventBusUtil.register(this);

        amountTv = findViewById(R.id.amount_tv);
        promptTitle = findViewById(R.id.prompt_title);
        pwdInputText = findViewById(R.id.pin_input_text);


        promptTitle.setText("Please Enter password");
        helper = new EnterPinHelper(this, new RespStatusImpl(this));
        helper.start(this, getIntent());


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            helper.sendAbort();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        helper.stop();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAsync(PINEvent event) {
        String action = event.getStatus();
        switch (action) {
            case PINStatus.PIN_ENTERING:
                Log.i(TAG, "PINStatus.PIN_ENTERING");
                pinLen++;
                String star = "*";
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pinLen; i++) {
                    sb.append(star);
                }
                pwdInputText.setText(sb.toString());
                break;
            case PINStatus.PIN_ENTER_CLEARED:
                Log.i(TAG, "PINStatus.PIN_ENTER_CLEARED");
                pinLen = 0;
                pwdInputText.setText("");
                break;
            case PINStatus.PIN_RNIB_TOUCH:
                Log.i(TAG, "PINStatus.PIN_RNIB_TOUCH");
                break;
            default:
                break;
        }
    }

    @Override
    public void onShowMessage(@Nullable String transName, @Nullable String message) {
        //Display the message in your TextView or Toast etc.
    }

    @Override
    public void onShowAmount(long amount) {
        //Display the amount in your TextView.
    }

    @Override
    public void onShowCurrency(@Nullable String currency, @Nullable String currencySymbol, boolean isPoint) {
        //Display the currency in your TextView.
    }

    @Override
    public void onShowPin(@Nullable String pinStyles, boolean isOnline, boolean isPinByapss) {
        if (pinStyles != null) {
            if (pinStyles.equals(PinStyles.LAST))
                promptTitle.setText("Please Enter PIN Last Time");
            else if (pinStyles.equals(PinStyles.RETRY))
                promptTitle.setText("Please Enter PIN Again");
            else
                promptTitle.setText("Please Enter PIN");
        }
    }
}
