package org.acmucr.shoppinglist;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 20;

    private ListView lvShoppingList;
    private FloatingActionButton btAddItem;

    private List<String> itemNames;
    private ArrayAdapter<String> itemsAdapter;

    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the variables to the view elements
        lvShoppingList = findViewById(R.id.rvShoppingList);
        btAddItem = findViewById(R.id.btAddItem);

        // OnClick listener for the button to transition to AddItemActivity
        btAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemActivityIntent = new Intent(getApplicationContext(), AddItemActivity.class);
                startActivityForResult(addItemActivityIntent, REQUEST_CODE);
            }
        });

        // Initialize the list of names
        itemNames = new ArrayList<>();

        // Create the adapter to send the list of names to the list view
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemNames);

        // Set the list view's adapter
        lvShoppingList.setAdapter(itemsAdapter);

        // Remove an item from the list if the user long presses on it
        lvShoppingList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Remove the item from the Firebase database
                final String clickedItem = itemNames.get(position);
                dbReference.orderByValue().equalTo(clickedItem).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot d : dataSnapshot.getChildren()) {
                            if(d.getValue().toString().equals(clickedItem)) {
                                dbReference.child(d.getKey()).removeValue();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting the items from the database failed
                    }
                });

                // Remove the clicked item from the ListView and notify the adapter
                itemsAdapter.remove(itemNames.get(position));
                itemsAdapter.notifyDataSetChanged();
                return false;
            }
        });

        lvShoppingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Get some " + itemNames.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        // Get a reference to the root of the Firebase database
        dbReference = FirebaseDatabase.getInstance().getReference();

        // Pull the items from the Firebase database and load them into the ListView
        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String item = d.getValue(String.class);
                    itemNames.add(item);
                }
                itemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting the items from the database failed
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // If returning from AddItemActivity, get the text the user entered
            String itemName = data.getStringExtra("ITEM_NAME");

            // Add the item to the list and notify the adapter
            itemNames.add(itemName);
            itemsAdapter.notifyDataSetChanged();

            // Add the item to the Firebase database
            dbReference.push().setValue(itemName);
        }
    }
}