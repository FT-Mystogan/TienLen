package main.client;

import main.tools.Card;
import main.tools.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyPanel extends JPanel {
    private Player player;
    private List<Card> playedCards;

    public MyPanel() {
        playedCards = new ArrayList<>();
    }

    public MyPanel(BorderLayout layout) {
        super(layout);
        playedCards = new ArrayList<>();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public void setPlayedCards(List<Card> playedCards) {
        this.playedCards = playedCards;
    }
}
