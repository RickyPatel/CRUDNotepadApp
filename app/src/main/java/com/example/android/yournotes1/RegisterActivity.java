package com.example.android.yournotes1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private Button btnReg;
    private EditText inputName,inputEmail,inputPassword;
    private FirebaseAuth fAuth;
    private DatabaseReference fUsersDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        btnReg =findViewById(R.id.btn_reg);
        inputName = findViewById(R.id.input_reg_name);
        inputEmail = findViewById(R.id.input_reg_email);
        inputPassword = findViewById(R.id.input_reg_password);
        fAuth = FirebaseAuth.getInstance();
        fUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =inputName.getText().toString();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString();
                registerUser(name, email, password);
                
            }
        });
    }

    private void registerUser(final String uname, String uemail, String upassword) {

        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        fAuth.createUserWithEmailAndPassword(uemail, upassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    fUsersDatabase.child(fAuth.getCurrentUser().getUid())
                            .child("basic").child("name").setValue(uname).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                                Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_LONG).show();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "ERROR:" + task.getException().getMessage() ,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "ERROR:" + task.getException().getMessage() ,Toast.LENGTH_LONG).show();
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
