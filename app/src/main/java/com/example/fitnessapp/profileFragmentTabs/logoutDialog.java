package com.example.fitnessapp.profileFragmentTabs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fitnessapp.R;

public class logoutDialog extends DialogFragment implements View.OnClickListener {
    Button logoutbtn, cancelbtn;
    public Communicator communicator;
    final static String TAG = "logoutDialog";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            communicator = (Communicator) getActivity();
        } catch (ClassCastException ex)
        {
            Log.e(TAG, "onAttach: ClassCastException: " + ex.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.logout_popup, null);
        logoutbtn = (Button) view.findViewById(R.id.logout_btn);
        cancelbtn = (Button) view.findViewById(R.id.cancel_btn);

        logoutbtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logout_btn)
        {
            communicator.onDialogLogout("Logout button was clicked");
            dismiss();
            // Toast.makeText(getActivity(), "Logout button clicked", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // communicator.onDialogLogout("Cancel button was clicked");
            dismiss();
            // Toast.makeText(getActivity(), "Cancel button clicked", Toast.LENGTH_SHORT).show();
        }
    }
    public interface Communicator
    {
        public void onDialogLogout(String message);
    }
}
