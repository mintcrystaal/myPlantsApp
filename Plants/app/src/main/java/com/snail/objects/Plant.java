package com.snail.objects;

public class Plant {
    private long id;
    private String name;
    private String picture;
    private int temperature;
    private int light;
    private int watering;
    private int humidity;
    private int fertilize;
    private int mine;
    private String info;


    public Plant (long id, String name, String picture, int temperature, int light, int watering, int humidity, int fertilize, int mine, String info) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.temperature = temperature;
        this.light = light;
        this.watering = watering;
        this.humidity = humidity;
        this.fertilize = fertilize;
        this.mine = mine;
        this.info = info;
    }

    public void addMine() {
        mine++;
    }

    public void removeMine() {
        mine--;
    }

    public void setMine(int mine) {
        this.mine = mine;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getLight() {
        return light;
    }

    public int getWatering() {
        return watering;
    }

    public int getFertilize() {
        return fertilize;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getMine() {
        return mine;
    }

    public String getInfo() {
        return info;
    }
}
