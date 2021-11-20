package com.example.login_and_registration_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    //    Initialize fields
    TextView alreadyhaveAnAccount;
    EditText inputFullName,inputEmail,inputPassword,inputConfirmPassword;
    Button btnRegister;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        alreadyhaveAnAccount = findViewById(R.id.alreadyHaveAnAccount);
        inputEmail = findViewById(R.id.inputEmail);
        inputFullName = findViewById(R.id.inputFullName);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnRegister = findViewById(R.id.btnResetPassword);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //        remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //        creation of a clicklisteners for initialized variables above

        alreadyhaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerfoAuth();
            }
        });

    }

//    method for PerfoAUth

    private void PerfoAuth() {
        String name = inputFullName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

//        This is to check the email field and password for correct values entered

        if (name.isEmpty())
        {
            inputFullName.setError("Please Enter The Correct Name");
            inputFullName.requestFocus();
        }
        else if (!email.matches(emailPattern))
        {
            inputEmail.setError("Please Enter The Correct Email");
            inputEmail.requestFocus();
        }
        else if (password.isEmpty() || password.length()<6)
        {
            inputPassword.setError("Please Enter The Correct Password To Proceed");
        }
        else if (!password.equals(confirmPassword))
        {
            inputConfirmPassword.setError("The Password Entered Does Not Match");
        }
        else
        {
            progressDialog.setMessage("Please Wait As Registration Is Ongoing....");
            progressDialog.setTitle("REGISTRATION");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

//            initialize firebase authentication here

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        User user = new User(name, email);
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                sendUserToNextActivity();
                                Toast.makeText(RegistrationActivity.this, "User Registration Is Successful", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(RegistrationActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
//Method for when user is successfully registered
    private void sendUserToNextActivity() {
        Intent intent = new Intent(RegistrationActivity.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}