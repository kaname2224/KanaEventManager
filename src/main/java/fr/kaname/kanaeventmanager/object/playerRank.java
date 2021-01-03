package fr.kaname.kanaeventmanager.object;

import java.util.UUID;

public class playerRank {
    private int score;
    private UUID uuid;
    private int classement;

    public playerRank(int score, UUID uuid, int classement) {
        this.score = score;
        this.uuid = uuid;
        this.classement = classement;
    }

    public int getScore() {
        return this.score;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public int getClassement() {
        return this.classement;
    }
}
