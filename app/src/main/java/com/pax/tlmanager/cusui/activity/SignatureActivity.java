package com.pax.tlmanager.cusui.activity;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pax.customui.core.helper.SignatureHelper;
import com.pax.tlmanager.R;
import com.pax.tlmanager.cusui.base.BaseActivity;
import com.pax.tlmanager.cusui.base.RespStatusImpl;
import com.pax.tlmanager.cusui.widget.ElectronicSignatureView;

import java.util.List;

public class SignatureActivity extends BaseActivity implements View.OnClickListener, SignatureHelper.ISignatureListener {

    TextView amountTv;
    LinearLayout amountLayout;
    ElectronicSignatureView mSignatureView;
    CountDownTimer countDownTimer;
    RelativeLayout writeUserName;

    Button clearBtn;
    Button confirmBtn;
    Button cancelBtn;

    TextView signLine1;
    TextView signLine2;
    TextView tvTimeout;

    private boolean processing = false;

    private SignatureHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authgraph_layout);

        amountTv = (TextView) findViewById(R.id.trans_amount_tv);
        amountLayout = (LinearLayout) findViewById(R.id.trans_amount_layout);
        writeUserName = (RelativeLayout) findViewById(R.id.writeUserNameSpace);
        clearBtn = (Button) findViewById(R.id.clear_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        signLine1 = findViewById(R.id.tv_title_signature);
        signLine2 = findViewById(R.id.tv_text_signature);
        tvTimeout = findViewById(R.id.tv_text_timeout);

        cancelBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        confirmBtn.requestFocus();
        // 内置签名板
        mSignatureView = new ElectronicSignatureView(SignatureActivity.this);
        mSignatureView.setBitmap(new Rect(0, 0, 384, 128), 0, Color.WHITE);
        writeUserName.addView(mSignatureView);
        helper = new SignatureHelper(this, new RespStatusImpl(this));
        helper.start(this, getIntent());
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (i) {
            case R.id.clear_btn:
                if (isProcessing()) {
                    return;
                }
                setProcessFlag();
                mSignatureView.clear();
                clearProcessFlag();

                break;
            case R.id.confirm_btn:
                if (isProcessing()) {
                    return;
                }
                if (!mSignatureView.getTouched()) {
                    return;
                }

                try {
                    confirmBtn.setClickable(false);
                    setProcessFlag();

                    List<float[]> pathPos = mSignatureView.getPathPos();
                    int length = 0;
                    for (float[] ba : pathPos) {
                        length += ba.length;
                    }
                    short[] total = new short[length];
                    int index = 0;
                    for (float[] ba : pathPos) {
                        for (float b : ba) {
                            total[index++] = (short) b;
                        }
                    }
                    Log.i("SignatureActivity", "total Length = " + total.length);

                    clearProcessFlag();
                    String data = mSignatureView.getSignData();
                    helper.sendNext(data);
                } finally {
                    confirmBtn.setClickable(true);
                }
                break;
            case R.id.cancel_btn:
                helper.sendAbort();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            helper.sendAbort();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        helper.stop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    protected void setProcessFlag() {
        processing = true;
    }

    protected void clearProcessFlag() {
        processing = false;
    }

    protected boolean isProcessing() {
        return processing;
    }

    @Override
    public void onShowAmount(long amount) {
        if (amount != 0) {
            amountTv.setText(String.valueOf(amount));
        } else {
            amountLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onShowCurrency(@Nullable String currency, @Nullable String currencySymbol, boolean isPoint) {
        //The corresponding currency symbol is displayed according to the currency code.
    }


    @Override
    public void onShowMessage(@Nullable String transName, @Nullable String message) {
        //Selectively display some prompt content.
    }

    @Override
    public void onShowTimeout(long timeout) {
        if (timeout > 0) {
            countDownTimer = new CountDownTimer(timeout, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    runOnUiThread(() -> tvTimeout.setText(String.format("( %d s )", (int) millisUntilFinished / 1000)));
                }

                @Override
                public void onFinish() {
                    helper.sendAbort();
                }
            };
            countDownTimer.start();
        }
    }

    @Override
    public void onShowSignLine(String line1, String line2) {
        signLine1.setText(line1);
        signLine2.setText(line2);
    }

    @Override
    public void onHideCancelBtn() {
        cancelBtn.setVisibility(View.GONE);
    }
}
