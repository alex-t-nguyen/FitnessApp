package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class logoutAuthenticate extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "logoutAuthenticate";
    private EditText email, password;
    private Button deletebtn, cancelbtn;

    // Firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_authenticate);
        email = (EditText)findViewById(R.id.emailConfirm);
        password = (EditText)findViewById(R.id.passwordConfirm);
        deletebtn = (Button)findViewById(R.id.delete_acc_confirm_btn);
        cancelbtn = (Button)findViewById(R.id.cancel_acc_confirm_btn);

        deletebtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId())
        {
            case R.id.delete_acc_confirm_btn:
            {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();

                AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(), password.getText().toString());
                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    // When user is re-authenticated
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated");
                        if (task.isSuccessful()) {
                            // Delete user from database
                            database = FirebaseDatabase.getInstance();
                            myRef = database.getReference("Users").child(firebaseUser.getUid());
                            // Delete user from "Users" branch
                            myRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Delete user from "Workouts" branch
                                    myRef = database.getReference("Workouts").child(firebaseUser.getUid());
                                    myRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Delete user from authentication
                                            firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(v.getContext(), "Account Deleted", Toast.LENGTH_LONG).show();
                                                        Intent returnlogin = new Intent(v.getContext(), MainActivity.class);
                                                        returnlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        returnlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(returnlogin);
                                                    } else {
                                                        Log.d(TAG, "failed to delete");
                                                        Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            Log.d(TAG, "Re-authentication failed");
                        }
                    }
                });
            }
            case R.id.cancel_acc_confirm_btn:
            {

            }
        }
    }
}
