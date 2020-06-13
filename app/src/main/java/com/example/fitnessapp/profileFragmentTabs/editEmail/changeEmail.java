package com.example.fitnessapp.profileFragmentTabs.editEmail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fitnessapp.R;
import com.example.fitnessapp.profileFragmentTabs.editProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class changeEmail extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "changeEmail";
    // Widgets
    private EditText newEmail, confirmEmail;
    private Button changeEmailbtn, cancelEmailBtn;
    private ProgressBar progressBar;

    // Firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_email);

        // Instantiate widgets
        newEmail = (EditText)findViewById(R.id.newUserEmail);
        confirmEmail = (EditText)findViewById(R.id.confirmUserEmail);
        changeEmailbtn = (Button)findViewById(R.id.changeEmailBtn);
        cancelEmailBtn = (Button)findViewById(R.id.cancelEmailBtn);
        progressBar = (ProgressBar)findViewById(R.id.progressbar_changeEmail);

        changeEmailbtn.setOnClickListener(this);
        cancelEmailBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId())
        {
            case R.id.changeEmailBtn:
            {
                progressBar.setVisibility(View.VISIBLE);

                if(newEmail.getText().toString().isEmpty())
                {
                    newEmail.setError("Field cannot be empty");
                    newEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(confirmEmail.getText().toString().isEmpty())
                {
                    confirmEmail.setError("Field cannot be empty");
                    confirmEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(newEmail.getText().toString()).matches())
                {
                    newEmail.setError("Please enter a valid email address");
                    newEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(confirmEmail.getText().toString()).matches())
                {
                    confirmEmail.setError("Please enter a valid email address");
                    confirmEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                // Check if email and confirm email match
                if(newEmail.getText().toString().equals(confirmEmail.getText().toString())) {
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseUser = firebaseAuth.getCurrentUser();
                    firebaseUser.updateEmail(newEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(v.getContext(), "Email changed successfully", Toast.LENGTH_SHORT).show();
                                Intent returnEditProfile = new Intent(getApplicationContext(), editProfile.class);
                                startActivity(returnEditProfile);
                                finish();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(v.getContext(), "Failed to change email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else    // If email and confirm email don't match
                {
                    progressBar.setVisibility(View.GONE);
                    confirmEmail.setError("Confirm email does not match initial email");
                    confirmEmail.requestFocus();
                    return;
                }
                break;
            }
            case R.id.cancelEmailBtn:
            {
                Intent returnEditProfile = new Intent(v.getContext(), editProfile.class);
                startActivity(returnEditProfile);
                finish();
                break;
            }
        }
    }
}
