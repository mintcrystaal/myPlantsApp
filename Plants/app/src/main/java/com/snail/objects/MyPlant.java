package com.snail.objects;

public class MyPlant {
    private long id;
    private long idAll;
    private int feels;

    public MyPlant(long id, long idAll, int feels) {
        this.id = id;
        this.idAll = idAll;
        this.feels = feels;
    }

    public long getId() {
        return id;
    }

    public long getIdAll() {
        return idAll;
    }

    public int getFeels() {
        return feels;
    }

    public void setFeels(int feels) {
        this.feels = feels;
    }
}
