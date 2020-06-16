package com.example.fitnessapp.profileFragmentTabs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;

import com.example.fitnessapp.R;
import com.example.fitnessapp.profileFragmentTabs.DeleteAccount.deleteAuthenticate;
import com.example.fitnessapp.profileFragmentTabs.editEmail.change_email_authenticate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class editProfile extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "editProfile";
    private CardView editEmailCard, deleteAccountCard;
    private Toolbar toolbar;
    private Dialog mDialog;
    private ProgressBar progressBar;

    private DarkModePrefManager darkModePrefManager;
    Button deleteAccountbtn, cancelbtn;

    // Firebase variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        darkModePrefManager = new DarkModePrefManager(this);
        if (darkModePrefManager.loadDarkModeState())
        {
            setTheme(R.style.darkthemeProfile);
        }
        else {
            setTheme(R.style.lightThemeProfile);
        }

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
                Intent editEmailActivity = new Intent(v.getContext(), change_email_authenticate.class);
                startActivity(editEmailActivity);
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

                deleteAccountbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        Intent reAuthenticate = new Intent(v.getContext(), deleteAuthenticate.class);
                        startActivity(reAuthenticate);
                        //Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

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
