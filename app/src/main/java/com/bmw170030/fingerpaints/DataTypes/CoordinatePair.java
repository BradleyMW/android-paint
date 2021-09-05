/**
 * CoordinatePair.java
 *
 * Written by Bradley Wersterfer for CS4301.002, Assignment 5, starting April 16, 2021.
 * NetID: bmw170030
 *
 * This is a utility class that simply tracks x and y coordinates. They are properly encapsulated in
 * private instance variables with getter and setter methods to ensure that data is protected and
 * only accessed as intended.
 */

package com.bmw170030.fingerpaints.DataTypes;

import java.io.Serializable;

public class CoordinatePair implements Serializable {

    //! Private member variables representing this coordinate location
    private float x;
    private float y;

    /**
     * Default public constructor to create a (0, 0) coordinate pair.
     */
    public CoordinatePair() {
        // If no coordinates were provided, assume (0, 0)
        this(0, 0);
    }

    /**
     * Main public constructor for generating a set of coordinates.
     * @param x X-axis location (float)
     * @param y Y-axis location (float)
     */
    public CoordinatePair(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Getters and setters for x and y
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
