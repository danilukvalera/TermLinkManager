package com.pax.tlmanager.keyboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.pax.devicemanager.PaxDeviceUtils;
import com.pax.tlmanager.R;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoftKeyboardSimpleStyle extends KeyboardView {
    public SoftKeyBoard mKeyBoard = null;
    private Context mContext;
    private int mHeightContainerView = 0;
    private int mWidthContainerView = 0;
    private TextPaint numberPaint, textPaint;
    private int vSpecLength, hSpecLength;
    private List<Keyboard.Key> keys;
    private Map<Integer, Drawable> tempKeyDrawable = new HashMap<>();


    public SoftKeyboardSimpleStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initialize();
    }

    public SoftKeyboardSimpleStyle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initialize();
    }

    public static int dip2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5);
    }

    public static boolean isNum(String str) {
        return !TextUtils.isEmpty(str) && str.matches("^[0-9]*$");
    }

    public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    private void initialize() {
        mKeyBoard = new SoftKeyBoard(mContext, getKeyBoardResource(), 0, mWidthContainerView, mHeightContainerView);
        vSpecLength = dip2px(mContext, 3);
        hSpecLength = dip2px(mContext, 3);
        numberPaint = new TextPaint();
        numberPaint.setColor(Color.BLACK);
        numberPaint.setAntiAlias(true);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        numberPaint.setTextSize(dip2px(mContext, 32));
        numberPaint.setStrokeWidth(5);

        textPaint = new TextPaint();
        textPaint.setColor(Color.GRAY);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(dip2px(mContext, 20));
        textPaint.setStrokeWidth(3);
        initKeyBoard();
    }

    private void initKeyBoard() {
        Log.d("test", "initKeyBoard: ");
        tempKeyDrawable.clear();
        mKeyBoard = new SoftKeyBoard(mContext, getKeyBoardResource(), 0, mWidthContainerView - 3 * hSpecLength,
                mHeightContainerView - 4 * vSpecLength);
        keys = mKeyBoard.getKeys();
        setKeyboard(mKeyBoard);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (PaxDeviceUtils.isLowResolutionLandDevice()) {
            setMeasuredDimension(getDefaultSize(widthMeasureSpec, widthMeasureSpec)
                    , getMeasureSize(100, heightMeasureSpec));
        } else {
            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                    getMeasureSize(240, heightMeasureSpec));
        }
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = getMeasureSize(240, heightMeasureSpec);
        mHeightContainerView = h - getTop() - getBottom();
        mWidthContainerView = w - getRight() - getLeft();
        if (mWidthContainerView > 0 && mHeightContainerView > 0) {
            initKeyBoard();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (keys != null) {
            for (Keyboard.Key key : keys) {
                drawSimpleNumSpecialKey(key, canvas);
            }
        }
    }

    private void drawSimpleNumSpecialKey(Keyboard.Key key, Canvas canvas) {
        int backgroundId = PaxDeviceUtils.isLowResolutionLandDevice() ? R.drawable.pax_selection_button_number_small : R.drawable.pax_selection_button_number;
        int iconId = 0;//删除或隐藏键盘的图标id

        switch (key.codes[0]) {
            case -5:
                backgroundId = PaxDeviceUtils.isLowResolutionLandDevice() ? R.drawable.pax_selection_button_clear_small : R.drawable.pax_selection_button_clear;
                iconId = R.drawable.ic_back;
                break;
            case -3:
                backgroundId = R.drawable.pax_selection_button_special;
                iconId = R.mipmap.pax_hideboard;
                break;
            case 57418:
                backgroundId = PaxDeviceUtils.isLowResolutionLandDevice() ? R.drawable.pax_selection_button_cancel_small : R.drawable.pax_selection_button_cancel;
                iconId = R.drawable.ic_close;
                break;
            case 57420:
            case 57422:
                backgroundId = PaxDeviceUtils.isLowResolutionLandDevice() ? R.drawable.pax_selection_button_confirm_small : R.drawable.pax_selection_button_confirm;
                iconId = R.drawable.ic_enter;
                break;
            default:
                break;
        }
        String keyStr = "";
        if (key.label != null)
            keyStr = key.label.toString();

        drawKeyBackground(backgroundId, keyStr, canvas, key, iconId);
    }

    private void drawKeyBackground(int drawableId, String keyValue, Canvas canvas, Keyboard.Key key, int iconId) {
        key.label = keyValue;
        Drawable backgroundDrawable = getDrawableFromRes(drawableId);
        int[] drawableState = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            backgroundDrawable.setState(drawableState);
        }
        backgroundDrawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        backgroundDrawable.draw(canvas);
        if (key.icon != null) {
            Drawable icon = key.icon;
            icon.setBounds(key.x + key.width / 4, key.y + key.height / 4, (int) (key.x + key.width - key.width / 4), (int) (key.y + key.height - key.height / 4));
            icon.draw(canvas);
        }
        //draw text
        Paint.FontMetrics fontMetrics = numberPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        float textBaseY = key.y + key.height - (key.height - fontHeight) / 2 - fontMetrics.bottom;
        if (isNum(keyValue) && getKeyboard() == mKeyBoard) {
            canvas.drawText(keyValue, key.x + key.width / 2, textBaseY, numberPaint);
        } else {
            //如果是确认按钮 切换字体颜色
            if (drawableId == R.drawable.pax_selection_button_confirm || drawableId == R.drawable.pax_selection_button_confirm_small) {
                textPaint.setColor(Color.WHITE);
            } else {
                textPaint.setColor(Color.DKGRAY);
            }
            //如果无图标
            if (iconId == 0) {
                fontMetrics = textPaint.getFontMetrics();
                fontHeight = fontMetrics.bottom - fontMetrics.top;
                textBaseY = key.y + key.height - (key.height - fontHeight) / 2 - fontMetrics.bottom;
                canvas.drawText(keyValue, key.x + key.width / 2, textBaseY, textPaint);
            } else {
                Bitmap m = getBitmapFromDrawable(getContext(), iconId);
                if (m.getHeight() > key.height || m.getWidth() > key.width) {
                    BigDecimal mWidth = new BigDecimal(m.getWidth());
                    BigDecimal keyWidth = new BigDecimal(key.width);
                    BigDecimal mHeight = new BigDecimal(m.getHeight());
                    BigDecimal keyHeight = new BigDecimal(key.height);
                    float scale = Math.min(keyWidth.divide(mWidth, 2, BigDecimal.ROUND_DOWN).floatValue(),
                            keyHeight.divide(mHeight, 2, BigDecimal.ROUND_DOWN).floatValue());
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);
                    m = Bitmap.createBitmap(m, 0, 0, m.getWidth(),
                            m.getHeight(), matrix, true);
                }

                canvas.drawBitmap(m, key.x + key.width / 2 - m.getWidth() / 2, key.y + (key.height - m.getHeight()) / 2, textPaint);
            }
        }
    }

    private Drawable getDrawableFromRes(int resId) {
        if (tempKeyDrawable.containsKey(resId)) {
            return tempKeyDrawable.get(resId);
        }
        Drawable drawable = getContext().getResources().getDrawable(resId);
        tempKeyDrawable.put(resId, drawable);
        return drawable;
    }

    private int getKeyBoardResource() {
        if (PaxDeviceUtils.isLowResolutionLandDevice()) {
            return R.xml.pax_keyboard_q_series;
        } else {
            return R.xml.pax_keyboard;
        }
    }

    private int getMeasureSize(int dpSize, int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int pxSize = dip2px(getContext(), dpSize);
        int parentSeize = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                return pxSize;
            case MeasureSpec.AT_MOST:
                return Math.min(parentSeize, pxSize);
            case MeasureSpec.EXACTLY:
                return parentSeize;
        }
        return parentSeize;
    }
}
