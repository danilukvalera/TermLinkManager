package com.pax.tlmanager.cusui.base;

import android.view.Gravity;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.pax.customui.core.api.IUIListener;
import com.pax.tlmanager.App;

public class BaseFragment extends Fragment implements IUIListener {
    @Override
    public void showDetail(String msg) {
        Toast toast = Toast.makeText(App.getApp(), msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }
}
