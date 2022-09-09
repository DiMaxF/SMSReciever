package fl.oleg2013.smsreciever.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import fl.oleg2013.smsreciever.NetworkChangeReceiver;
import fl.oleg2013.smsreciever.R;
import fl.oleg2013.smsreciever.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private Button call_button;
    private Button settings_button;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.FOREGROUND_SERVICE,
            android.Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    final Context context = MainActivity.this;
    BroadcastReceiver receiver = new NetworkChangeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        if(!Utils.FileManager.FileExist(getString(R.string.settingsFileName), getApplicationContext())){
            String json = "{\"link\":\"" + getString(R.string.default_url) + "\"}";
            Utils.FileManager.SaveJson(json, getString(R.string.settingsFileName), getApplicationContext());
        }

        receiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();

        call_button = findViewById(R.id.call_button);
        call_button.setOnClickListener(view -> System.exit(0));

        settings_button = findViewById(R.id.settings_button);
        settings_button.setOnClickListener(view ->
                startActivity(new Intent(context, SettingsActivity.class)));
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

}