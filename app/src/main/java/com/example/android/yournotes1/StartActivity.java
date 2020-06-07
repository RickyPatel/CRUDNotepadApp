package com.example.android.yournotes1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    private Button btnReg, btnLog;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        btnLog = findViewById(R.id.start_log_btn);
        btnReg = findViewById(R.id.start_reg_btn);
        fAuth = FirebaseAuth.getInstance();

        updateUI();
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }
    private void register(){
        Intent regIntent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(regIntent);
    }

    private void login(){
        Intent logInintent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(logInintent);
    }

    private void updateUI(){
        if(fAuth.getCurrentUser()!=null){
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.i("StartActivity", "fAuth!=null");
        }else{

            Log.i("StartActivity", "fAuth==null");
        }
    }
}
