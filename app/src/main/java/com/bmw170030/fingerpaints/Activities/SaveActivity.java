/**
 * SaveActivity.java
 *
 * Written by Bradley Wersterfer for CS4301.002, Assignment 6, starting April 29, 2021.
 * NetID: bmw170030
 *
 * This activity maintains save functionality for writing LineStack drawings to the database. Users
 * can either enter a new title in the InputEditText or click on an item from the list of currently
 * saved drawings to overwrite that one. Once a user is done saving their drawing (which can be done
 * multiple times), the cancel or back buttons will take them back to the main PaintActivity.
 */

package com.bmw170030.fingerpaints.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bmw170030.fingerpaints.DataTypes.LineStack;
import com.bmw170030.fingerpaints.Database.DBContract;
import com.bmw170030.fingerpaints.Database.DatabaseIO;
import com.bmw170030.fingerpaints.Database.DrawingDBHelper;
import com.bmw170030.fingerpaints.R;
import com.bmw170030.fingerpaints.TextViewAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class SaveActivity extends AppCompatActivity implements TextViewAdapter.ClickListener {

    //! Unique identifier for this sub-activity
    public static final int IDENTIFIER = 3;

    //! For managing access to the database
    DrawingDBHelper db_helper;
    DatabaseIO manager;

    //! The underlying names of stored drawings
    ArrayList<String> drawings;
    LineStack new_drawing;

    // UI elements for displaying and selecting drawing titles
    TextViewAdapter adapter;
    TextInputEditText name_field;
    CoordinatorLayout main_view;
    TextView err_text;
    RecyclerView rv;
    Button save_drawing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coordinator_layout_save);

        // Create or open the drawings database and pull in the passed drawing
        db_helper = new DrawingDBHelper(this);
        new_drawing = (LineStack) getIntent().getSerializableExtra("drawing");

        // Get the database via the manager and then list out all stored drawing names
        manager = new DatabaseIO(db_helper.getWritableDatabase());
        drawings = manager.getNames();

        // Find UI elements from this scene
        name_field = (TextInputEditText) findViewById(R.id.enter_name);
        save_drawing = (Button) findViewById(R.id.save_btn);
        main_view = (CoordinatorLayout) findViewById(R.id.coordinator_layout_save);
        err_text = (TextView) findViewById(R.id.err_text);
        rv = (RecyclerView) findViewById(R.id.text_list);

        // Remove the error text about the RecyclerView being empty initially
        err_text.setVisibility(View.GONE);
        initializeRecyclerView();
    }

    /**
     * Attempt to save the drawing that was passed to this activity and stored internally under the
     * given name. This could either be because the user selected an item from the list or because
     * they filled in the textbox and hit the save button to create a new entry.
     *
     * @param name The String name to save with the drawing.
     * @param replace Whether this drawing is intended to override an existing one or not.
     * @return
     */
    public boolean saveDrawing(String name, boolean replace) {
        if(name == null || name.length() > DBContract.Drawing.MAX_NAME_LENGTH || name.length() <= 0) {
            // If the name was invalid, notify the user that the save failed
            Snackbar snackbar = Snackbar.make(main_view, "The drawing name is invalid!", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
        else {
            // If the user is overwriting a current quiz, then the name is not added
            if(!replace) {
                drawings.add(name);
                adapter.notifyDataSetChanged();
            }
            return manager.addDrawing(new_drawing, name);
        }
    }

    /**
     * onClick method for when the user attempts to save the drawing.
     * @param view The clicked button.
     */
    public void onClickSave(View view) {
        // Put that drawing in the database.
        String name = name_field.getText().toString();
        saveDrawing(name, false);
    }

    /**
     * onClick method for when the user hits the Cancel button to return to the main activity.
     * @param view The clicked button.
     */
    public void onClickCancel(View view) {
        // If the user canceled their decision, then just end the activity.
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * This updates the RecyclerView by creating a new TextViewAdapter with the list of strings as
     * the stored items in the list. The adapter uses this MainActivity class as its click listener
     * and LinearLayoutManager, and also disables the SAVE button until the user selects an item
     * from the newly updated RecyclerView.
     */
    public void initializeRecyclerView()
    {
        // If no drawings were found, then display an error message to the user notifying them such.
        if(drawings == null || drawings.size() == 0)
            err_text.setVisibility(View.VISIBLE);

        // Set the recycler view to display only those drawing names from the internal list.
        adapter = new TextViewAdapter(this, drawings);
        adapter.setClickListener(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Handles click events from within the adapter by implementing that interface. When a row of the
     * Recycler View is clicked, the corresponding drawing must be chosen and the button for loading
     * it will be enabled.
     *
     * @param view The clicked list element.
     * @param display_name The name of the drawing that was just clicked (text value of View in list).
     * @param position The index of the clicked item.
     */
    @Override
    public void onItemClick(View view, String display_name, int position)
    {
        // Pass forward the drawing name and immediately attempt to save it by overwriting that
        // drawing in the database.
        saveDrawing(display_name, true);
    }
}