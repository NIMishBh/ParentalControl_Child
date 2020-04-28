package com.example.parentalcontrol_child;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {
    TextView t;
    public static final int PERMISSIONS_REQUEST=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        t = findViewById(R.id.textView);
        LocationManager lm=(LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            finish();
        }
        int permission= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode==PERMISSIONS_REQUEST) && (grantResults.length==1) && (grantResults[0]==PackageManager.PERMISSION_GRANTED)){
            startTrackerService();
        }
        else
        {
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTrackerService() {

        startService(new Intent(this,TrackingService.class));
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();
        finish();
    }
}
