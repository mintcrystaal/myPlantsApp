package com.snail.objects;

public class PlantUpdates {
    private long id;
    private long lastWater;
    private long lastFert;

    public PlantUpdates(long id, long lastWater, long lastFert) {
        this.id = id;
        this.lastWater = lastWater;
        this.lastFert = lastFert;
    }

    public long getId() {
        return id;
    }

    public long getLastWater() {
        return lastWater;
    }

    public long getLastFert() {
        return lastFert;
    }
}
