package com.example.parentalcontrol_child;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parentalcontrol_child.models.parents;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    EditText e1;
    Button b1;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference parentRef = database.getReference("parents");
    StorageReference storageReference;
    FirebaseAuth mAuth;
    String found="false";
    String parent_name,memail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        e1=findViewById(R.id.editText);
        b1=findViewById(R.id.button);
        mAuth = FirebaseAuth.getInstance();

//        DatabaseReference temp = parentRef.child(mAuth.getCurrentUser().getUid());



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                memail = e1.getText().toString();
                parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DatabaseReference ref = parentRef.child(snapshot.getKey());
//                            Toast.makeText(getApplicationContext(),"key : "+snapshot.getKey().toString(),Toast.LENGTH_SHORT).show();
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                                    String child_email = dataSnapshot1.child("childEmail").getValue().toString();
                                    Toast.makeText(getApplicationContext(),"entered mail : "+memail,Toast.LENGTH_SHORT).show();
                                    if(child_email.equals(memail)){
                                            found = "true";
                                            parent_name = dataSnapshot1.child("name").getValue().toString();
                                            Toast.makeText(getApplicationContext(),"Parents name : "+parent_name,Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getApplicationContext(),WelcomeActivity.class);
                                            startActivity(i);
                                            finish();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Child not registered!",Toast.LENGTH_SHORT).show();
                                    }

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

//                if(found.equals("true")){
//                    Toast.makeText(getApplicationContext(),"Parent : "+parent_name,Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(getApplicationContext(),"Child not registered!",Toast.LENGTH_SHORT).show();
//                }
            }
        });

    }
}
