package com.virtualpowerplant.utils;

public class PositionStandard {

    public static final double GRID_SIZE = 0.25;
    public static double positionStandard(Double position) {
        if (null == position) {
            return 0.0;
        }
        return Math.round(position / GRID_SIZE) * GRID_SIZE;
    }

    public static String positionStandardStr(Double position) {
        if (null == position) {
            return "0.0";
        }
        return String.valueOf(Math.round(position / GRID_SIZE) * GRID_SIZE);
    }
}
