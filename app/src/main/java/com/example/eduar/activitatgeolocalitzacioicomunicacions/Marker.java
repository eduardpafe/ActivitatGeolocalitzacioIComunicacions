package com.example.eduar.activitatgeolocalitzacioicomunicacions;

/**
 * Created by eduar on 09/03/2018.
 */

public class Marker {
    private String city, name;
    private int id;
    private double latitude, longitude;

    public Marker(){

    }

    public Marker(String city, String name, int id, double latitude, double longitude) {
        this.city = city;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
