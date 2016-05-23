package blf.com.camtrack.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import blf.com.camtrack.R;
import blf.com.camtrack.config.Config;
import blf.com.camtrack.config.Global;
import blf.com.camtrack.services.UploadService;


public class MainActivity extends ActionBarActivity {

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            CheckServiceStatus();
            //Log.d("receiver", "Got message: " + message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckServiceStatus();
        RegisterReceiver();
    }

    private void RegisterReceiver()
    {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("upload-service-change"));
    }

    @Override
    public void onDestroy()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this,SettingsActivity.class);
            startActivity(settings);
        }

        return super.onOptionsItemSelected(item);
    }

    private void CheckServiceStatus()
    {
//        boolean running = false;
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(UploadService.service.getClassName())) {
//                running = true;
//            }
//        }
        TextView uploadServiceStatus = (TextView)findViewById(R.id.uploadServiceStatus);
        Button runButton = (Button)findViewById(R.id.buttonRunUploadService);
        if(((Global)this.getApplication()).GetUploadServiceStatus()){
            uploadServiceStatus.setText("RUNNING");
            uploadServiceStatus.setTextColor(getResources().getColor(R.color.runningstatus));
            runButton.setText("STOP");
        }
        else
        {
            uploadServiceStatus.setText("IDLE");
            uploadServiceStatus.setTextColor(getResources().getColor(R.color.idlestatus));
            runButton.setText("RUN");
        }
    }

    public void ToggleCamtrackService(View view) {
        Intent camtrackService = new Intent(this, UploadService.class);
        camtrackService.putExtra(UploadService.LOG_OPTIONS, new String[]{"GPS"});
        camtrackService.putExtra(LoginActivity.SESSION_ID,getIntent().getStringExtra(LoginActivity.SESSION_ID));
        this.startService(camtrackService);
        //CheckServiceStatus();
    }
}
