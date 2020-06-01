package com.example.parentalcontrol_child;

import android.app.Service;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.parentalcontrol_child.Menu.fb_count;
import static com.example.parentalcontrol_child.Menu.ig_count;
import static com.example.parentalcontrol_child.Menu.wp_count;

public class UsageStats extends Service {
    public UsageStats() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences("App Duration",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        TimerTask detectApp = new TimerTask() {
            @Override
            public void run() {
                UsageStatsManager usageStatsManager = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
                long endtime = System.currentTimeMillis();
                long begintime= endtime- (1000);
                List<android.app.usage.UsageStats> usageStats= usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, begintime,endtime);
                if(usageStats!=null)
                {
                    for(final android.app.usage.UsageStats usageStat : usageStats){
                        if(usageStat.getPackageName().toLowerCase().contains("com.whatsapp")){

                           editor.putLong(wp_count,usageStat.getTotalTimeInForeground());
                        }
                        if(usageStat.getPackageName().toLowerCase().contains("com.facebook.lite")){

                            editor.putLong(fb_count,usageStat.getTotalTimeInForeground());

                        }
                        if(usageStat.getPackageName().toLowerCase().contains("com.instagram.android")){

                            editor.putLong(ig_count,usageStat.getTotalTimeInForeground());
                        }
                        editor.apply();
                    }
                }
            }
        };
        Timer detectAppTimer = new Timer();
        detectAppTimer.scheduleAtFixedRate(detectApp,0,1000);
        return  super.onStartCommand(intent,flags,startId);
    }

}
