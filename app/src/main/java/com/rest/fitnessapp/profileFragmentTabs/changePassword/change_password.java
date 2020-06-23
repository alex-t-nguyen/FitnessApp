package com.rest.fitnessapp.profileFragmentTabs.changePassword;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rest.fitnessapp.R;

public class change_password extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "changePassword";

    // Widgets
    private EditText newPassword, newPasswordConfirm;
    private Button changePasswordbtn, cancelPasswordbtn;
    private ProgressBar progressBar;

    // Firebase variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Instantiate widgets
        newPassword = findViewById(R.id.newPassword);
        newPasswordConfirm = findViewById(R.id.newPasswordConfirm);
        changePasswordbtn = findViewById(R.id.changePasswordBtn);
        cancelPasswordbtn = findViewById(R.id.cancelPasswordBtn);
        progressBar = findViewById(R.id.progressbar_changePasswordConfirm);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        changePasswordbtn.setOnClickListener(this);
        cancelPasswordbtn.setOnClickListener(this);

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId())
        {
            case R.id.changePasswordBtn:
            {
                progressBar.setVisibility(View.VISIBLE);

                if(newPassword.getText().toString().isEmpty())
                {
                    newPassword.setError("Field cannot be empty");
                    newPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(newPasswordConfirm.getText().toString().isEmpty())
                {
                    newPasswordConfirm.setError("Field cannot be empty");
                    newPasswordConfirm.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(!newPassword.getText().toString().equals(newPasswordConfirm.getText().toString()))
                {
                    newPasswordConfirm.setError("Confirm password does not match new password");
                    newPasswordConfirm.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                firebaseUser.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(v.getContext(), "User password updated", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            }
            case R.id.cancelPasswordBtn:
            {
                //Intent returnProfileFragment = new Intent(v.getContext(), Home.class);
               // returnProfileFragment.putExtra("profileFragmentLoad", "profileFragment");
                //startActivity(returnProfileFragment);
                finish();
                break;
            }
        }
    }
}
