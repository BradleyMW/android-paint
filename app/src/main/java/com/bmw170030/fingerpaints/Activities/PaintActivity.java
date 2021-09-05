/**
 * PaintActivity.java
 *
 * Written by Bradley Wersterfer for CS4301.002, Assignment 5, starting April 16, 2021.
 * Modified for Assignment 6 to support database loading and saving, starting April 29, 2021.
 * NetID: bmw170030
 *
 * This is the main activity for the FingerPaints program. It defines several UI elements such as
 * a custom FollowView that will track points the user touches as lines (each time they lift their
 * finger, it results in a new line being started). Furthermore, there are four buttons with different
 * color options and a RangeSlider to change the thickness of the line. Each of these values will be
 * propagated to the FollowView object and applied to all future lines that are drawn (until they are
 * overridden, of course). Each line will store these attributes for recreation in the future.
 *
 * This activity also implements a custom Toolbar to add features to either go to a SaveActivity or
 * a LoadActivity for further functionality. Each of these classes supports database storage, and
 * once they are completed they can return a result to this activity (or simply maintain the existing
 * drawing if a new one was not selected).
 */

package com.bmw170030.fingerpaints.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import com.bmw170030.fingerpaints.DataTypes.LineStack;
import com.bmw170030.fingerpaints.R;
import com.bmw170030.fingerpaints.Views.FollowView;
import com.bmw170030.fingerpaints.Views.OnTouchCoordinates;
import com.google.android.material.slider.RangeSlider;

public class PaintActivity extends AppCompatActivity implements RangeSlider.OnChangeListener {

    //! UI elements for handling the view to draw on
    FollowView follow;
    OnTouchCoordinates draw_handler;

    //! The size slider and max and minimum ranges
    RangeSlider slider;
    private final float MIN_RADIUS = 3;
    private final float MAX_RADIUS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar bar = findViewById(R.id.main_toolbar);
        setSupportActionBar(bar);

        // Find the custom view to draw lines on and set up its listener for grabbing touch coordinates
        follow = (FollowView) findViewById(R.id.canvas);
        follow.setRadius(MIN_RADIUS);
        onBlackClick(null);

        draw_handler = new OnTouchCoordinates();
        follow.setOnTouchListener(draw_handler);

        // Find the slider and update it so that the value can be maintained
        slider = (RangeSlider) findViewById(R.id.size_slider);
        slider.addOnChangeListener(this);
    }
    
    /**
     * onClick method for when the user travels to the loading screen. Will pass along the current
     * drawing in case it needs to be repopulated when the user cancels.
     */
    public void goToLoadActivity() {
        Intent load = new Intent(this, LoadActivity.class);
        load.setAction(Intent.ACTION_GET_CONTENT);
        load.putExtra("drawing", follow.getDrawing());
        startActivityForResult(load, LoadActivity.IDENTIFIER);
    }

    /**
     * onClick method for when the user travels to the saving screen. Must pass along the current
     * drawing so that it can be stored.
     */
    public void goToSaveActivity() {
        Intent save = new Intent(this, SaveActivity.class);
        save.putExtra("drawing", follow.getDrawing());
        startActivityForResult(save, SaveActivity.IDENTIFIER);
    }

    /**
     * This is called when a startActivityForResult() call is properly finished. This must have been
     * the result of either the loading or saving screens, each of which will either return a new image
     * that has been loaded or the one that was passed in originally so that the user can resume.
     *
     * @param requestCode The type of activity that had been invoked
     * @param resultCode Whether the activity executed successfully
     * @param data An intent containing the data from the activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (SaveActivity.IDENTIFIER): {
                // If the activity was simply saving the drawing, there is nothing new to load
                break;
            }
            case (LoadActivity.IDENTIFIER): {
                // If the activity was loading a new one, check to see if it wasn't canceled
                if(resultCode == Activity.RESULT_OK) {
                    LineStack drawing = (LineStack) data.getSerializableExtra("drawing");
                    follow.setDrawing(drawing);
                }
                break;
            }
        }
    }

    /**
     * This is called when the toolbar menu is created to populate it with menu items.
     * @param menu The menu being inflated.
     * @return Whether this attempt was successful or not.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add menu items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar_buttons, menu);
        return true;
    }

    /**
     * Override the menu item event handler to determine which function should be invoked.
     * @param mi The item in the toolbar that was clicked.
     * @return Whether this menu item event was recognized or not.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        switch(mi.getItemId()) {
            case R.id.undo: {
                onClickUndo(null);
                break;
            }
            case R.id.save: {
                goToSaveActivity();
                break;
            }
            case R.id.load: {
                goToLoadActivity();
                break;
            }
            default:
                return false;
        }
        return true;
    }

    /**
     * onClick method for when the red paint is selected.
     * @param view The clicked button.
     */
    public void onRedClick(View view) {
        follow.setColor(Color.RED);
    }

    /**
     * onClick method for when the green paint is selected.
     * @param view The clicked button.
     */
    public void onGreenClick(View view) {
        follow.setColor(Color.GREEN);
    }

    /**
     * onClick method for when the blue paint is selected.
     * @param view The clicked button.
     */
    public void onBlueClick(View view) {
        follow.setColor(Color.BLUE);
    }

    /**
     * onClick method for when the black paint is selected.
     * @param view The clicked button.
     */
    public void onBlackClick(View view) {
        follow.setColor(Color.BLACK);
    }

    /**
     * onClick method for removing the most recently added line.
     * @param view The clicked button. Can be null.
     */
    public void onClickUndo(View view) {
        follow.undo();
    }

    /**
     * Called when the value of the slider changes. Simply updates the FollowView radius value
     * for making new drawings.
     *
     * @param slider The slider UI element
     * @param value The new value
     * @param fromUser Whether this change came from the user or not
     */
    @Override
    public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
        follow.setRadius(value);
    }
}