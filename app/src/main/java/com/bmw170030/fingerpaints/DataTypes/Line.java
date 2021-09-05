/**
 * Line.java
 *
 * Written by Bradley Wersterfer for CS4301.002, Assignment 5, starting April 16, 2021.
 * NetID: bmw170030
 *
 * This class represents a line as a sequential series of CoordinatePair points. Each line can also
 * have its own radius and paint types, which can be derived to determine how the line should be drawn.
 * The getPts() method will return the full list of CoordinatePair objects.
 */

package com.bmw170030.fingerpaints.DataTypes;

import android.graphics.Paint;

import java.io.Serializable;
import java.util.ArrayList;

public class Line implements Serializable {

    // Each line is represented by a list of coordinate points
    private ArrayList<CoordinatePair> pts;

    // The paint type (with color and thickness) along with the underlying radius
    private final float radius;
    private final int color;

    // Public constructor to get the radius and paint values
    public Line(float radius, int color) {
        // Initialize the interior member variables
        pts = new ArrayList<>();
        this.radius = radius;
        this.color = color;
    }

    // Add a given point to this line with its current style
    public void addPt(CoordinatePair pt) {
        pts.add(pt);
    }

    // Return all of the points in this line
    public ArrayList<CoordinatePair> getPts() {
        return pts;
    }

    // Method to overwrite an entire line if necessary
    public void setPts(ArrayList<CoordinatePair> pts) {
        this.pts = pts;
    }

    // Getters for the radius and color
    public float getRadius() {
        return radius;
    }

    public int getColor() {
        return color;
    }

    /**
     * This getter will construct a paint object representing this line using the
     * stored radius and color values for the line.
     * @return A Paint object with color and stroke width set
     */
    public Paint getPaint() {
        // Construct the paint object from this color and radius
        Paint pLine = new Paint();
        pLine.setColor(color);
        pLine.setStrokeWidth(radius * 2);     // diameter = radius * 2
        return pLine;
    }
}
