package com.rest.fitnessapp.calendar;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rest.fitnessapp.R;
import com.rest.fitnessapp.profileFragmentTabs.DarkModePrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class calendarBottomSheet extends BottomSheetDialogFragment implements calendar_list_item_Adapter.onCalItemListener {

    @Override
    public void onCalendarItemClick(int position) {
        Log.d(TAG, "onCalendarItemClick: " + "calendar item clicked");
        final calendarItem calItem = mData.get(position);
        mDialog = new Dialog(getActivity());
        mDialog.setContentView(R.layout.calendar_item_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialog = setDialogWindowSize(mDialog);

        final EditText dialogTitle = mDialog.findViewById(R.id.dialogTitle);
        final EditText dialogDesc = mDialog.findViewById(R.id.dialogDescription);
        Button dialogSubmit = mDialog.findViewById(R.id.submitDialog);

        //dialogTitle.requestFocus();

        dialogTitle.setText(calItem.getTitle());
        dialogDesc.setText(calItem.getDescription());

        dialogTitle.setSelection(dialogTitle.getText().length());
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        dialogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialogTitle.getText().toString().isEmpty())
                {
                    dialogTitle.setError("Field cannot be empty");
                    dialogTitle.requestFocus();
                    return;
                }
                if (dialogDesc.getText().toString().isEmpty())
                {
                    dialogDesc.setError("Field cannot be empty");
                    dialogDesc.requestFocus();
                    return;
                }
                // Edit current notes in firebase
                Date refDate = Calendar.getInstance().getTime();
                SimpleDateFormat ft = new SimpleDateFormat("d MMMM yyyy");
                String noteDate = ft.format(refDate);

                myRef.child(calItem.getKey()).child("title").setValue(dialogTitle.getText().toString());
                myRef.child(calItem.getKey()).child("description").setValue(dialogDesc.getText().toString());
                myRef.child(calItem.getKey()).child("date").setValue(noteDate);

                mDialog.dismiss();
                myRef = database.getReference("Calendar").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayOfWeek);

                dialogTitle.requestFocus();

            }
        });
        mDialog.show();
    }

    private static final String TAG = "calendarBottomSheet";

    private RecyclerView calendarRecyclerView;
    private calendar_list_item_Adapter calendarAdapter;
    private List<calendarItem> mData;
    private TextView bottomSheetTitle;

    // Dialog
    private Dialog mDialog;

    // Firebase variables
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String dayOfWeek;

    // Dark mode manager
    private DarkModePrefManager darkModePrefManager;

    // FAB button
    private FloatingActionButton fabAdd;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar_bottom_sheet, container, false);

        // Initialize Widgets
        bottomSheetTitle = v.findViewById(R.id.bottomSheetTitle);
        fabAdd = v.findViewById(R.id.calendarFAB);

        darkModePrefManager = new DarkModePrefManager(getActivity());
        if (darkModePrefManager.loadDarkModeState())
        {
            getActivity().setTheme(R.style.darktheme);
            fabAdd.setBackgroundColor(Color.parseColor("#03DAC6"));
        }
        else {
            getActivity().setTheme(R.style.AppTheme);
            fabAdd.setBackgroundColor(Color.parseColor("#FF4081"));
        }

        calendarRecyclerView = v.findViewById(R.id.calendarRecyclerView);
        mData = new ArrayList<>();


        // Adapter initialize and setup
        calendarAdapter = new calendar_list_item_Adapter(getActivity(), mData, this);
        calendarRecyclerView.setAdapter(calendarAdapter);
        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(calendarRecyclerView);

        // Get key to access correct path in database
        dayOfWeek = getArguments().getString("dayOfWeek");

        bottomSheetTitle.setText(dayOfWeek);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Calendar").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayOfWeek);

        // Fetch data from database to reload recycler view
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mData.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    calendarItem item = snapshot.getValue(calendarItem.class);
                    mData.add(item);

                    Log.d(TAG, "OnCreate Name: " + item.getTitle());

                    calendarAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = new Dialog(getActivity());
                mDialog.setContentView(R.layout.calendar_item_dialog);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                mDialog = setDialogWindowSize(mDialog);

                final EditText dialogTitle = mDialog.findViewById(R.id.dialogTitle);
                final EditText dialogDesc = mDialog.findViewById(R.id.dialogDescription);

                // Get current date of posting note
                Date refDate = Calendar.getInstance().getTime();
                SimpleDateFormat ft = new SimpleDateFormat("d MMMM yyyy");
                final String noteDate = ft.format(refDate);
                Button dialogSubmit = mDialog.findViewById(R.id.submitDialog);

                dialogTitle.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                dialogSubmit.setText("Add Note");
                dialogSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialogTitle.getText().toString().isEmpty())
                        {
                            dialogTitle.setError("Field cannot be empty");
                            dialogTitle.requestFocus();
                            return;
                        }
                        if (dialogDesc.getText().toString().isEmpty())
                        {
                            dialogDesc.setError("Field cannot be empty");
                            dialogDesc.requestFocus();
                            return;
                        }
                        String itemKey = myRef.push().getKey();

                        myRef.child(itemKey).child("title").setValue(dialogTitle.getText().toString());
                        myRef.child(itemKey).child("description").setValue(dialogDesc.getText().toString());
                        myRef.child(itemKey).child("date").setValue(noteDate);
                        myRef.child(itemKey).child("key").setValue(itemKey);

                        mData.add(new calendarItem(dialogTitle.getText().toString(), dialogDesc.getText().toString(), noteDate, itemKey));
                        calendarAdapter.notifyDataSetChanged();
                        mDialog.dismiss();
                        myRef = database.getReference("Calendar").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayOfWeek);

                        dialogTitle.requestFocus();
                    }
                });
                mDialog.show();
            }
        });

        return v;
    }

    public Dialog setDialogWindowSize(Dialog dialog)
    {
        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * 0.85f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * 0.75f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        dialog.getWindow().setAttributes(layoutParams);

        return dialog;
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(mData, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            Log.d(TAG, "Name position: " + mData.get(viewHolder.getAdapterPosition()).getTitle());

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        // Exercise exercise = snapshot.getValue(Exercise.class);
                        String title = snapshot.child("title").getValue(String.class);
                        if(title == null)
                            Log.d(TAG, "title variable is null");
                        else if (mData.get(viewHolder.getAdapterPosition()).getTitle() == null)
                            Log.d(TAG, "item title is null");
                        if(title.equals(mData.get(viewHolder.getAdapterPosition()).getTitle()))
                            myRef = database.getReference("Calendar").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayOfWeek).child(snapshot.getKey());
                        Log.d(TAG, "OnSwiped Name: " + title);

                    }
                    myRef.removeValue();
                    mData.remove(viewHolder.getAdapterPosition());
                    calendarAdapter.notifyDataSetChanged();
                    // Reset reference to selected day of week
                    myRef = database.getReference("Calendar").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayOfWeek);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    };
}
