package com.rest.fitnessapp.profileFragmentTabs.editEmail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rest.fitnessapp.R;

public class change_email_authenticate extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = "editemailAuthenticate";
    private EditText email, password;
    private Button submitbtn, cancelbtn;
    private ProgressBar progressBar;

    // Firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email_authenticate);

        // Initialize widgets
        email = findViewById(R.id.emailConfirm_foredit);
        password = findViewById(R.id.passwordConfirm_foredit);
        submitbtn = findViewById(R.id.submit_editemail_btn);
        cancelbtn = findViewById(R.id.cancel_editemail_btn);
        progressBar = findViewById(R.id.progressbar_emailAuthenticate);

        // Set button onClick listeners
        submitbtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);


    }

    @Override
    public void onClick(final View v) {
        switch (v.getId())
        {
            case R.id.submit_editemail_btn:
            {
                progressBar.setVisibility(View.VISIBLE);

                if(email.getText().toString().isEmpty())
                {
                    email.setError("Field cannot be empty");
                    email.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
                {
                    email.setError("Please enter a valid email address");
                    email.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(password.getText().toString().isEmpty())
                {
                    password.setError("Password is required");
                    password.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();

                String emailConfirm = email.getText().toString();
                String passwordConfirm = password.getText().toString();

                AuthCredential credential = EmailAuthProvider.getCredential(emailConfirm, passwordConfirm);
                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            progressBar.setVisibility(View.GONE);
                            Intent changeEmailActivity = new Intent(getApplicationContext(), changeEmail.class);
                            startActivity(changeEmailActivity);
                            finish();
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            }
            case R.id.cancel_editemail_btn:
            {
                progressBar.setVisibility(View.GONE);
                finish();
                break;
            }
        }
    }
}
