package com.example.parentalcontrol_child;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    Button loc;
    String mail,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();

        mail = intent.getStringExtra("pemail");
        pass = intent.getStringExtra("ppass");

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

}