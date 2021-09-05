/**
 * LoadActivity.java
 *
 * Written by Bradley Wersterfer for CS4301.002, Assignment 6, starting April 29, 2021.
 * NetID: bmw170030
 *
 * This class manages the loading functionality for pulling in a drawing from the underlying database.
 * It will display all recorded databases to the user and then allow for them to select and load one,
 * bringing it back to the main PaintActivity for further alterations, or cancel their decision to
 * return back to the main PaintActivity with the existing drawing that they have there.
 */

package com.bmw170030.fingerpaints.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bmw170030.fingerpaints.DataTypes.LineStack;
import com.bmw170030.fingerpaints.Database.DatabaseIO;
import com.bmw170030.fingerpaints.Database.DrawingDBHelper;
import com.bmw170030.fingerpaints.R;
import com.bmw170030.fingerpaints.TextViewAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class LoadActivity extends AppCompatActivity implements TextViewAdapter.ClickListener {

    //! Unique identifier for this subactivity
    public static final int IDENTIFIER = 2;

    //! The underlying names of stored drawings
    ArrayList<String> drawings;
    String drawing_name;

    // For managing access to the database
    DrawingDBHelper db_helper;
    DatabaseIO manager;

    // UI elements for displaying and selecting drawing titles
    TextViewAdapter adapter;
    TextView err_text;
    CoordinatorLayout main_view;
    RecyclerView rv;
    Button load_drawing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coordinator_layout_load);

        // Create or open the drawings database
        db_helper = new DrawingDBHelper(this);

        // Get the database via the manager and then list out all stored drawing names
        manager = new DatabaseIO(db_helper.getReadableDatabase());
        drawings = manager.getNames();

        // Find UI elements from this scene
        err_text = (TextView) findViewById(R.id.err_text);
        rv = (RecyclerView) findViewById(R.id.text_list);
        main_view = (CoordinatorLayout) findViewById(R.id.coordinator_layout_load);
        load_drawing = (Button) findViewById(R.id.load_btn);

        // Set up the recycler view for this activity
        initializeRecyclerView();
    }

    /**
     * This updates the RecyclerView by creating a new TextViewAdapter with the list of strings as
     * the stored items in the list. The adapter uses this MainActivity class as its click listener
     * and LinearLayoutManager, and also disables the LOAD button until the user selects an item
     * from the newly updated RecyclerView.
     */
    public void initializeRecyclerView()
    {
        // If no drawings were found, then display an error message to the user notifying them such.
        if(drawings == null || drawings.size() == 0)
            err_text.setVisibility(View.VISIBLE);
        else
            err_text.setVisibility(View.GONE);

        // Set the recycler view to display only those drawing names from the internal list.
        adapter = new TextViewAdapter(this, drawings);
        adapter.setClickListener(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Also disable the LOAD button so that a selection must be made
        load_drawing.setEnabled(false);
    }

    /**
     * onClick method for when the user attempts to load the selected drawing. This button is only
     * enabled while a selection has been made from the RecyclerView. It will first load the selected
     * drawing from the database and then send it back to the main activity.
     * @param view The clicked button.
     */
    public void onClickLoad(View view) {
        if(drawing_name == null || drawing_name.length() <= 0) {
            // If the name was invalid, notify the user that the save failed
            Snackbar snackbar = Snackbar.make(main_view, "Cannot load that drawing!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else {
            // Reconstruct the drawing from the database
            LineStack drawing = manager.getDrawing(drawing_name);

            // Pass it back to the main activity
            Intent data = new Intent();
            data.putExtra("drawing", drawing);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    /**
     * onClick method for when the user hits the Cancel button to return to the main activity.
     * @param view The clicked button.
     */
    public void onClickCancel(View view) {
        // If the user canceled their decision, then just end the activity
        setResult(RESULT_CANCELED);
        finish();
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
        // Retrieve the drawing file name from the internal HashMap mapping to topics
        drawing_name = display_name;
        load_drawing.setEnabled(true);
    }
}