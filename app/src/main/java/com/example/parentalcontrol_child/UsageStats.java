package com.example.parentalcontrol_child;

import android.app.Service;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
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

public class UsageStats extends Service {
    public UsageStats() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference parentRef = database.getReference("parents");
    StorageReference storageReference;
    FirebaseAuth mauth;
    String wpcount;
    String fbcount;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mauth=FirebaseAuth.getInstance();
        Toast.makeText(getApplicationContext(),"Serv STarted",Toast.LENGTH_SHORT).show();
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
                        if(usageStat.getPackageName().toLowerCase().contains("com.whaatsapp")){

                            parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String uid = snapshot.getKey();
                                        DatabaseReference ref = database.getReference("parents");
                                        Toast.makeText(getApplicationContext(),uid,Toast.LENGTH_SHORT);
                                        long wp = usageStat.getTotalTimeInForeground();
                                        long sec = (wp/1000)%60;
                                        long min = (wp/(1000*60))%60;
                                        long hr = (wp/(1000*60*60));
                                        wpcount = hr + " hr " + min + " min " + sec + " secs ";
                                        ref.child(uid).child("AppUsage").child("WhatsApp").setValue(wpcount);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        if(usageStat.getPackageName().toLowerCase().contains("com.whaatsapp")){

                            parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String uid = snapshot.getKey();
                                        DatabaseReference ref = database.getReference("parents");
                                        Toast.makeText(getApplicationContext(),uid,Toast.LENGTH_SHORT);
                                        long fb = usageStat.getTotalTimeInForeground();
                                        long sec = (fb/1000)%60;
                                        long min = (fb/(1000*60))%60;
                                        long hr = (fb/(1000*60*60));
                                        fbcount = hr + " hr " + min + " min " + sec + " secs ";
                                        ref.child(uid).child("AppUsage").child("Facebook").setValue(fbcount);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                }
            }
        };
        Toast.makeText(getApplicationContext(),fbcount,Toast.LENGTH_SHORT).show();
        Timer detectAppTimer = new Timer();
        detectAppTimer.scheduleAtFixedRate(detectApp,0,1000);
        return  super.onStartCommand(intent,flags,startId);
    }

}
