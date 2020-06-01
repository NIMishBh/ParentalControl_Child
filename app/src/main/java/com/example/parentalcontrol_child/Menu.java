package com.example.parentalcontrol_child;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class Menu extends AppCompatActivity {

    Button loc;
    String mail,pass;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference parentRef = database.getReference("parents");
    FirebaseAuth mAuth;
    String call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();
        //call = "Yes";
        Intent intent = getIntent();

        mail = intent.getStringExtra("pemail");
        pass = intent.getStringExtra("ppass");

        if(!checkUsageStatsAllowedOrNot()){
            Intent usageAccessIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            usageAccessIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(usageAccessIntent);
            if(checkUsageStatsAllowedOrNot()){
                startService(new Intent(Menu.this,UsageStats.class));
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Please give The Required Access",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            startService(new Intent(Menu.this,UsageStats.class));
        }
        //Toast.makeText(getApplicationContext(),call,Toast.LENGTH_SHORT).show();

        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatabaseReference ref = parentRef.child(snapshot.getKey());
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            call = dataSnapshot.child("call").getValue().toString().trim();
                            callnoti(call);
                            Toast.makeText(getApplicationContext(),call,Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loc = findViewById(R.id.button);
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Menu.this, Location.class);
                i.putExtra("pemail", mail);
                i.putExtra("ppass", pass);
                startActivity(i);
                finish();
            }
        });
    }

   private void callnoti(String call) {

        if(call.equals("Yes")){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("Call","Call", NotificationManager.IMPORTANCE_HIGH);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }

            Intent i = new Intent(getApplicationContext(),remnoti.class);
            PendingIntent pi= PendingIntent.getActivity(Menu.this, 0,i,0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"Call")
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.callpar))
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_call)
                    .setContentIntent(pi);
            NotificationManager mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, builder.build());
        }

    }
   public boolean checkUsageStatsAllowedOrNot() {
       try {
           Toast.makeText(getApplicationContext(),"Inside cidsaon",Toast.LENGTH_SHORT).show();
           PackageManager packageManager = getPackageManager();
           ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
           AppOpsManager appOpsManager = (AppOpsManager) getSystemService(APP_OPS_SERVICE);
           int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
           return (mode == AppOpsManager.MODE_ALLOWED);
       } catch (Exception c) {
           Toast.makeText(getApplicationContext(), "Cannot get Usage Stats!", Toast.LENGTH_SHORT).show();
           return false;
       }
   }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(checkUsageStatsAllowedOrNot()){
            Toast.makeText(getApplicationContext(),"Inside ondestroy",Toast.LENGTH_SHORT).show();
            startService(new Intent(Menu.this,UsageStats.class));
        }
    }
}