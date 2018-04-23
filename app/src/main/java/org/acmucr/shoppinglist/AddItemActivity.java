package org.acmucr.shoppinglist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddItemActivity extends AppCompatActivity {

    private EditText etItem;
    private Button btDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Bind the variables to the view elements
        etItem = findViewById(R.id.etItem);
        btDone = findViewById(R.id.btDone);

        // Add the OnClick listener to the button
        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text the user entered
                String itemName = etItem.getText().toString();

                // Pass the information and transition back to the MainActivity
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainActivityIntent.putExtra("ITEM_NAME", itemName);
                setResult(RESULT_OK, mainActivityIntent);
                finish();
            }
        });
    }
}