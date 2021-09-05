/**
 * LineStack.java
 *
 * Written by Bradley Wersterfer for CS4301.002, Assignment 5, starting April 16, 2021.
 * NetID: bmw170030
 *
 * This is a wrapper class for a stack of lines to ensure that they are used as intended. It provides
 * an interface to the underlying stack methods such as size and peek, along with ensuring that the
 * user will add or remove new methods through the supported push and pop.
 */

package com.bmw170030.fingerpaints.DataTypes;

import java.io.Serializable;
import java.util.Stack;

public class LineStack implements Serializable {
    private Stack<Line> lines;

    /**
     * Default public constructor.
     */
    public LineStack() {
        lines = new Stack<Line>();
    }

    /**
     * Method for accessing all of the underlying lines.
     * @return The entire stack of lines.
     */
    public Stack<Line> getLines() {
        return lines;
    }

    /**
     * Find the number of lines in this object.
     * @return The size of the underlying stack.
     */
    public int size() {
        return lines.size();
    }

    /**
     * Wrapper method for getting the top element from the stack.
     * @return The most recently drawn line.
     */
    public Line peek() {
        if(lines.size() > 0)
            return lines.peek();
        else
            return null;
    }

    /**
     * Add a given line object to the stack.
     * @param l The line to add.
     */
    public void add(Line l) {
        lines.push(l);
    }

    /**
     * Remove a given line from the stack.
     */
    public Line remove() {
        return lines.pop();
    }
}
