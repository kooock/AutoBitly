package koock.koock.autobitly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Switch serviceSwitch;
    EditText accessTokenInput;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceSwitch = (Switch)findViewById(R.id.service_switch);
        accessTokenInput = (EditText)findViewById(R.id.access_token_input);

        sharedPref = SharedPref.getInstance(getApplicationContext());
        accessTokenInput.setText(sharedPref.getToken());

        final Intent intent = new Intent(this,ClipboardService.class);

        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if (ClipboardService.class.getName().equals(service.service.getClassName()) && (!b)) {
                        stopService(intent);
                        Toast.makeText(getBaseContext(),"auto bitly 중지", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(b) {
                    sharedPref.saveToken(accessTokenInput.getText().toString());
                    startService(intent);
                    Toast.makeText(getBaseContext(), "auto bitly 실행", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
