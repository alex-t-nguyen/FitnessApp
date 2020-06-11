package com.example.fitnessapp.profileFragmentTabs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.fitnessapp.MainActivity;
import com.example.fitnessapp.R;
import com.example.fitnessapp.logoutAuthenticate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class editProfile extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "editProfile";
    private CardView editEmailCard, deleteAccountCard;
    private Toolbar toolbar;
    private Dialog mDialog;
    private ProgressBar progressBar;
    Button deleteAccountbtn, cancelbtn;

    // Firebase variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set layout
        setContentView(R.layout.edit_profile_menu);

        // defining Cards
        editEmailCard = (CardView)findViewById(R.id.edit_email_btn);
        deleteAccountCard = (CardView)findViewById(R.id.delete_account_btn);

        // define toolbar for back button
        Toolbar toolbar = findViewById(R.id.toolbarID);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // define progress bar

        // Add click listener to cards
        editEmailCard.setOnClickListener(this);
        deleteAccountCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.edit_email_btn:
            {
                break;
            }
            case R.id.delete_account_btn:
            {
                mDialog = new Dialog(this);
                mDialog.setContentView(R.layout.popup_delete_acc_confirmation);
                mDialog.show();

                // Define buttons
                deleteAccountbtn = mDialog.findViewById(R.id.delete_confirmation_btn);
                cancelbtn = mDialog.findViewById(R.id.cancel_delete_btn);

                // Define progressbar
                progressBar = mDialog.findViewById(R.id.delete_acc_progressbar);

                deleteAccountbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        firebaseAuth = FirebaseAuth.getInstance();
                        firebaseUser = firebaseAuth.getCurrentUser();

                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(v.getContext(), "Account Deleted", Toast.LENGTH_LONG).show();
                                    Intent returnlogin = new Intent(v.getContext(), MainActivity.class);
                                    returnlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    returnlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(returnlogin);

                                }
                                else    // If user hasn't recently logged in and needs to be re-authenticated
                                {
                                    Log.d(TAG, "failed to delete");
                                    Intent reAuthenticate = new Intent(v.getContext(), logoutAuthenticate.class);
                                    startActivity(reAuthenticate);
                                    //Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        mDialog.dismiss();
                    }
                });
                cancelbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(v.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                });
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }
}
