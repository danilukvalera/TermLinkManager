package com.pax.tlmanager.setting;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pax.poslink.peripheries.DeviceModel;
import com.pax.termlinkmanager.ConnectManager;
import com.pax.tlmanager.R;

import java.util.List;

public class SettingAboutFragment extends Fragment {
    private View view;
    private TextView tvVersion;
    private TextView tvTermLinkVer;
    private TextView tvAndroidVer;
    private TextView tvDeviceSN;
    private TextView tvServiceName;

    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = "";
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting_about, null);
        tvVersion = view.findViewById(R.id.tv_curr_version);
        tvAndroidVer = view.findViewById(R.id.tv_android_version);
        tvTermLinkVer = view.findViewById(R.id.tv_termlink_version);
        tvDeviceSN = view.findViewById(R.id.tv_device_sn);
        tvServiceName = view.findViewById(R.id.tv_termlink_name);

        tvVersion.setText(packageName(getContext()));
        tvTermLinkVer.setText("checking...");
        tvServiceName.setText("checking...");
        tvDeviceSN.setText(DeviceModel.getSN(getContext()) + "");
        tvAndroidVer.setText(Build.VERSION.RELEASE);
        new Thread(() -> getPackageVersion(getContext())).start();
        return view;
    }

    private void getPackageVersion(Context context) {
        final String packageName = ConnectManager.getInstance(context).getTargetPackageName();
        if (!packageName.startsWith("com.pax")) {
            view.post(() -> tvTermLinkVer.setText(packageName));
            return;
        }
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (final PackageInfo info : packages) {
            if (info.packageName.equals(packageName)) {
                view.post(() -> {
                    tvTermLinkVer.setText(info.versionName);
                    tvServiceName.setText(info.packageName.substring(19).replace(".", " ").toUpperCase());
                });
                return;
            }
        }
        view.post(() -> tvTermLinkVer.setText("Get TermLink Version Error."));

    }
}
