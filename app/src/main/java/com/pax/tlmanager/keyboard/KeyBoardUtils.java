package com.pax.tlmanager.keyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.pax.devicemanager.PaxDeviceUtils;
import com.pax.tlmanager.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.content.Context.AUDIO_SERVICE;

public class KeyBoardUtils {
    private SoftKeyboardSimpleStyle keyboardView;
    public boolean isShow = false;// Whether the keyboard is being displayed
    InputFinishListener inputOver;
    KeyBoardStateChangeListener keyBoardStateChangeListener;
    private int ID_container = R.id.pax_fl_container, ID_keyboard = R.id.pax_keyboard_view;
    /**
     * KEYBOARD_SHOW,KEYBOARD_HIDE {@link KeyBoardStateChangeListener}
     */
    public static final int KEYBOARD_SHOW = 1;
    public static final int KEYBOARD_HIDE = 2;

    private EditText ed;// The editText that currently gets the focus
    private EditText[] mCustomEditTexts;// An editText array that requires a secure soft keyboard
    private boolean[] isGetFocus;// mCustomEditTexts [] gets the focus array
    private Handler showHandler;

    private Context mContext;
    private Activity mActivity;
    private View inflaterView;// infaterView in dialog
    private RelativeLayout mContainerView;// The relative container that contains the keyboard
    private KeyBroadTouchListener touchListener;

    private boolean isFloat;

    /**
     * Run this constructor after CustomEditText findVieById
     */
    public KeyBoardUtils(Context context, EditText[] editteText) {
        this.mContext = context;
        this.mCustomEditTexts = editteText;
        this.mActivity = (Activity) mContext;
        initKeyBoardView();
    }

    /**
     * @param ctx           used for findId
     * @param idOfContainer id of Container
     * @param idOfKeyboard  id of Keyboard
     */
    public KeyBoardUtils(Context ctx, EditText[] editTexts, int idOfContainer, int idOfKeyboard) {
        this.mContext = ctx;
        this.mCustomEditTexts = editTexts;
        this.mActivity = (Activity) mContext;
        this.ID_container = idOfContainer;
        this.ID_keyboard = idOfKeyboard;
        initKeyBoardView();
    }

    /**
     * Popbox class, use this
     *
     * @param view nflaterView for popbox.
     */
    public KeyBoardUtils(Context ctx, EditText[] editTexts, View view) {
        this.mCustomEditTexts = editTexts;
        this.mContext = ctx;
        if (mContext instanceof Activity) this.mActivity = (Activity) mContext;
        this.inflaterView = view;
        initKeyBoardView();
    }

    public KeyBoardUtils(Context ctx, EditText[] editTexts, View view, boolean isFloat) {
        this.mCustomEditTexts = editTexts;
        this.mContext = ctx;
        if (mContext instanceof Activity) this.mActivity = (Activity) mContext;
        this.inflaterView = view;
        this.isFloat = isFloat;
        initKeyBoardView();
    }

    private void initKeyBoardView() {
        isGetFocus = new boolean[mCustomEditTexts.length];
        for (int i = 0; i < mCustomEditTexts.length; i++) {
            isGetFocus[i] = false;
            mCustomEditTexts[i].setTag(i);
            setKeyBoardCursorNew(mCustomEditTexts[i]);// 防止系统键盘的弹出
        }

        if (inflaterView != null) {
            mContainerView = (inflaterView.findViewById(ID_container));
        } else {
            if (mContainerView == null) {
                mContainerView = mActivity.findViewById(ID_container);
            }
        }
        ed = mCustomEditTexts[0];
        setListener();// 给mCustomEditText[]设置监听
    }

