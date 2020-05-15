package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    // Firebase Database variables
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userID;
    private List<String> newExpandableCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        DrawerLayout mDrawerLayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final NavigationView navigationView = (NavigationView)findViewById(R.id.navigationView);

        listView = (ExpandableListView)findViewById(R.id.expandable_list_view);
        //initializeData();

        listAdapter = new expandableListAdapter(this, listHeader, listHashMap);
        listView.setAdapter(listAdapter);

        // Pop-up menu
        myDialog = new Dialog(this);

        // Firebase Database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());  // Gets current user ID to add to their specific workout lists

        // Get list of workouts data from database and load onto screen
        /*
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //listHashMap.clear();
                listHeader.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    listHeader.add(snapshot.getValue().toString());
                    Log.i(TAG, "Header adding: " + snapshot.getValue().toString());
                    //listHashMap.put(snapshot.getKey(), newExpandableCategory);    *** Need to change database reference to the category instead of user ID and call addValueEventListener again
                }                                                                   //  and go through each child (items of category) and add them to an ArrayList, then put the ArrayList into the hash map
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listHashMap.clear();
                listHeader.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    try {
                        final String parentKey = snapshot.getKey();  // contains "Push" category
                        DatabaseReference childReference = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(parentKey);

                        childReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                newExpandableCategory = new ArrayList<>();
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    String childValue = snapshot1.getValue().toString();
                                    newExpandableCategory.add(childValue);
                                }
                                listHeader.add(parentKey);
                                listHashMap.put(listHeader.get(listHeader.size() - 1), newExpandableCategory);
                                listAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    catch(Exception e)
                    {
                        Log.i(TAG, "Exception: " + e.toString());
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
        textClose = (TextView)myDialog.findViewById(R.id.textCloseCategory);
        btnAddCategory = (Button)myDialog.findViewById(R.id.addCategory);
        categoryTitle = (EditText)myDialog.findViewById(R.id.category_title);
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
                else
                {
                    categoryTitle.setError(null);
                    validTitle = true;
                }
                if(validTitle) {
                    listHeader.add(headerTitle);    // Add header title to list of headers
                    final List<String> newExpandableCategory = new ArrayList<>(); // Create new expandable list w/ 0 items
                    listHashMap.put(listHeader.get(listHeader.size() - 1), newExpandableCategory);
                    myDialog.dismiss();
                    listAdapter.notifyDataSetChanged();
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(listHeader.get(listHeader.size() - 1));  // Gets current user ID to add to their specific workout lists
                    myRef.setValue(listHeader.get(listHeader.size() - 1));   // Add category to database
                    /*
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                listHashMap.put(snapshot.getKey(), newExpandableCategory);
                            }
                            listAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                     */
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
        textClose = (TextView)myDialog.findViewById(R.id.textCloseItem);
        btnAddItem = (Button)myDialog.findViewById(R.id.addItem);
        itemTitle = (EditText)myDialog.findViewById(R.id.item_title);
        spinner = (Spinner)myDialog.findViewById(R.id.choose_category);

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(this, R.layout.color_spinner_layout, tempHeaderList);
        itemAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(itemAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenCategory = parent.getItemAtPosition(position).toString();
                if(chosenCategory.equals("Choose category"))
                    categorySelected = false;
                else
                    categorySelected = true;
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
                headerTitle = itemTitle.getText().toString();
                if(headerTitle.isEmpty())
                {
                    itemTitle.setError("Field cannot be empty");
                    itemTitle.requestFocus();
                    validTitle = false;
                }
                else
                {
                    itemTitle.setError(null);
                    validTitle = true;
                }
                if(!categorySelected)
                {
                    setSpinnerError(spinner,"Field cannot be empty");
                }
                else if(validTitle) {
                    newExpandableCategory = new ArrayList<>();
                    database = FirebaseDatabase.getInstance();

                    myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chosenCategory);  // Gets current user ID's chosen category to add to their specific workout lists
                    myRef.push().setValue(headerTitle);   // Add item to database
                    myDialog.dismiss();
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
        textClose = (TextView)myDialog.findViewById(R.id.textCloseCategoryDelete);
        btnRemoveCategory = (Button)myDialog.findViewById(R.id.removeCategoryButton);
        spinner = (Spinner)myDialog.findViewById(R.id.choose_categoryDelete);

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(this, R.layout.color_spinner_layout, tempHeaderList);
        itemAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(itemAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenCategory = parent.getItemAtPosition(position).toString();
                if(chosenCategory.equals("Choose category"))
                    categorySelected = false;
                else
                    categorySelected = true;
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
                    newExpandableCategory = new ArrayList<>();
                    database = FirebaseDatabase.getInstance();

                    myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chosenCategory);  // Gets current user ID's chosen category to add to their specific workout lists
                    myRef.removeValue();   // Remove category from database
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
        textClose = (TextView)myDialog.findViewById(R.id.textCloseItemDelete);
        btnRemoveItem = (Button)myDialog.findViewById(R.id.removeItemButton);
        spinner = (Spinner)myDialog.findViewById(R.id.choose_categoryDeleteItemPopup);

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(this, R.layout.color_spinner_layout, tempHeaderList);
        itemAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(itemAdapter);

        spinnerItems = (Spinner) myDialog.findViewById(R.id.choose_ItemDelete);
        spinnerItems.setEnabled(false);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chosenCategory = parent.getItemAtPosition(position).toString();
                    if (chosenCategory.equals("Choose category"))
                        categorySelected = false;
                    else
                        categorySelected = true;

                    if(categorySelected)
                        setItemSpinner();
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
        /*
        Log.i(TAG, "Category: " + chosenCategory);
        spinnerItems.setEnabled(true);
        ArrayList<String> tempHeaderListItems = new ArrayList<>();

        ArrayAdapter<String> itemAdapter2 = new ArrayAdapter<>(this, R.layout.color_spinner_layout, tempHeaderListItems);
        itemAdapter2.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinnerItems.setAdapter(itemAdapter2);
        */
        spinnerItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenItem = parent.getItemAtPosition(position).toString();
                if (chosenItem.equals("Choose item"))
                    itemSelected = false;
                else
                    itemSelected = true;
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
                    newExpandableCategory = new ArrayList<>();
                    database = FirebaseDatabase.getInstance();

                    myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chosenCategory);  // Gets current user ID's chosen category to add to their specific workout lists
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String item = snapshot.getValue().toString();
                                if (item.equals(chosenItem)) {
                                    myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(chosenCategory).child(snapshot.getKey());
                                    myRef.removeValue();   // Remove item from database
                                }
                            }
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

    public void setItemSpinner()
    {
        //Log.i(TAG, "Category: " + chosenCategory);
        spinnerItems.setEnabled(true);
        ArrayList<String> tempHeaderListItems = new ArrayList<>();

        tempHeaderListItems.addAll(0, listHashMap.get(chosenCategory));
        tempHeaderListItems.add(0, "Choose item");

        ArrayAdapter<String> itemAdapter2 = new ArrayAdapter<>(this, R.layout.color_spinner_layout, tempHeaderListItems);
        itemAdapter2.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinnerItems.setAdapter(itemAdapter2);

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
}
