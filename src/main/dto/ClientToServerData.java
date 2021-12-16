package main.dto;

import main.tools.Card;
import main.tools.Player;

import java.io.Serializable;
import java.util.List;

public class ClientToServerData implements Serializable {
    public static final long serialVersionUID = 104L;

    private Player player;
    private boolean isPassing;
    private List<Card> playedSet;
    private int typeSelection;

    public Player getPlayer() {
        return player;
    }

    public boolean isPassing() {
        return isPassing;
    }

    public List<Card> getPlayedSet() {
        return playedSet;
    }

    public int getTypeSelection() {
        return typeSelection;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPassing(boolean passing) {
        isPassing = passing;
    }

    public void setPlayedSet(List<Card> playedSet) {
        this.playedSet = playedSet;
    }

    public void setTypeSelection(int typeSelection) {
        this.typeSelection = typeSelection;
    }
}
