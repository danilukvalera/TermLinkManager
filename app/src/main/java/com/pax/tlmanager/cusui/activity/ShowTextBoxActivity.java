package com.pax.tlmanager.cusui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toolbar;

import androidx.annotation.ColorInt;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pax.customui.core.helper.ShowTextBoxHelper;
import com.pax.customui.widget.PaxTextView;
import com.pax.tlmanager.R;
import com.pax.tlmanager.cusui.base.BaseActivity;
import com.pax.tlmanager.cusui.base.ManagerStatusImpl;
import com.pax.tlmanager.cusui.widget.ElectronicSignatureView;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ShowTextBoxActivity extends BaseActivity implements ShowTextBoxHelper.IShowTextBoxListener, View.OnClickListener {

    private static final String KEY_0 = "KEY0";
    private static final String KEY_1 = "KEY1";
    private static final String KEY_2 = "KEY2";
    private static final String KEY_3 = "KEY3";
    private static final String KEY_4 = "KEY4";
    private static final String KEY_5 = "KEY5";
    private static final String KEY_6 = "KEY6";
    private static final String KEY_7 = "KEY7";
    private static final String KEY_8 = "KEY8";
    private static final String KEY_9 = "KEY9";
    private static final String KEY_ENTER = "KEYENTER";
    private static final String KEY_CANCEL = "KEYCANCEL";
    private static final String KEY_CLEAR = "KEYCLEAR";
    private static final Map<Integer, String> KEY_MAP = new HashMap<>();

    static {
        KEY_MAP.put(KeyEvent.KEYCODE_0, KEY_0);
        KEY_MAP.put(KeyEvent.KEYCODE_1, KEY_1);
        KEY_MAP.put(KeyEvent.KEYCODE_2, KEY_2);
        KEY_MAP.put(KeyEvent.KEYCODE_3, KEY_3);
        KEY_MAP.put(KeyEvent.KEYCODE_4, KEY_4);
        KEY_MAP.put(KeyEvent.KEYCODE_5, KEY_5);
        KEY_MAP.put(KeyEvent.KEYCODE_6, KEY_6);
        KEY_MAP.put(KeyEvent.KEYCODE_7, KEY_7);
        KEY_MAP.put(KeyEvent.KEYCODE_8, KEY_8);
        KEY_MAP.put(KeyEvent.KEYCODE_9, KEY_9);
        KEY_MAP.put(KeyEvent.KEYCODE_ENTER, KEY_ENTER);
        KEY_MAP.put(KeyEvent.KEYCODE_BACK, KEY_CANCEL);
        KEY_MAP.put(KeyEvent.KEYCODE_ESCAPE, KEY_CANCEL);
        KEY_MAP.put(KeyEvent.KEYCODE_DEL, KEY_CLEAR);
    }

    private Button btnTextBoxCenter;
    private Button btnTextBoxCenter2;
    private Button btnTextBoxCenter3;
    private PaxTextView tvShowTextBox;
    private Toolbar toolbar;
    private RelativeLayout layoutBtn1;
    private RelativeLayout layoutBtn2;
    private RelativeLayout layoutBtn3;
    private LinearLayout rlBtn;
    private ScrollView scrollView;
    private LinearLayout signatureLayer;
    private RelativeLayout writeUserName;
    private Button clearBtn;
    private Button confirmBtn;
    private Button cancelBtn;
    private ImageView imgBarCode;
    private ElectronicSignatureView mSignatureView;
    private ShowTextBoxHelper helper;
    private String btnNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_text_box);
        btnTextBoxCenter = findViewById(R.id.btn_text_box_center);
        btnTextBoxCenter2 = findViewById(R.id.btn_text_box_center2);
        btnTextBoxCenter3 = findViewById(R.id.btn_text_box_center3);
        tvShowTextBox = findViewById(R.id.tv_show_text_box);
        toolbar = findViewById(R.id.toolbar_layer);
        layoutBtn1 = findViewById(R.id.rl_btn_1);
        layoutBtn2 = findViewById(R.id.rl_btn_2);
        layoutBtn3 = findViewById(R.id.rl_btn_3);
        rlBtn = findViewById(R.id.rl_btn);
        scrollView = findViewById(R.id.scroll_content);
        signatureLayer = findViewById(R.id.signature_layer);
        writeUserName = findViewById(R.id.writeUserNameSpace);
        clearBtn = findViewById(R.id.clear_btn);
        confirmBtn = findViewById(R.id.confirm_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        imgBarCode = findViewById(R.id.img_barcode);
        btnTextBoxCenter.setOnClickListener(this);
        btnTextBoxCenter2.setOnClickListener(this);
        btnTextBoxCenter3.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        tvShowTextBox.setCenterAlign(true);
        helper = new ShowTextBoxHelper(this, new ManagerStatusImpl(this) {
            @Override
            public void onReset() {
                ShowTextBoxActivity.super.finish();
            }

            @Override
            public void onClearMsg() {
                tvShowTextBox.setText(null);
            }

            @Override
            public void onAccepted() {
                ShowTextBoxActivity.super.finish();
            }
        });
        helper.start(this, getIntent());
        Log.i("onCreate", getIntent().getAction());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("onNewIntent", intent.getAction());
        setIntent(intent);
        helper.start(this, intent);
    }

    private void setBtnColor(Button btn, String color) {
        GradientDrawable drawable = (GradientDrawable) btn.getBackground();
        if (!TextUtils.isEmpty(color)) {
            if (getColor(color)) {
                btn.setTextColor(Color.WHITE);
            }
            drawable.setColor(Color.parseColor("#" + color));
        } else {
            drawable.setColor(Color.WHITE);
        }
    }

    public boolean getColor(String color) {
        int r = Integer.valueOf(color.substring(0, 2), 16);
        int g = Integer.valueOf(color.substring(2, 4), 16);
        int b = Integer.valueOf(color.substring(4), 16);
        //false: light color, true: dark color
        return (r * 0.299 + g * 0.578 + b * 0.114 < 192);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_text_box_center:
                btnNum = "1";
                helper.sendBtnNum(btnNum);
                break;
            case R.id.btn_text_box_center2:
                btnNum = "2";
                helper.sendBtnNum(btnNum);
                break;
            case R.id.btn_text_box_center3:
                btnNum = "3";
                helper.sendBtnNum(btnNum);
                break;
            case R.id.clear_btn:
                mSignatureView.clear();
                break;
            case R.id.confirm_btn:
                if (!mSignatureView.getTouched()) {
                    return;
                }

                confirmBtn.setEnabled(false);
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
                Log.i("ShowTextBox", "total Length = " + total.length);
                String data = mSignatureView.getSignData();
                helper.sendSignature(data);
                confirmBtn.setEnabled(true);
                break;
            case R.id.cancel_btn:
                helper.sendAbort();
                break;
            default:
                break;
        }
    }

    @Override
    public void onShowTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onShowSignatureBox() {
        rlBtn.setVisibility(View.GONE);
        signatureLayer.setVisibility(View.VISIBLE);

        mSignatureView = new ElectronicSignatureView(ShowTextBoxActivity.this);
        mSignatureView.setBitmap(new Rect(0, 0, 384, 128), 0, Color.WHITE);
        writeUserName.addView(mSignatureView);
        mSignatureView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP
                    && keyCode == KeyEvent.KEYCODE_ENTER) {
                confirmBtn.performClick();
                return true;
            }
            return false;
        });

        int height = getResources().getDisplayMetrics().heightPixels;
        ConstraintLayout.LayoutParams lps = (ConstraintLayout.LayoutParams) signatureLayer.getLayoutParams();
        lps.height = height / 2;
        signatureLayer.setLayoutParams(lps);
        scrollView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP
                    && keyCode == KeyEvent.KEYCODE_ENTER) {
                onClick(confirmBtn);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onShowText(String body) {
        tvShowTextBox.setText(body);
    }

    @Override
    public void finish() {
        if (helper != null && helper.isNoBlocking()) {
            return;
        }
        super.finish();
    }

    @Override
    public void onShowButton(ArrayList<Map.Entry<String, String>> btnProperty) {
        if (helper != null && helper.isNoBlocking()) return;
        signatureLayer.setVisibility(View.GONE);
        rlBtn.setVisibility(View.VISIBLE);
        switch (btnProperty.size()) {
            case 1:
                enableBtn(btnProperty, btnTextBoxCenter, 0, layoutBtn1);
                break;
            case 2:
                enableBtn(btnProperty, btnTextBoxCenter, 0, layoutBtn1);

                enableBtn(btnProperty, btnTextBoxCenter2, 1, layoutBtn2);
                break;
            case 3:
                enableBtn(btnProperty, btnTextBoxCenter, 0, layoutBtn1);

                enableBtn(btnProperty, btnTextBoxCenter2, 1, layoutBtn2);

                enableBtn(btnProperty, btnTextBoxCenter3, 2, layoutBtn3);
                break;
            default:
                break;
        }
    }

    @Override
    public void onShowBarCode(int barCodeType, String barCodeData) {
        // Show BarCaode here. currently support QR code, the barCodeType is 7.
        if (barCodeType == 7) {
            //means QR code
            imgBarCode.setImageBitmap(createQRCodeBitmap(barCodeData, 400, 400, Color.BLUE, Color.WHITE));
        }
    }

    private void enableBtn(ArrayList<Map.Entry<String, String>> btnProperty, Button btnTextBoxCenter, int i, RelativeLayout layoutBtn) {
        btnTextBoxCenter.setText(btnProperty.get(i).getKey());
        layoutBtn.setVisibility(View.VISIBLE);
        btnTextBoxCenter.setBackgroundResource(R.drawable.bg_textbox_btn);
        setBtnColor(btnTextBoxCenter, btnProperty.get(i).getValue());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("keycode:", "" + keyCode);

        if (signatureLayer.getVisibility() == View.VISIBLE) {
            handleSingatureKey(keyCode);
            return true;
        } else {
            if (helper.isEnableHardKey()) {
                hardKeyEnabled(keyCode);
            } else {
                hardKeyNotEnabled(keyCode);
            }
            if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_APP_SWITCH) {
                return super.onKeyDown(keyCode, event);
            }
            return true;
        }

    }

    private void handleSingatureKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                onClick(confirmBtn);
                break;
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_ESCAPE:
                onClick(cancelBtn);
                break;
            case KeyEvent.KEYCODE_DEL:
                onClick(clearBtn);
                break;
            default:
                break;
        }
    }

    private void hardKeyEnabled(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                if (helper.getHardKeyList().isEmpty()
                        || helper.getHardKeyList().contains(KEY_MAP.get(keyCode))) {
                    btnNum = KEY_MAP.get(keyCode);
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_ESCAPE:
            case KeyEvent.KEYCODE_DEL:
                if (helper.isNoBlocking()) {
                    return;
                }
                if (helper.getHardKeyList().isEmpty()
                        || helper.getHardKeyList().contains(KEY_MAP.get(keyCode))) {
                    btnNum = KEY_MAP.get(keyCode);
                }
                break;
            default:
                break;
        }


        if (!TextUtils.isEmpty(btnNum) && (helper.getHardKeyList().isEmpty() || helper.getHardKeyList().contains(btnNum))) {
            helper.sendBtnNum(btnNum);
        }
    }

    private void hardKeyNotEnabled(int keyCode) {
        String key = "";
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                key = KEY_MAP.get(keyCode);
                break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DEL:
                if (helper.isNoBlocking()) {
                    return;
                }
                key = KEY_MAP.get(keyCode);
                break;
            case KeyEvent.KEYCODE_BACK:
                if (helper.isNoBlocking()) {
                    return;
                }
                key = KEY_MAP.get(keyCode);
                onKeyAbort(key);
                break;
            default:
                break;
        }

        setBtnNum(key);
    }

    private void onKeyAbort(String key) {
        if (!helper.getButtonKeyList().isEmpty()) {
            for (int i = 0; i < helper.getButtonKeyList().size(); i++) {
                if (key.equals(helper.getButtonKeyList().get(i)) && TextUtils.isEmpty(helper.getBtnPropertyList().get(i).getKey())) {
                    helper.sendAbort();
                    return;
                }
            }
        } else {
            helper.sendAbort();
        }
    }

    private void setBtnNum(String key) {
        ArrayList<String> btnKeyList = helper.getButtonKeyList();
        if (btnKeyList != null && !btnKeyList.isEmpty()) {
            int index;
            if ((index = btnKeyList.indexOf(key)) >= 0) {
                btnNum = String.valueOf(index + 1);
                helper.sendBtnNum(btnNum);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.stop();
    }

    private Bitmap createQRCodeBitmap(String content, int width, int height,
                                      @ColorInt int colorBlack, @ColorInt int colorWhite) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }

        if (width < 0 || height < 0) {
            return null;
        }

        try {
            Map<EncodeHintType, String> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, "H");
            hints.put(EncodeHintType.MARGIN, "2");
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = colorBlack;
                    } else {
                        pixels[y * width + x] = colorWhite;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }
}
