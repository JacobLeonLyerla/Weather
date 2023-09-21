package com.example.jpmc.utils;

public class DateUtility {

    /**
     * Checks if the provided timestamp is more than 1 minutes old.
     *
     * @param timestamp the timestamp to check.
     * @return true if the timestamp is more than 1 minutes old, false otherwise.
     * If this app were to be used I would set this to 15-30 minutes 1 min is for testing.
     */
    public static boolean isDataStale(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long oneMinuteInMillis = 60 * 1000;
        return currentTime - timestamp > oneMinuteInMillis;
    }
}