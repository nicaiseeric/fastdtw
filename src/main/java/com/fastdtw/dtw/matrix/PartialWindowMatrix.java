/*
 * PartialWindowMatrix.java   Jul 14, 2004
 *
 * Copyright (c) 2004 Stan Salvador
 * stansalvador@hotmail.com
 */

package com.fastdtw.dtw.matrix;

import com.fastdtw.dtw.window.SearchWindow;

public final class PartialWindowMatrix implements CostMatrix {

    private static final double OUT_OF_WINDOW_VALUE = Double.POSITIVE_INFINITY;
    private double[] lastCol;
    private double[] currCol;
    private int currColIndex;
    private int minLastRow;
    private int minCurrRow;
    private final SearchWindow window;

    public PartialWindowMatrix(SearchWindow searchWindow) {
        window = searchWindow;

        if (window.maxI() > 0) {
            currCol = new double[window.maxJforI(1) - window.minJforI(1) + 1];
            currColIndex = 1;
            minLastRow = window.minJforI(currColIndex - 1);
        } else
            currColIndex = 0;

        minCurrRow = window.minJforI(currColIndex);
        lastCol = new double[window.maxJforI(0) - window.minJforI(0) + 1];
    }

    @Override
    public void put(int col, int row, double value) {
        if ((row < window.minJforI(col)) || (row > window.maxJforI(col))) {
            throw new RuntimeException("CostMatrix is filled in a cell (col=" + col + ", row="
                    + row + ") that is not in the " + "search window");
        } else {
            if (col == currColIndex)
                currCol[row - minCurrRow] = value;
            else if (col == currColIndex - 1)
                lastCol[row - minLastRow] = value;
            else if (col == currColIndex + 1) {
                lastCol = currCol;
                minLastRow = minCurrRow;
                currColIndex++;
                currCol = new double[window.maxJforI(col) - window.minJforI(col) + 1];
                minCurrRow = window.minJforI(col);

                currCol[row - minCurrRow] = value;
            } else
                throw new RuntimeException(
                        "A PartialWindowMatrix can only fill in 2 adjacentcolumns at a time");
        }
    }

    @Override
    public double get(int col, int row) {
        if ((row < window.minJforI(col)) || (row > window.maxJforI(col)))
            return OUT_OF_WINDOW_VALUE;
        else {
            if (col == currColIndex)
                return currCol[row - minCurrRow];
            else if (col == currColIndex - 1)
                return lastCol[row - minLastRow];
            else
                return OUT_OF_WINDOW_VALUE;
        }
    }

    @Override
    public int size() {
        return lastCol.length + currCol.length;
    }

    public int windowSize() {
        return window.size();
    }

}
