package com.example.administrator.rubbish01.bean;

/**
 * Created by Rosemary on 2018/3/30.
 */

public class Bin {
    private int id;

    private float usage;
    private double longitude;
    private double latitude;

    public Bin(int id, float usage, double longitude, double latitude) {
        this.id = id;
        this.usage = usage;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getUsage() {
        return usage;
    }

    public void setUsage(float usage) {
        this.usage = usage;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Bin{" +
                "id=" + id +
                ", usage=" + usage +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
