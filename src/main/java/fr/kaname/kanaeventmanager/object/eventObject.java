package fr.kaname.kanaeventmanager.object;

public class eventObject {

    private int ID;
    private final String eventName;
    private final String broadcast;
    private final String displayName;
    private final Double locX;
    private final Double locY;
    private final Double locZ;

    public eventObject(int id, String eventName, String broadcast, Double locX, Double locY, Double locZ, String displayName) {

        this.ID = id;
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

    public int getID() {
        return this.ID;
    }
}
