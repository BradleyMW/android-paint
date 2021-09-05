/**
 * OnTouchCoordinates.java
 *
 * Written by Bradley Wersterfer for CS4301.002, Assignment 5, starting April 16, 2021.
 * NetID: bmw170030
 *
 * This onTouchListener is meant to be added to a FollowView object. It will detect three types of
 * touch events for that view: on finger down (start a new line), on finger move (continue the current
 * line), and on finger up (do nothing). The former two cases will get the X and Y coordinates of this
 * touch event as a new CoordinatePair and then pass the info along to the actual FollowView object
 * along with a boolean representing whether this point is the start of a new line or not.
 */

package com.bmw170030.fingerpaints.Views;

import android.view.MotionEvent;
import android.view.View;

import com.bmw170030.fingerpaints.DataTypes.CoordinatePair;

public class OnTouchCoordinates implements View.OnTouchListener {

    //! The minimum distance for an ACTION_MOVE event to register
    public float min_distance;

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to. Should be a FollowView.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        FollowView fv = (FollowView) v;
        switch(event.getAction())
        {
            // Set the acquired coordinates and then begin refreshing the screen
            case(MotionEvent.ACTION_DOWN):
                fv.addPt(new CoordinatePair(event.getX(), event.getY()), true);
                break;

            // View was already being touched, but mouse is moving
            case(MotionEvent.ACTION_MOVE):
                fv.addPt(new CoordinatePair(event.getX(), event.getY()), false);
                break;

            // User is not touching screen; stop displaying the circle
            case (MotionEvent.ACTION_UP):
                break;
        }

        return true;
    }
}