    private void setListener() {
        for (EditText customEditText : mCustomEditTexts) {
            customEditText.setOnFocusChangeListener(mOnFocusChangeListener);
            customEditText.setOnTouchListener(mOnTouchListener);
        }
    }

    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (isFloat)
                return;
            isGetFocus[(Integer) v.getTag()] = hasFocus;
            if (!hasFocus) {
                showHandler = new Handler();
                showHandler.postDelayed(() -> checkFocus(), 40);
            }
        }
    };
    @SuppressLint("ClickableViewAccessibility")
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP && !isShow && !isFloat) {
                showKeyboardLayout();
            }
            return false;
        }
    };

    /**
     * 处理当界面还有非customedittext的情况下 点击这个普通的eidttext或者其他view获取焦点的时候 隐藏软键盘
     */
    private void checkFocus() {
        for (int i = 0; i < isGetFocus.length; i++) {
            if (isGetFocus[i]) {
                ed = mCustomEditTexts[i];
                return;
            }
        }
        hideKeyboardLayout(true);
    }

    /**
     * 调用super.onBackPress()时判断键盘是否需要隐藏调用此方法，防止当类型是D800_PIN时，按返回键不能直接返回
     *
     * @return 键盘是否显示，是D800_PIN的话，返回false。
     */
    public boolean isShow() {
        return isShow;
    }

    /***
     *
     *
     * @param editText
     * @return 判断输入法是否打开
     */
    private boolean setKeyBoardCursorNew(EditText editText) {
        boolean flag = false;
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();// is若返回true，则表示输入法打开
        Log.d("testKeyBoard", "SysKeyBoard isOpen:" + isOpen);
        if (isOpen) {
            flag = imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        int currentVersion = Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setShowSoftInputOnFocus(false);
        } else {
            if (currentVersion >= Build.VERSION_CODES.JELLY_BEAN) {
                // 4.2
                methodName = "setShowSoftInputOnFocus";
            } else if (currentVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                // 4.0
                methodName = "setSoftInputShownOnFocus";
            }

            if (methodName == null) {
                editText.setInputType(InputType.TYPE_NULL);
            } else {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                try {
                    setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                    setShowSoftInputOnFocus.setAccessible(true);
                    setShowSoftInputOnFocus.invoke(editText, false);
                } catch (NoSuchMethodException e) {
                    editText.setInputType(InputType.TYPE_NULL);
                    e.printStackTrace();
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        if (mActivity != null)
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return flag;
    }

    /**
     * How to display a soft keyboard
     *
     * @param forced Mandatory display of soft keyboards
     */
    public void showKeyboardLayout(boolean forced) {
        if (PaxDeviceUtils.hasPhysicalKeyboard() && !forced) {
            return;
        }

        if (setKeyBoardCursorNew(ed)) {
            showHandler = new Handler();
            showHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    show();
                }
            }, 400);
        } else {
            // 直接显示
            show();
        }
    }

    public void showKeyboardLayout() {
        showKeyboardLayout(false);
    }

    private void show() {
        if (mContainerView != null) {
            mContainerView.setVisibility(View.VISIBLE);
        }
        if (keyboardView != null) {
            keyboardView.setVisibility(View.GONE);
        }
        initKeyBoard(ID_keyboard);
        keyboardView.setPreviewEnabled(false);
        isShow = true;
        if (keyBoardStateChangeListener != null)
            keyBoardStateChangeListener.KeyBoardStateChange(KEYBOARD_SHOW, ed);
    }

    private void initKeyBoard(int keyBoardViewID) {
        if (inflaterView != null) {
            keyboardView = inflaterView.findViewById(keyBoardViewID);
        } else {
            mActivity = (Activity) mContext;
            keyboardView = mActivity.findViewById(keyBoardViewID);
        }
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
        keyboardView.setOnKeyboardActionListener(listener);
        keyboardView.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);
    }

    /**
     * 关闭软键盘方法
     */
    public void hideKeyboardLayout() {
        hideKeyboardLayout(false);
    }

    /**
     * 强制隐藏软键盘的方法
     */
    private void hideKeyboardLayout(boolean isForceClose) {
        isShow = false;
        if (keyboardView != null)
            if (keyboardView.getVisibility() == View.VISIBLE)
                keyboardView.setVisibility(View.INVISIBLE);
        if (mContainerView != null)
            mContainerView.setVisibility(View.GONE);
        if (keyBoardStateChangeListener != null)
            keyBoardStateChangeListener.KeyBoardStateChange(KEYBOARD_HIDE, ed);
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
            if (ed == null)
                return;
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            String temp = editable.subSequence(0, start) + text.toString()
                    + editable.subSequence(start, editable.length());
            ed.setText(temp);
            Editable etext = ed.getText();
            Selection.setSelection(etext, start + 1);
        }

        @Override
        public void onRelease(int primaryCode) {
            // Device.beepPrompt();
            callTouchRelease(primaryCode);
            playClick(primaryCode);
        }

        @Override
        public void onPress(int primaryCode) {
            callTouchPress(primaryCode);
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 取消
                hideKeyboardLayout();
                if (inputOver != null)
                    inputOver.inputHasOver(primaryCode, ed);
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 删除
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == 57419) {// 000
                editable.insert(start, "000");
            } else if (primaryCode == 57418) {// back
                Log.d("testKeyboard", "onKey: Back");
                sendKeyDownUpSyncInOtherThread(KeyEvent.KEYCODE_BACK);
            } else if (primaryCode == 57420) {// confirm
                Log.d("testKeyboard", "onKey: Confirm");
                sendKeyDownUpSyncInOtherThread(KeyEvent.KEYCODE_ENTER);
            } else if (primaryCode == 57421) {// 00
                editable.append("00");
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }
    };

    private void sendKeyDownUpSyncInOtherThread(final int key) {
        new Thread(() -> sendKeyDownUpSync(key)).start();
    }

    private void sendKeyDownUpSync(final int key) {
        try {
            new Instrumentation().sendKeyDownUpSync(key);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface InputFinishListener {
        void inputHasOver(int onclickType, EditText editText);
    }

    // 设置监听事件
    public void setInputOverListener(InputFinishListener listener) {
        this.inputOver = listener;
    }

    public interface KeyBoardStateChangeListener {
        void KeyBoardStateChange(int state, EditText editText);
    }

    public void setKeyBoardStateChangeListener(KeyBoardStateChangeListener listener) {
        this.keyBoardStateChangeListener = listener;
    }

    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case Keyboard.KEYCODE_CANCEL:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                break;
        }
    }

    public void setTouchListener(KeyBroadTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public interface KeyBroadTouchListener {
        void onPress(int keyCode);

        void onRelease(int keyCode);
    }

    private void callTouchPress(int keyCode) {
        if (touchListener != null) {
            touchListener.onPress(keyCode);
        }
    }

    private void callTouchRelease(int keyCode) {
        if (touchListener != null) {
            touchListener.onRelease(keyCode);
        }
    }
}
