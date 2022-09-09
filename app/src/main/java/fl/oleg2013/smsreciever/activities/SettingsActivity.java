package fl.oleg2013.smsreciever.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import fl.oleg2013.smsreciever.R;
import fl.oleg2013.smsreciever.utils.Utils;

public class SettingsActivity extends AppCompatActivity {

    private Button apply_button;
    private EditText url_text;

    public String current_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        url_text = findViewById(R.id.url_text);
        apply_button = findViewById(R.id.apply_button);
        apply_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveUrl();
            }
        });

        checkData();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveUrl() {
        if (TextUtils.isEmpty(url_text.getText().toString())) {
            Toast.makeText(SettingsActivity.this, "Пожалуйста, введите ссылку", Toast.LENGTH_SHORT).show();
            return;
        }
        current_url = url_text.getText().toString();
        String json = "{\"link\":\"" + current_url + "\"}";
        Utils.FileManager.SaveJson(json, getString(R.string.settingsFileName), getApplicationContext());
        Toast.makeText(SettingsActivity.this, "Новые данные сохранены :)", Toast.LENGTH_SHORT).show();
    }

    private void checkData() {
        if (Utils.FileManager.FileExist(
                this.getString(R.string.settingsFileName), getApplicationContext())) {
            setTextView();
            return;
        }
        url_text.setText(getString(R.string.default_url));
        saveUrl();
    }

    private void setTextView() {
        try{
            JSONObject json = Utils.FileManager.GetJson(
                    this.getString(R.string.settingsFileName), getApplicationContext());
            url_text.setText(json.getString("link"));
        }
        catch (Exception e){
            Log.e(getString(R.string.app_name), e.getMessage());
            e.printStackTrace();
        }
    }
}
