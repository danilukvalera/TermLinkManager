package com.pax.tlmanager.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.EditTextPreferenceDialogFragmentCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.pax.devicemanager.Models;
import com.pax.tlmanager.App;
import com.pax.tlmanager.ParameterManager;
import com.pax.tlmanager.R;

public class SettingMainFragment extends PreferenceFragmentCompat {
    private SwitchPreferenceCompat checkUart;
    private SwitchPreferenceCompat checkEthernet;
    private SwitchPreferenceCompat checkBle;
    private SwitchPreferenceCompat checkUsb;
    private SwitchPreferenceCompat checkClientMode;
    private ListPreference baudRate;
    private ListPreference protocolType;
    private EditTextPreference etHostIp;
    private EditTextPreference etHostPort;
    private Preference exitPreference;
    private Preference idlePicSelect;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_main, rootKey);
        checkUart = findPreference(ParameterManager.KEY_COMM_UART);
        checkEthernet = findPreference(ParameterManager.KEY_COMM_ETHERNET);
        checkBle = findPreference(ParameterManager.KEY_COMM_BLE);
        checkUsb = findPreference(ParameterManager.KEY_COMM_USB);
        baudRate = findPreference(ParameterManager.KEY_COMM_RATE);
        protocolType = findPreference(ParameterManager.KEY_COMM_ETH_TYPE);
        etHostIp = findPreference(ParameterManager.KEY_COMM_HOST);
        etHostPort = findPreference(ParameterManager.KEY_COMM_PORT);
        checkClientMode = findPreference(ParameterManager.KEY_CLIENT_MODE);
        exitPreference = findPreference("preference_exit");
        idlePicSelect = findPreference("select_idle_pic");
        baudRate.setVisible(checkUart.isChecked() || checkUsb.isChecked());
        baudRate.setIcon(checkUart.isChecked() ? R.drawable.ic_baseline_settings_input_composite_24 : R.drawable.ic_baseline_usb_24);
        protocolType.setVisible(checkEthernet.isChecked());
        if (protocolType.getValue().contains("CLIENT")) {//Only the ZA region support the Client Mode.
            checkClientMode.setChecked(true);
            protocolType.setEntries(R.array.setting_comm_ethernet_client);
            protocolType.setEntryValues(R.array.setting_comm_ethernet_value_client);
        }
        etHostIp.setVisible(checkEthernet.isChecked() && checkClientMode.isChecked());
        checkClientMode.setVisible(checkEthernet.isChecked());
        etHostPort.setVisible(checkEthernet.isChecked());
        baudRate.setSummary(baudRate.getValue());
        protocolType.setSummary(protocolType.getValue());
        etHostIp.setSummary(etHostIp.getText());
        etHostPort.setSummary(etHostPort.getText());
        if (Models.Q10A.equals(Build.MODEL.toUpperCase())) {
            checkBle.setVisible(false);
        }
        listenSwitch();
        listenPreferences();
    }

    private void listenSwitch() {
        idlePicSelect.setOnPreferenceClickListener(preference -> {
            getActivity().startActivity(new Intent(getContext(),IdlePictureActivity.class));
            return true;
        });
        checkUart.setOnPreferenceChangeListener(new MySwitchListener() {
            @Override
            protected boolean onSwitchChange(boolean newValue) {
                baudRate.setVisible(newValue);
                if (newValue) {
                    checkEthernet.setChecked(false);
                    protocolType.setVisible(false);
                    etHostIp.setVisible(false);
                    etHostPort.setVisible(false);
                    checkClientMode.setVisible(false);
                    baudRate.setIcon(R.drawable.ic_baseline_settings_input_composite_24);
                    checkBle.setChecked(false);
                    checkUsb.setChecked(false);
                }
                return true;
            }
        });

        checkUsb.setOnPreferenceChangeListener(new MySwitchListener() {
            @Override
            protected boolean onSwitchChange(boolean newValue) {
                baudRate.setVisible(newValue);
                if (newValue) {
                    checkUart.setChecked(false);
                    checkEthernet.setChecked(false);
                    protocolType.setVisible(false);
                    checkClientMode.setVisible(false);
                    baudRate.setIcon(R.drawable.ic_baseline_usb_24);
                    etHostIp.setVisible(false);
                    etHostPort.setVisible(false);
                    checkBle.setChecked(false);
                }
                return true;
            }
        });

        checkEthernet.setOnPreferenceChangeListener(new MySwitchListener() {
            @Override
            protected boolean onSwitchChange(boolean newValue) {
                protocolType.setVisible(newValue);
                etHostIp.setVisible(newValue && protocolType.getValue().contains("CLIENT"));
                etHostPort.setVisible(newValue);
                checkClientMode.setVisible(newValue);
                if (newValue) {
                    checkUart.setChecked(false);
                    baudRate.setVisible(false);
                    checkBle.setChecked(false);
                    checkUsb.setChecked(false);
                }
                return true;
            }
        });

        checkBle.setOnPreferenceChangeListener(new MySwitchListener() {
            @Override
            protected boolean onSwitchChange(boolean newValue) {
                if (newValue) {
                    checkUart.setChecked(false);
                    baudRate.setVisible(false);
                    checkEthernet.setChecked(false);
                    protocolType.setVisible(false);
                    checkClientMode.setVisible(false);
                    etHostIp.setVisible(false);
                    etHostPort.setVisible(false);
                    checkUsb.setChecked(false);
                }
                return true;
            }
        });
        checkClientMode.setOnPreferenceChangeListener((preference, newValue) -> {
            if ((boolean) newValue) {
                etHostIp.setVisible(true);
                protocolType.setEntries(R.array.setting_comm_ethernet_client);
                protocolType.setEntryValues(R.array.setting_comm_ethernet_value_client);
                protocolType.setValue("TCP_CLIENT");
                protocolType.setSummary("TCP_CLIENT");
            } else {
                etHostIp.setVisible(false);
                protocolType.setEntries(R.array.setting_comm_ethernet);
                protocolType.setEntryValues(R.array.setting_comm_ethernet_value);
                protocolType.setValue("TCP");
                protocolType.setSummary("TCP");
            }
            return true;
        });
    }

    private void listenPreferences() {
        etHostPort.setOnBindEditTextListener(this::setEditTextConfig);
        etHostIp.setOnBindEditTextListener(this::setEditTextConfig);
        etHostPort.setOnPreferenceChangeListener((preference, newValue) -> {
            try {
                int port = Integer.parseInt((String) newValue);
                if (port < 0 || port > 65535) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            etHostPort.setSummary((String) newValue);
            return true;
        });

        etHostIp.setOnPreferenceChangeListener((preference, newValue) -> {
            etHostIp.setSummary((String) newValue);
            return true;
        });

        baudRate.setOnPreferenceChangeListener((preference, newValue) -> {
            baudRate.setSummary((String) newValue);
            return true;
        });

        protocolType.setOnPreferenceChangeListener((preference, newValue) -> {
            protocolType.setSummary((String) newValue);
            etHostIp.setVisible(((String) newValue).contains("CLIENT"));
            return true;
        });

        exitPreference.setOnPreferenceClickListener(preference -> {
            App.getApp().exitApp();
            return false;
        });
    }

    private void setEditTextConfig(EditText et) {
        et.setSingleLine(true);
        et.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dismissDialog();
                return true;
            }
            return false;
        });
        et.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                dismissDialog();
                return true;
            }
            return false;
        });
    }

    void dismissDialog() {
        for (Fragment fragments : getActivity().getSupportFragmentManager().getFragments()) {
            if (fragments instanceof EditTextPreferenceDialogFragmentCompat) {
                EditTextPreferenceDialogFragmentCompat dialog = ((EditTextPreferenceDialogFragmentCompat) fragments);
                dialog.onClick(dialog.getDialog(), DialogInterface.BUTTON_POSITIVE);
                dialog.dismiss();
                return;
            }
        }
    }


    abstract static class MySwitchListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof SwitchPreferenceCompat && ((SwitchPreferenceCompat) preference).isChecked()) {
                return false;
            }
            return onSwitchChange(newValue instanceof Boolean && (boolean) newValue);
        }

        protected abstract boolean onSwitchChange(boolean newValue);
    }
}
