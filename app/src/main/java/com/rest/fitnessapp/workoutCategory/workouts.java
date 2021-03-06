package com.rest.fitnessapp.workoutCategory;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rest.fitnessapp.R;
import com.rest.fitnessapp.profileFragmentTabs.DarkModePrefManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class workouts extends AppCompatActivity {
    private ActionBarDrawerToggle mToggle;
    private static final String TAG = "Workouts";
    private ExpandableListView listView;
    private expandableListAdapter listAdapter;
    private List<String> listHeader = new ArrayList<>();
    private HashMap<String, List<String>> listHashMap = new HashMap<>();


    // Pop up menu variables
    private EditText categoryTitle;
    private Dialog myDialog;
    private String headerTitle = "";
    private boolean validTitle;
    private EditText itemTitle;

    private Spinner spinner;
    private Spinner spinnerItems;
    private String chosenCategory;
    private String chosenItem;
    private boolean categorySelected;
    private boolean itemSelected;
    List<String> tempHeaderListItems;
    private DarkModePrefManager darkModePrefManager;

    // Firebase Database variables
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String workoutKey;
    private List<String> newExpandableCategory;

    private HashMap<String, String> keyHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        darkModePrefManager = new DarkModePrefManager(this);
        if (darkModePrefManager.loadDarkModeState())
        {
            this.setTheme(R.style.darktheme);
        }
        else
            this.setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_workouts);

        DrawerLayout mDrawerLayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (darkModePrefManager.loadDarkModeState())
        {
            mToggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        }
        else
            mToggle.getDrawerArrowDrawable().setColor(Color.BLACK);


        final NavigationView navigationView = findViewById(R.id.navigationView);

        listView = findViewById(R.id.expandable_list_view);
        //initializeData();

        listAdapter = new expandableListAdapter(this, listHeader, listHashMap);
        listView.setAdapter(listAdapter);

        // Pop-up menu
        myDialog = new Dialog(this);

        // Firebase Database
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());  // Gets current user ID to add to their specific workout lists

        keyHashMap = new HashMap<>();
        final Intent exerciseIntent = new Intent(getApplicationContext(), workoutTemplate.class);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    try {
                        final String parentKey = snapshot.getKey();
                        final DatabaseReference childReference = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(parentKey);

                        childReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    // String childValue = snapshot1.getValue().toString();
                                    keyHashMap.put(snapshot1.getKey(), snapshot1.getKey());
                                    listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                        @Override
                                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                            Toast.makeText(getApplicationContext(), listHashMap.get(listHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                                            //System.out.println("Child value: " + childValue);
                                            //if(keyHashMap.get(listHashMap.get(listHeader.get(groupPosition)).get(childPosition)).equals(snap)
                                            String key = keyHashMap.get(listHashMap.get(listHeader.get(groupPosition)).get(childPosition));
                                            String group = listHeader.get(groupPosition);
                                            exerciseIntent.putExtra("selectedWorkoutKey", key);
                                            exerciseIntent.putExtra("selectedWorkoutGroup", group);
                                            startActivity(exerciseIntent);
                                            finish();
                                                //childReference.removeEventListener(this);
                                            return true;
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        childReference.removeEventListener(this);
                    }
                    catch(Exception e)
                    {
                        Log.i(TAG, "Exception2: " + e.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        // Drawer Menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.back:
                    {
                        finish();
                        break;
                    }
                    case R.id.addCategory:
                    {
                        //View header = getLayoutInflater().inflate(R.layout.list_group, null);
                        //listView.addHeaderView(header);
                        /*
                            1) Create pop-up menu w/ EditText box
                            2) Type in header group to add
                            3) Click create button
                            4) Add new header to list
                         */
                        try {
                            ShowPopupCategory(item);
                        }
                        catch (Exception e)
                        {
                            Log.d(TAG, "Add pop-up: " + e.toString());
                        }
                        break;
                    }
                    case R.id.addItem:
                    {
                        /*
                            1) Create pop-up menu w/ dropdown box and EditText box
                            2) Choose header group to add to from dropdown menu
                            3) Type in title of item-to-add
                            4) Click create button
                            5) Add new item to corresponding group
                         */
                        ShowPopupItem(item);
                        break;
                    }
                    case R.id.deleteCategory:
                    {
                        ShowPopUpDeleteCategory(item);
                        break;
                    }
                    case R.id.deleteItem:
                    {
                        ShowPopUpDeleteItem(item);
                        break;
                    }
                }
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeData()
    {
        listHeader = new ArrayList<>();
        listHashMap = new HashMap<>();

        listHeader.add("Push");
        listHeader.add("Pull");
        listHeader.add("Legs");

        List<String> push = new ArrayList<>();
        push.add("Workout 1");

        List<String> pull = new ArrayList<>();
        pull.add("Workout 1");
        pull.add("Workout 2");
        pull.add("Workout 3");
        pull.add("Workout 4");


        List<String> legs = new ArrayList<>();
        legs.add("Workout 1");
        legs.add("Workout 2");

        listHashMap.put(listHeader.get(0), push);
        listHashMap.put(listHeader.get(1), pull);
        listHashMap.put(listHeader.get(2), legs);
    }

    public void ShowPopupCategory(MenuItem menuItem)
    {
        TextView textClose;
        Button btnAddCategory;
        myDialog.setContentView(R.layout.add_category_popup);
        textClose = myDialog.findViewById(R.id.textCloseCategory);
        btnAddCategory = myDialog.findViewById(R.id.addCategory);
        categoryTitle = myDialog.findViewById(R.id.category_title);
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headerTitle = categoryTitle.getText().toString();
                if(headerTitle.isEmpty())
                {
                    categoryTitle.setError("Field cannot be empty");
                    categoryTitle.requestFocus();
                    validTitle = false;
                }
                else if(listHeader.contains(headerTitle))
                {
                    categoryTitle.setError("Title name already exists");
                    categoryTitle.requestFocus();
                    validTitle = false;
                }
                else
                {
                    categoryTitle.setError(null);
                    validTitle = true;
                }
                if(validTitle) {
                    listHeader.add(headerTitle);    // Add header title to list of headers
                    //newExpandableCategory = new ArrayList<>(); // Create new expandable list w/ 0 items
                    //listHashMap.put(listHeader.get(listHeader.size() - 1), newExpandableCategory);
                    myDialog.dismiss();
                    //listAdapter.notifyDataSetChanged();
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(listHeader.get(listHeader.size() - 1));  // Gets current user ID to add to their specific workout lists
                    myRef.setValue(listHeader.get(listHeader.size() - 1));   // Add category to database
                }
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void ShowPopupItem(MenuItem menuItem)
    {
        TextView textClose;
        Button btnAddItem;

        List<String> tempHeaderList = new ArrayList<>();
        tempHeaderList.addAll(0, listHeader);
        tempHeaderList.add(0, "Choose category");

        myDialog.setContentView(R.layout.add_item_popup);
        textClose = myDialog.findViewById(R.id.textCloseItem);
        btnAddItem = myDialog.findViewById(R.id.addItem);
        itemTitle = myDialog.findViewById(R.id.item_title);
        spinner = myDialog.findViewById(R.id.choose_category);

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(this, R.layout.color_spinner_layout, tempHeaderList);
        itemAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(itemAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenCategory = parent.getItemAtPosition(position).toString();
                categorySelected = !chosenCategory.equals("Choose category");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categorySelected = false;
            }
        });
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    1) Check if item title is empty
                    2) Check if category is selected (Produces null pointer when searching for "item title" in list if not selected)
                        2a) If category is selected -> check if item title is in list
                        2b) Else don't check if item title is in list
                 */
                if(itemTitle.getText().toString().isEmpty())
                {
                    itemTitle.setError("Field cannot be empty");
                    itemTitle.requestFocus();
                    validTitle = false;
                }
                else {  // Item title will always have some text if this runs
                    if (!categorySelected) {
                        setSpinnerError(spinner, "Field cannot be empty");
                        validTitle = false;
                    } else if (listHashMap.get(chosenCategory).contains(itemTitle.getText().toString())) {
                        itemTitle.setError("Title name already exists");
                        itemTitle.requestFocus();
                        validTitle = false;
                    } else {
                        itemTitle.setError(null);
                        setSpinnerError(spinner, null);
                        validTitle = true;
                    }
                }
                if(validTitle) {
                    //newExpandableCategory = new ArrayList<>();
                    database = FirebaseDatabase.getInstance();

                    myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chosenCategory);  // Gets current user ID's chosen category to add to their specific workout lists
                    myRef.child(itemTitle.getText().toString()).setValue(itemTitle.getText().toString());   // Add item to database
                    myDialog.dismiss();

                    //listHashMap.get(chosenCategory).add(headerTitle);
                    //listAdapter.notifyDataSetChanged();
                }
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
    /* A) TO ADD ITEM TO CATEGORY
        1) Make new empty ArrayList<String>()
        2) Access database using addValueEventListener and copy all children of a category into the new ArrayList
        3) Add new item from TextView popup menu to end of new ArrayList
        4) Add new item to database under correct category
        5) Put new ArrayList (now contains old items and the new item) into hash map using: listHashMap.put("category name", "new item name");
            * Note: 1) Category name can be taken from TextView in popup menu when asking what category to add to
                    2) This step displays the items on the phone screen
        6) Call listAdapter.notifyDataSetChanged()

       B) ADD ITEMS IN ON CREATE
        1) Change database reference to the category instead of user ID
        2) Call addValueEventListener again (2nd time)
        3) For-each loop each child (items of category) and add them to an ArrayList
        4) Put the ArrayList into the hash map using: listHashMap.put("category name", "new item name");
        5) Call listAdapter.notifyDataSetChanged()
    */

    public void ShowPopUpDeleteCategory(MenuItem menuItem)
    {
        TextView textClose;
        Button btnRemoveCategory;

        List<String> tempHeaderList = new ArrayList<>();
        tempHeaderList.addAll(0, listHeader);
        tempHeaderList.add(0, "Choose category");

        myDialog.setContentView(R.layout.delete_category_popup);
        textClose = myDialog.findViewById(R.id.textCloseCategoryDelete);
        btnRemoveCategory = myDialog.findViewById(R.id.removeCategoryButton);
        spinner = myDialog.findViewById(R.id.choose_categoryDelete);

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(this, R.layout.color_spinner_layout, tempHeaderList);
        itemAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(itemAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenCategory = parent.getItemAtPosition(position).toString();
                categorySelected = !chosenCategory.equals("Choose category");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categorySelected = false;
            }
        });

        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        btnRemoveCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!categorySelected)
                {
                    setSpinnerError(spinner,"Field cannot be empty");
                }
                else {
                    //listHashMap.remove(chosenCategory);
                    //listAdapter.notifyDataSetChanged();

                    database = FirebaseDatabase.getInstance();

                    myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chosenCategory);  // Gets current user ID's chosen category to add to their specific workout lists
                    myRef.removeValue();   // Remove category from database

                    myRef = database.getReference("Workouts");
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean userFound = false;
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                            {
                                if (dataSnapshot1.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                {
                                    userFound = true;
                                    break;
                                }
                            }
                            if (!userFound)
                                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    myDialog.dismiss();
                }
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void ShowPopUpDeleteItem(MenuItem menuItem)
    {
        TextView textClose;
        Button btnRemoveItem;

        List<String> tempHeaderList = new ArrayList<>();
        tempHeaderList.addAll(0, listHeader);
        tempHeaderList.add(0, "Choose category");

        myDialog.setContentView(R.layout.delete_item_popup);
        textClose = myDialog.findViewById(R.id.textCloseItemDelete);
        btnRemoveItem = myDialog.findViewById(R.id.removeItemButton);
        spinner = myDialog.findViewById(R.id.choose_categoryDeleteItemPopup);

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(this, R.layout.color_spinner_layout, tempHeaderList);
        itemAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(itemAdapter);

        spinnerItems = myDialog.findViewById(R.id.choose_ItemDelete);
        spinnerItems.setEnabled(false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenCategory = parent.getItemAtPosition(position).toString();
                categorySelected = !chosenCategory.equals("Choose category");
                setItemSpinner(categorySelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categorySelected = false;
            }
        });

        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        spinnerItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenItem = parent.getItemAtPosition(position).toString();
                itemSelected = !chosenItem.equals("Choose item");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                itemSelected = false;
            }
        });

        btnRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!categorySelected) {
                    setSpinnerError(spinner, "Field cannot be empty");
                } else if (!itemSelected) {
                    setSpinnerError(spinnerItems, "Field cannot be empty");
                } else {
                    //newExpandableCategory = new ArrayList<>();
                    database = FirebaseDatabase.getInstance();

                    myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chosenCategory);  // Gets current user ID's chosen category to add to their specific workout lists
                    myRef.addValueEventListener(new ValueEventListener() {

                        /*
                            --------------------------------------------------------------
                            onDataChange is ASYNCHRONOUS
                            Deleting an item stores string variable (chosenItem) globally b/c it is a private variable
                            When trying to add workout with the same name as chosenItem the if statement returns true
                            Resulting in the value being deleted immediately after being pushed to database
                            SOLUTION:
                                1) Make chosenItem String variable local so that the value is not saved after function ends
                                2) Reset chosenItem String variable to an empty String at end of function
                            --------------------------------------------------------------
                         */

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                String item = snapshot.getKey();
                                //newExpandableCategory.add(item);
                                if (item.equals(chosenItem)) {
                                    myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chosenCategory).child(snapshot.getKey());
                                    Log.d(TAG, snapshot.getKey());
                                    myRef.removeValue();   // Remove item from database
                                    myRef = database.getReference("Workouts");
                                    myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            boolean userFound = false;
                                            Log.d(TAG, "deleteItem: onDataChange: Re-add userID");
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                            {
                                                if (dataSnapshot1.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                                {
                                                    userFound = true;
                                                    break;
                                                }
                                            }
                                            if (!userFound)
                                                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                            /*
                                            DatabaseReference childReference = myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                            childReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    boolean workoutCategoryFound = false;
                                                    for (DataSnapshot workoutCategorySnapshot : snapshot.getChildren())
                                                    {
                                                        if (workoutCategorySnapshot.getKey().equals(chosenCategory))
                                                        {
                                                            workoutCategoryFound = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!workoutCategoryFound)
                                                        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chosenCategory).setValue(chosenCategory);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            */
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    //newExpandableCategory.remove(item);
                                    chosenItem = "";
                                    //Log.d(TAG, "Header Size: " + listHeader.size());
                                    //Log.d(TAG, "HashMap Size: " + listHashMap.size());
                                    //listAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            myRef.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    myDialog.dismiss();
                }
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void setItemSpinner(boolean categorySelected)
    {
        if(categorySelected) {
            spinnerItems.setEnabled(true);
            ArrayList<String> tempHeaderListItems = new ArrayList<>();

            tempHeaderListItems.addAll(0, listHashMap.get(chosenCategory));
            tempHeaderListItems.add(0, "Choose item");

            ArrayAdapter<String> itemAdapter2 = new ArrayAdapter<>(this, R.layout.color_spinner_layout, tempHeaderListItems);
            itemAdapter2.setDropDownViewResource(R.layout.spinner_dropdown_layout);
            spinnerItems.setAdapter(itemAdapter2);
        }
        else
        {
            ArrayList<String> tempHeaderListItems = new ArrayList<>();
            tempHeaderListItems.add(0, "Choose item");

            ArrayAdapter<String> itemAdapter2 = new ArrayAdapter<>(this, R.layout.color_spinner_layout, tempHeaderListItems);
            itemAdapter2.setDropDownViewResource(R.layout.spinner_dropdown_layout);
            spinnerItems.setAdapter(itemAdapter2);
        }

    }

    public void setSpinnerError(Spinner spinner, String error)
    {
        View selectedView = spinner.getSelectedView();
        if(selectedView instanceof TextView)
        {
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError(error);
            selectedTextView.setTextColor(Color.RED);
            selectedTextView.setText(error);
            spinner.requestFocus();
        }
    }

    @Override
    protected void onResume()
    {
        listHashMap.clear();
        listHeader.clear();
        listAdapter.notifyDataSetChanged();
        super.onResume();

        // Firebase Database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());  // Gets current user ID to add to their specific workout lists

        // initializeData();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listHashMap.clear();
                listHeader.clear();
                //listAdapter.notifyDataSetChanged();
                Log.d(TAG, "Group change called");

                for (final DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    final DatabaseReference childReference = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(snapshot.getKey());
                    childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final ArrayList<String> items = new ArrayList<>();
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren())
                            {
                                items.add(snapshot1.getKey());
                            }
                            listHeader.add(snapshot.getKey());
                            if(!listHashMap.containsKey(listHeader.get(listHeader.size() - 1))) {
                                listHashMap.put(listHeader.get(listHeader.size() - 1), items);
                                //listAdapter = new expandableListAdapter(getApplicationContext(), listHeader, listHashMap);
                                //listView.setAdapter(listAdapter);

                                listAdapter.notifyDataSetChanged();
                                Log.d(TAG, "Hash Map: " + listHashMap);
                            }
                            childReference.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
