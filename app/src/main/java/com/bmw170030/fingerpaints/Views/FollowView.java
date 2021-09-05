/**
 * FollowView.java
 *
 * Written by Bradley Wersterfer for CS4301.002, Assignment 5, starting April 16, 2021.
 * NetID: bmw170030
 *
 * This is custom View that internally stores a LineStack object. When its onDraw method is invoked,
 * this will result in the entire stack of lines (which are each an ArrayList of data points) being
 * drawn to the canvas as individual lines. Upon creation, each line will also take the FollowView's
 * current radius and color values, which are set by the public update methods from the parent activity.
 *
 * Finally, support for popping the top line off of the stack is supported, which will undo the last
 * drawn line from the user.
 */

package com.bmw170030.fingerpaints.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bmw170030.fingerpaints.DataTypes.CoordinatePair;
import com.bmw170030.fingerpaints.DataTypes.Line;
import com.bmw170030.fingerpaints.DataTypes.LineStack;

public class FollowView extends androidx.appcompat.widget.AppCompatImageView {

    // Attributes for the next line to draw
    private int color;
    private float radius;

    // Keep track of all of the lines that are drawn so that they can be undone in reverse order
    LineStack lines = new LineStack();

    // Constructor taking in a context
    public FollowView(@NonNull Context context) {
        super(context);
    }

    // Constructor taking in a context and set of attributes
    public FollowView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * When the canvas is invalid, the entire drawing surface must be updated. Go through every line
     * in the LineStack, and for each one, go through every data point to draw lines between the
     * coordinate pairs.
     *
     * @param canvas The object to draw this view's lines on.
     */
    @Override
    public void onDraw(Canvas canvas) {
        // Go through every line in this object
        for(Line l : lines.getLines()) {

            // For each line, draw every point
            for(int ix = 0; ix < l.getPts().size(); ix++) {
                CoordinatePair cur = l.getPts().get(ix);
                if(ix == 0) {
                    // If this is just the first point in the line, create a starting circle
                    canvas.drawCircle(cur.getX(), cur.getY(), l.getRadius(), l.getPaint());
                }
                else {
                    // If there is at least one point before this one, draw a full line
                    CoordinatePair prev = l.getPts().get(ix - 1);
                    canvas.drawLine(prev.getX(), prev.getY(), cur.getX(), cur.getY(), l.getPaint());
                }
            }
        }
    }

    /**
     * Either add a given coordinate pair to the current line or start a new line with that point.
     * This will use this view's own paint color and radius, so they should be set ahead of time.
     *
     * @param cp The x-y coordinate pair to draw the circle point at.
     * @param nextLine Whether this point is the start of a new line or not.
     */
    public void addPt(CoordinatePair cp, boolean nextLine) {
        if(nextLine) {
            // If this is a new line, start another one on the stack.
            // The current radius and circle will be stored with this line for reconstructing it.
            Line nl = new Line(radius, color);
            nl.addPt(cp);
            lines.add(nl);
        }
        else {
            // If this new point is a continuation of an old one, add it to the stack.
            lines.peek().addPt(cp);
        }

        // Set this view to refresh itself by calling onDraw in the future.
        this.invalidate();
    }

    /**
     * Utility function to pop off the top line from the stack (the one that was most recently added).
     * This will also signal to the View that the canvas is invalid and should be redrawn.
     */
    public void undo() {
        if(lines.size() > 0) {
            lines.remove();
        }
        this.invalidate();
    }

    /**
     * Public getter for retrieving the underlying data storage for the current drawing.
     * @return A LineStack object tracking lines and their paints and thickness.
     */
    public LineStack getDrawing() {
        return lines;
    }

    /**
     * Public setter to override the current drawing with a loaded one. Will also redraw screen.
     * @param ls A LineStack object representing another drawing.
     */
    public void setDrawing(LineStack ls) {
        this.lines = ls;
        this.invalidate();
    }

    /**
     * Update the current radius size for all future drawn points.
     * @param radius The width of circles to draw in this line.
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * Update the current paint color for all future drawn points.
     * @param color The color of this line.
     */
    public void setColor(int color) {
        this.color = color;
    }
}
