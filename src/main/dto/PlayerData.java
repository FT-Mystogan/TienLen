package main.dto;

import java.io.Serializable;

public class PlayerData implements Serializable {
    private static final long serialVersionUID = 110L;
    private int playerId;
    private int id;
    private String playerName;
    private String username;
    private String password;
    private float money;
    private String isLogin;
    private int idRoom;
    public PlayerData() {
    }

    public PlayerData(String playerName) {
        this.playerName = playerName;
    }

    public PlayerData(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }
    
    
    
}
