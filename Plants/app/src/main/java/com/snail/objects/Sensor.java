package com.snail.objects;

public class Sensor {
    private long id;
    private long plant;
    private int type; // 0 - temperature; 1 - humidity;
    private int value;

    public Sensor(long id, long plant, int type, int value) {
        this.id = id;
        this.plant = plant;
        this.type = type;
        this.value = value;
    }

    public long getId() {return id;}

    public int getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public long getPlant() {
        return plant;
    }

    public void setValue(int value) {
        this.value = value;
    }
}