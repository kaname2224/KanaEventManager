package fr.kaname.kanaeventmanager.object;

import java.sql.Date;
import java.sql.Timestamp;

public class logObject {

    private final int ID;
    private final int eventID;
    private final String organizer;
    private final Date time;
    private final boolean isBeta;

    public logObject(int id, int eventID, String organizer, Date time, boolean isBeta) {

        this.ID = id;
        this.eventID = eventID;
        this.organizer = organizer;
        this.time = time;
        this.isBeta = isBeta;
    }

    public int getID() {
        return ID;
    }

    public int getEventID() {
        return eventID;
    }

    public String getOrganizer() {
        return organizer;
    }

    public Date getTime() {
        return time;
    }

    public boolean isBeta() {
        return isBeta;
    }
}
