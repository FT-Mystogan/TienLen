/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.tools;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class Room implements Serializable{
    int id;
    float bet;
    List<Player> player;
    List<Socket> outputClient;
    int playerIndex;
    public Room() {
    }

    public int getPlayerIndex() {
        return playerIndex++;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public Room(int id, float bet) {
        this.id = id;
        this.bet = bet;
        playerIndex=0;
        this.player = new ArrayList<>();
        this.outputClient=new ArrayList<>();
    }

    public List<Socket> getOutputClient() {
        return outputClient;
    }

    public void setOutputClient(List<Socket> outputClient) {
        this.outputClient = outputClient;
    }
    public void addPlayer(Player p){
        this.player.add(p);
    }
    public void addClient(Socket s){
        this.outputClient.add(s);
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getBet() {
        return bet;
    }

    public void setBet(float bet) {
        this.bet = bet;
    }

    public List<Player> getPlayer() {
        return player;
    }

    public void setPlayer(List<Player> player) {
        this.player = player;
    }
    
}
