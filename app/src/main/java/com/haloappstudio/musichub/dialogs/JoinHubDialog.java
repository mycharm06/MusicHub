package com.haloappstudio.musichub.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haloappstudio.musichub.R;

import java.util.List;

/**
 * Created by suheb on 26/8/14.
 */
public class JoinHubDialog extends DialogFragment {

    private WifiManager mWifiManager;
    private WifiConfiguration mWifiConf;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Start Wifi scan
        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        mWifiManager.startScan();
        final List<ScanResult> scanResultList = mWifiManager.getScanResults();
        WifiListAdapter wifiListAdapter = new WifiListAdapter(getActivity(), R.layout.joinhub_dialog, scanResultList);
        mWifiConf = new WifiConfiguration();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Hub")
                .setAdapter(wifiListAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mWifiConf.SSID = "\"" + scanResultList.get(i).SSID + "\"";
                        mWifiConf.status = WifiConfiguration.Status.ENABLED;
                        mWifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        int id  = mWifiManager.addNetwork(mWifiConf);
                        mWifiManager.enableNetwork(id, true);
                    }
                });

        return builder.create();
    }

    public class WifiListAdapter extends ArrayAdapter<ScanResult> {
        private Context mContext;
        private List<ScanResult> mList;
        private int mResourceId;

        public WifiListAdapter(Context context, int resource, List<ScanResult> list) {
            super(context, resource, list);
            this.mList = list;
            this.mContext = context;
            this.mResourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView == null){
                convertView = inflater.inflate(mResourceId, parent, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.textView);
            textView.setText(mList.get(position).SSID);
            return convertView;
        }
    }
}
