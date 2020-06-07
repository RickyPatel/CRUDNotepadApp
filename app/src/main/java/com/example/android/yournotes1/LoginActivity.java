package com.example.android.yournotes1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText inputLogEmail,inputLogPassword;
    private Button btnLogIn;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputLogEmail = findViewById(R.id.input_log_email);
        inputLogPassword = findViewById(R.id.input_log_password);
        btnLogIn =findViewById(R.id.btn_log);
        fAuth = FirebaseAuth.getInstance();

        btnLogIn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                String lemail =inputLogEmail.getText().toString().trim();
                String lpass = inputLogPassword.getText().toString();

                if(!TextUtils.isEmpty(lemail) && !TextUtils.isEmpty(lpass)){
                    logIn(lemail, lpass);
                }

            }
        });
    }
    private void logIn(String email, String password){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In...");
        progressDialog.show();
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Intent intent =new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginActivity.this, "Log In Successful", Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(LoginActivity.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                break;
        }

        return  true;
    }
}
