/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.tools;

import java.net.Socket;

/**
 *
 * @author Admin
 */
public class Client {
    Socket socket;
    int id;
    boolean inGame;

    public Client() {
    }

    public Client(Socket socket) {
        this.socket = socket;
        this.id = 0;
        inGame = false;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
}
