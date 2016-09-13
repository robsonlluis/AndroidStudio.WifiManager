package com.app.rsouza.wifimanager;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    Button btnRefresh;
    Button btnDisconnect;
    Button btnDisableNetwork;
    Button btnEnableNetwork;
    Button btnConnect;
    Button btnRemoveNetwork;
    TextView tvSSID;
    TextView tvSinalLevel;
    TextView tvSpeed;
    TextView tvFrequence;
    TextView tvSecurity;
    TextView tvNetworkId;

    private static final int SECURITY_PSK = 1;
    private static final int SECURITY_EAP = 2;
    private static final int SECURITY_WEP = 3;
    private static final int SECURITY_NONE = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        btnDisableNetwork  = (Button) findViewById(R.id.btnDisableNetwork);
        btnEnableNetwork  = (Button) findViewById(R.id.btnEnableNetwork);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnRemoveNetwork = (Button) findViewById(R.id.btnRemoveNetwork);

        tvSSID = (TextView) findViewById(R.id.tvSSID);
        tvSinalLevel = (TextView) findViewById(R.id.tvSinalLevel);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvFrequence = (TextView) findViewById(R.id.tvFrequence);
        tvSecurity = (TextView) findViewById(R.id.tvSecurity);
        tvNetworkId = (TextView) findViewById(R.id.tvNetworkId);

        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo;
        final List<WifiConfiguration> configuredWifis = wifiManager.getConfiguredNetworks();
        wifiInfo = wifiManager.getConnectionInfo();

        btnDisconnect.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                wifiManager.disconnect();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo;
                wifiInfo = wifiManager.getConnectionInfo();

                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (!mWifi.isConnected()){
                    Toast.makeText(getApplication(), "Please enable your wifi first.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    tvSSID.setText(wifiInfo.getSSID());
                    tvSinalLevel.setText(String.valueOf(((WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 10) * 100) / 10)));
                    tvSpeed.setText(String.valueOf(wifiInfo.getLinkSpeed()));
                    tvFrequence.setText( String.valueOf(wifiInfo.getFrequency()));
                    tvNetworkId.setText( String.valueOf(wifiInfo.getNetworkId()));

                    for (final WifiConfiguration config : configuredWifis) {
                        if (config.status == WifiConfiguration.Status.CURRENT) {
                            switch (getSecurity(config)) {
                                case SECURITY_PSK:
                                    tvSecurity.setText("PSK");
                                    break;
                                case SECURITY_EAP:
                                    tvSecurity.setText("EAP");
                                    break;
                                case SECURITY_WEP:
                                    tvSecurity.setText("WEP");
                                    break;
                                case SECURITY_NONE:
                                    tvSecurity.setText("NONE");
                                    break;
                            }
                        }
                    }
                }

            }
        });

        btnDisableNetwork.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                wifiManager.disableNetwork(wifiInfo.getNetworkId());
            }
        });

        btnEnableNetwork.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                wifiManager.enableNetwork(wifiInfo.getNetworkId(), false);
            }
        });

        //Doesn't works.
        btnConnect.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplication(), "You can reconnect only networks created by own application.\nHere we don't have a create wifi sample.", Toast.LENGTH_LONG).show();
                wifiManager.reconnect();
            }
        });

        //You can remove only networks created by own application.
        //The code following doesn't works because try to remove the current wifi connection that won't created by own application.
        btnRemoveNetwork.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Toast.makeText(getApplication(), "You can remove only networks created by own application.\nHere we don't have a create wifi sample.", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplication(), "getNetworkId = "+wifiInfo.getNetworkId()+" ;removeNetwork = "+ wifiManager.removeNetwork(wifiInfo.getNetworkId()), Toast.LENGTH_SHORT).show();
                wifiManager.saveConfiguration();
            }
        });
    }

    static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) ||
                config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }
}
