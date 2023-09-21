package com.example.jpmc.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

// Normally I would have the DTO and Entity be Kotlin. I thought I would do one of each for
// the requirement of this needing both Java and Kotlin

@Entity(tableName = "weather")
public class Weather {

    // I decided to have there only be a single entry that can be updated by the user by searching
    // or refreshing, if the user is online this entry will be displayed if the user opens the app
    // and there is data in the table that entry will be displayed until the table is dropped or over written

    @PrimaryKey
    private int id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "temp")
    private double temp;

    @ColumnInfo(name = "humidity")
    private int humidity;

    @ColumnInfo(name = "wind_speed")
    private double windSpeed;

    @NonNull
    @ColumnInfo(name = "description")
    private String description;

    @NonNull
    @ColumnInfo(name = "icon")
    private String icon;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    // No-args constructor for Room
    public Weather() {
        this("", 0.0, 0, 0.0, "", "");
    }

    // Constructor with all parameters, we ignore this for clarity by default room will choose the
    // no-args constructor
    @Ignore
    public Weather(@NonNull String name, double temp, int humidity, double windSpeed, @NonNull String description, @NonNull String icon) {
        this.id = 1;
        this.name = name;
        this.temp = temp;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.description = description;
        this.icon = icon;
        this.timestamp = new Date().getTime();

    }

    // Getters
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public double getTemp() {
        return temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getIcon() {
        return icon;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters

    public void setName(@NonNull String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public void setTemp(double temp) {
        if (temp < -100 || temp > 100) {
            throw new IllegalArgumentException("Temperature should be between -100 and 100");
        }
        this.temp = temp;
    }

    public void setHumidity(int humidity) {
        if (humidity < 0 || humidity > 100) {
            throw new IllegalArgumentException("Humidity should be between 0 and 100");
        }
        this.humidity = humidity;
    }

    public void setWindSpeed(double windSpeed) {
        // Assuming there isn't a logical upper limit for windSpeed.
        if (windSpeed < 0) {
            throw new IllegalArgumentException("Wind speed cannot be negative");
        }
        this.windSpeed = windSpeed;
    }

    public void setDescription(@NonNull String description) {
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.description = description;
    }

    public void setIcon(@NonNull String icon) {
        if (icon.isEmpty()) {
            throw new IllegalArgumentException("Icon cannot be empty");
        }
        this.icon = icon;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
