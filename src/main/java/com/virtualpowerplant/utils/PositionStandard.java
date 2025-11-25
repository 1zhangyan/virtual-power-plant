package com.virtualpowerplant.utils;

public class PositionStandard {

    public static final double GRID_SIZE = 0.25;
    public static double positionStandard(double position) {
        return Math.round(position / GRID_SIZE) * GRID_SIZE;
    }
}
