package fr.kaname.kanaeventmanager.object;

public class eventObject {

    String eventName;
    String broadcast;
    String displayName;
    Double locX;
    Double locY;
    Double locZ;

    public eventObject(String eventName, String broadcast, Double locX, Double locY, Double locZ, String displayName) {

        this.eventName = eventName;
        this.broadcast = broadcast;
        this.displayName = displayName;
        this.locX = locX;
        this.locY = locY;
        this.locZ = locZ;
    }

    public String getEventName() {
        return eventName;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public Double getLocX() {
        return locX;
    }

    public Double getLocY() {
        return locY;
    }

    public Double getLocZ() {
        return locZ;
    }

    public String getDisplayName() {
        return displayName;
    }
}
