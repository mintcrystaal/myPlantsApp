package com.snail.objects;

public class EventInfo {
    private long id;
    private long connectedWithPlant;
    private String name;

    public EventInfo (String name, long id, long connectedWithPlant) {
        this.name = name;
        this.id = id;
        this.connectedWithPlant = connectedWithPlant;
    }



    public long getId() {
        return id;
    }

    public String getName() {return name;}

    public long getConnectedWithPlant() {return connectedWithPlant;}
}
