package com.example.parentalcontrol_child;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TrackingService extends Service {
    private static final String TAG = TrackingService.class.getSimpleName();
    String mail,pass;
    FirebaseAuth mauth;

    public TrackingService() {
    }

   @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mail= intent.getStringExtra("pemail");
        pass= intent.getStringExtra("ppass");

//        Toast.makeText(getApplicationContext(),"Parents email : "+mail,Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),"Parents email : "+pass,Toast.LENGTH_LONG).show();
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           buildNotification();
       }
       loginToFirebase();

       return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {return null;}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        mauth=FirebaseAuth.getInstance();
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildNotification();
        }
        loginToFirebase();*/
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Location","Location",NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"Location")
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.tracking_enabled);
        startForeground(1, builder.build());
        //NotificationManagerCompat manager = NotificationManagerCompat.from(this);
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            unregisterReceiver(stopReceiver);

            stopSelf();
        }
    };
    private void loginToFirebase() {


        String email = mail;
        String password = pass;
        Toast.makeText(getApplicationContext(),email,Toast.LENGTH_LONG).show();
        requestLocationUpdates();

        //Call OnCompleteListener if the user is signed in successfully//

        mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    //Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void requestLocationUpdates(){
        //Toast.makeText(getApplicationContext(),"inside requestloc",Toast.LENGTH_LONG).show();
        LocationRequest request = new LocationRequest();
        request.setInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        //final String path = getString(R.string.firebase_path);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission== PackageManager.PERMISSION_GRANTED)
        {
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myref = database.getReference("parents");
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        String loc= location.toString();
                        myref.child(mauth.getCurrentUser().getUid()).child("Location").setValue(location);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Error While getting the Location, Try Again!",Toast.LENGTH_LONG).show();
                    }
                }
            },null);
        }
    }
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//        startService(restartServiceIntent);
//        super.onTaskRemoved(rootIntent);
//    }
}