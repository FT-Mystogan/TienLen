package main.thread;

import main.dto.ServerToClientData;
import main.tools.GameManager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSendingThread extends Thread {

    private final List<Socket> clients;
    private final GameManager gameManager;
    private boolean hasError;
    private String message;

    public ServerSendingThread(List<Socket> clients, GameManager gameManager) {
        this.clients = clients;
        this.gameManager = gameManager;
    }

    public ServerSendingThread(List<Socket> clients, GameManager gameManager, boolean hasError, String message) {
        this.clients = clients;
        this.gameManager = gameManager;
        this.hasError = hasError;
        this.message = message;
    }

    @Override
    public void run() {
        clients.forEach(client -> {
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
                outputStream.writeObject(setSendingDataFromServer());
                outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private ServerToClientData setSendingDataFromServer() {
        ServerToClientData data = new ServerToClientData();
        data.setCurrentSet(gameManager.getPile().getCurrSet());
        data.setPlayers(gameManager.getPlayers());
        data.setWhoseTurn(gameManager.getWhoseTurn());
        data.setGameOver(gameManager.isGameOver());
        data.setHasError(hasError);
        data.setMessage(message);
        return data;
    }
}
