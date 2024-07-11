package com.pax.tlmanager.cusui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pax.customui.constant.status.ClssLightStatus;
import com.pax.customui.constant.status.SecurityStatus;
import com.pax.customui.core.entity.SecurityExtra;
import com.pax.customui.core.helper.SearCardHelper;
import com.pax.tlmanager.R;
import com.pax.tlmanager.cusui.base.BaseActivity;
import com.pax.tlmanager.cusui.base.RespStatusImpl;
import com.pax.tlmanager.cusui.event.ClssLightEvent;
import com.pax.tlmanager.cusui.event.EventBusUtil;
import com.pax.tlmanager.cusui.event.PINEvent;
import com.pax.tlmanager.cusui.widget.ClssLight;
import com.pax.tlmanager.cusui.widget.ClssLightsView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SearchCardActivity extends BaseActivity implements View.OnClickListener, SearCardHelper.ISearchCardListener {

    TextView promptTv;
    EditText cardNumEdt;
    ClssLightsView tvClssLight;
    TextView amountTv;
    Button confirmBtn;
    LinearLayout amountLayout;
    TextView totalView;
    TextView tvSwipe;
    TextView tvInsert;
    TextView tvTap;

    private SearCardHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_card);
        EventBusUtil.register(this);

        promptTv = findViewById(R.id.prompt_tv);
        cardNumEdt = findViewById(R.id.card_number_edit);
        tvClssLight = findViewById(R.id.clssLight);
        amountTv = findViewById(R.id.amount_prompt_tv);
        confirmBtn = findViewById(R.id.confirm_btn);
        amountLayout = findViewById(R.id.amount_prompt_layout);
        totalView = findViewById(R.id.total_text);
        tvSwipe = findViewById(R.id.tv_swipe);
        tvInsert = findViewById(R.id.tv_insert);
        tvTap = findViewById(R.id.tv_tap);
        confirmBtn.setOnClickListener(this);

        tvClssLight.setVisibility(View.INVISIBLE);

        promptTv.setText("Please Input Pan");
        helper = new SearCardHelper(this, new RespStatusImpl(this));
        helper.start(this, getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        helper.start(this, intent);
    }

    @Override
    public void onClick(View view) {
        helper.sendNext();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            helper.sendAbort();
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            helper.sendNext();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        EventBusUtil.unregister(this);
        helper.stop();
        super.onDestroy();
    }

    @Override
    public void onShowCard(boolean enableManualEntry, boolean enableSwipe, boolean enableInsert, boolean enableTap) {
        Log.d("SearchCard", "onShowCard: " + enableManualEntry + " " + enableSwipe + " " + enableInsert + " " + enableTap);
        // Enable Input Card Number by Manual
        if (enableManualEntry) {
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setEnabled(true);
        } else {
            promptTv.setVisibility(View.INVISIBLE);
            confirmBtn.setVisibility(View.INVISIBLE);
        }

        // Enable Swipe Card
        tvSwipe.setEnabled(enableSwipe);
        if (enableSwipe) {
            tvSwipe.setVisibility(View.VISIBLE);
        } else {
            tvSwipe.setVisibility(View.INVISIBLE);
        }


        // Enable Insert Card
        tvInsert.setEnabled(enableInsert);
        if (enableInsert)
            tvInsert.setVisibility(View.VISIBLE);
        else {
            tvInsert.setVisibility(View.INVISIBLE);
        }

        // Enable Tap Card
        if (enableTap) {
            tvTap.setVisibility(View.VISIBLE);
            tvClssLight.setVisibility(View.VISIBLE);
        } else {
            tvClssLight.setVisibility(View.GONE);
            tvTap.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewTreeObserver observer = cardNumEdt.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cardNumEdt.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int[] location = new int[2];
                cardNumEdt.getLocationInWindow(location);
                int x = location[0];
                int y = location[1];

                SecurityExtra extra = new SecurityExtra();
                extra.setImgHeight(48);
                extra.setImgWidth(48);
                try (InputStream in = getAssets().open("card.png")) {
                    byte[] bytes = new byte[in.available()];
                    in.read(bytes);
                    //The leftImage should be exchange into String with ASCII format.
                    extra.setLeftImg(new String(bytes, StandardCharsets.ISO_8859_1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                extra.setFocus(true);
                //The color format is the same as Color.parseColor().
                extra.setStrokeColor("red");
                extra.setStrokeWidth(2);
                extra.setPaddingLeft(10);
                extra.setPaddingRight(10);
                helper.setSecurityArea(x, y, cardNumEdt.getWidth(), 120,
                        "Enter Account", 20, extra);
            }
        });
        cardNumEdt.setVisibility(View.INVISIBLE);
    }

    /*
    The contactless light action receive through ClssStatusReceiver,
     and use Eventbus to pass related events to Activity.
    */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAsync(ClssLightEvent event) {
        String action = event.getStatus();
        if (action == null || action.equals(""))
            return;
        Log.i("StatusReceiver", "EVENTBUS action :" + action);
        switch (action) {
            case ClssLightStatus.CLSS_LIGHT_NOT_READY:
                tvClssLight.setLights(-1, ClssLight.OFF);
                break;
            case ClssLightStatus.CLSS_LIGHT_COMPLETED:
                tvClssLight.setLights(2, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_ERROR:
                tvClssLight.setLights(3, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_IDLE:
                tvClssLight.setLights(0, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_PROCESSING:
                tvClssLight.setLights(1, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_READY_FOR_TXN:
                tvClssLight.setLights(0, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_REMOVE_CARD:
                tvClssLight.setLights(2, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_BLUE_ON:
                tvClssLight.setLight(0, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_BLUE_OFF:
                tvClssLight.setLight(0, ClssLight.OFF);
                break;
            case ClssLightStatus.CLSS_LIGHT_BLUE_BLINK:
                tvClssLight.setLight(0, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_YELLOW_ON:
                tvClssLight.setLight(1, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_YELLOW_OFF:
                tvClssLight.setLight(1, ClssLight.OFF);
                break;
            case ClssLightStatus.CLSS_LIGHT_YELLOW_BLINK:
                tvClssLight.setLight(1, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_GREEN_ON:
                tvClssLight.setLight(2, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_GREEN_OFF:
                tvClssLight.setLight(2, ClssLight.OFF);
                break;
            case ClssLightStatus.CLSS_LIGHT_GREEN_BLINK:
                tvClssLight.setLight(2, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_RED_ON:
                tvClssLight.setLight(3, ClssLight.ON);
                break;
            case ClssLightStatus.CLSS_LIGHT_RED_OFF:
                tvClssLight.setLight(3, ClssLight.OFF);
                break;
            case ClssLightStatus.CLSS_LIGHT_RED_BLINK:
                tvClssLight.setLight(3, ClssLight.ON);
                break;
            default:
                break;
        }
    }


    @Override
    public void onShowAmount(long amount) {
        if (amount != 0) {
            amountLayout.setVisibility(View.VISIBLE);
            amountTv.setText(String.valueOf(amount));
        } else {
            amountLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onShowCurrency(@Nullable String currency, @Nullable String currencySymbol, boolean isPoint) {
        //Display the currency in your TextView.
        Log.d("InputAccount", "onShowCurrency: " + currencySymbol);
    }

    @Override
    public void onShowMessage(@Nullable String transName, @Nullable String message) {
        //Display the message in your TextView.
        Log.d("InputAccount", "onShowMessage: " + transName+","+message);
    }

    /**
     * @param event When show inputArea, the input state will be send by PINEvent.
     *              See {@link com.pax.tlmanager.cusui.receiver.PinStatusReceiver}
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAsync(PINEvent event) {
        String action = event.getStatus();
        switch (action) {
            case SecurityStatus.SECURITY_ENTERING:
                Log.i("OnEvent", "SECURITY_ENTERING");
                break;
            case SecurityStatus.SECURITY_DELETE:
                Log.i("OnEvent", "SECURITY_DELETE");
                break;
            case SecurityStatus.SECURITY_FORMAT_ERROR:
                Log.i("OnEvent", "SECURITY_FORMAT_ERROR");
                break;
            case SecurityStatus.SECURITY_LUHN_CHECK_OK:
                Log.i("OnEvent", "SECURITY_LUHN_CHECK_OK");
                break;
            case SecurityStatus.SECURITY_LUHN_CHECK_ERROR:
                Log.i("OnEvent", "SECURITY_LUHN_CHECK_ERROR");
                break;
            default:
                break;
        }
    }
}
