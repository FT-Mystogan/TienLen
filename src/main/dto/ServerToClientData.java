package main.dto;

import main.tools.Card;
import main.tools.Player;

import java.io.Serializable;
import java.util.List;

public class ServerToClientData implements Serializable {
    private static final long serialVersionUID = 103L;

    private List<Player> players;
    private List<Card> currentSet;
    private boolean hasError;
    private String message;
    private int whoseTurn;
    private boolean isGameOver;

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Card> getCurrentSet() {
        return currentSet;
    }

    public void setCurrentSet(List<Card> currentSet) {
        this.currentSet = currentSet;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getWhoseTurn() {
        return whoseTurn;
    }

    public void setWhoseTurn(int whoseTurn) {
        this.whoseTurn = whoseTurn;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }
}
