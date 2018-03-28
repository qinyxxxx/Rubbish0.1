package com.example.administrator.rubbish01.activity;

/**
 * Created by Think on 2018/3/27.
 */

public class RubbishData {
    private int Id;
    private float Longitude;
    private float Latitude;
    private int Usage;
    public int getId(){
        return Id;
    }
    public void setId(int id){
        this.Id=id;
    }
    public float getLongitude(){
        return Longitude;
    }
    public void setLongitude(int longitude){
        this.Longitude=longitude;
    }
    public float getLatitude(){
        return Latitude;
    }
    public void setLatitude(int latitude){
        this.Latitude=latitude;
    }
    public int getUsage(){
        return Usage;
    }
    public void setUsage(int usage){
        this.Usage=usage;
    }
    public RubbishData(int id, float longitude, float latitude, int usage){
        super();
        this.Id=id;
        this.Longitude=longitude;
        this.Latitude=latitude;
        this.Usage=usage;
    }

}
