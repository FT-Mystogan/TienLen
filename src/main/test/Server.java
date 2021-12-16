package main.test;

import main.tools.GameManager;

import java.io.IOException;

public class Server {

    public static void main(String[] args) throws IOException {
        GameManager gameManager = new GameManager();
        gameManager.run();
    }
}
