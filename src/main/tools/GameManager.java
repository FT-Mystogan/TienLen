package main.tools;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import main.dto.ClientToServerData;
import main.dto.PlayerData;
import main.thread.ServerSendingThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.constant.Application.PORT;
import main.db.ConnectDB;
import main.db.RoomDB;
import main.db.UserDB;

@SuppressWarnings("java:S106")
public class GameManager implements Serializable {

    private static final long serialVersionUID = 100L;
    private static final String TURN_TITLE_FORMAT = "*** TURN %d - PLAYER %s's TURN **************************************************";
    private static final String PASS_ROUND_TITLE_FORMAT = "~~ main.Player %s has already passed for this current round ~~";
    private final transient Deck deck;
    private final transient PlayedPile pile;
    private final transient ServerSocket socket;
    private final List<Room> room;
    private int whoseTurn;
    private boolean isGameOver;
    private int idRoom = 0;
    private boolean[] check;
    Connection conn;
    UserDB us;
    private int count;

    public GameManager() throws IOException {

        System.out.println("Generating New Game...");
        deck = new Deck();
        prepareDeck();
        deck.sortDeck();
        conn = ConnectDB.getConnect();
        RoomDB roomDB = new RoomDB();
        room = roomDB.getAllRoom(conn);
        pile = new PlayedPile();
        socket = new ServerSocket(PORT);
        check = new boolean[100];
        for (int i = 0; i < check.length; i++) {
            check[i] = true;
        }
        us = new UserDB();
        us.setIsLogin(conn, "no");
        us.setIdRoom(conn, -1);
        count = 0;
    }

    public void run() {
        waitingForPlayersConnect();
    }

    private void notifyAllPlayersStartingGame() {
        room.get(idRoom).getOutputClient().forEach(outputClient -> {
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(outputClient.getOutputStream());
                outputStream.writeObject("Game started!!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        float coin = 0;
        for (int i = 0; i < 4; i++) {
            coin = room.get(idRoom).getPlayer().get(i).getMoney() - room.get(idRoom).getBet();
            us.setCoin(conn, room.get(idRoom).getPlayer().get(i).getId(), coin);
        }
    }

    private void sending() {
        ServerSendingThread sendingThread = new ServerSendingThread(room.get(idRoom).getOutputClient(), this);
        sendingThread.start();
    }

    private void waitingForPlayersConnect() {
        try {
            while (true) {
                Socket client = socket.accept();
                Client c = new Client(client);
                boolean kt = false;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DataInputStream dis;
                        ObjectInputStream inputStream;
                        ObjectOutputStream outputStream;
                        DataOutputStream dos;
                        File file = new File("fileLog/log.txt");
                        FileWriter fw = null;
                        BufferedWriter bw = null;
                        try {
                            fw = new FileWriter(file, true);
                            bw = new BufferedWriter(fw);
                        } catch (IOException ex) {
                            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        int input;
                        try {
                            while (!client.isClosed()) {
                                dis = new DataInputStream(client.getInputStream());
                                input = dis.readInt();
                                if (input == 105) {// vào phòng 
                                    PlayerData player;
                                    inputStream = new ObjectInputStream(client.getInputStream());
                                    Object o = inputStream.readObject();
                                    if (o instanceof PlayerData) {
                                        player = (PlayerData) o;
                                        idRoom = player.getIdRoom();
                                    } else {
                                        throw new IllegalArgumentException("Player Data is invalid");
                                    }
                                    if (room.get(idRoom).getPlayer().size() < 4) {
                                        if (room.get(idRoom).getBet() > player.getMoney()) {
                                            outputStream = new ObjectOutputStream(client.getOutputStream());
                                            outputStream.writeObject(new PlayerData(6));
                                        } else {
                                            room.get(idRoom).addPlayer(new Player(player.getId(), player.getPlayerName(),
                                                    false, (int) player.getMoney(), player.getIdRoom()));
                                            System.out.println(String.format("Player %s joined room %d!", player.getPlayerName(), idRoom));
                                            outputStream = new ObjectOutputStream(client.getOutputStream());
                                            outputStream.writeObject(new PlayerData(room.get(idRoom).getPlayerIndex()));
                                            room.get(idRoom).addClient(client);
                                            us.setIdRoom(conn, player.getId(), idRoom);
                                            c.setInGame(true);
                                        }
                                    }
                                    if (room.get(idRoom).getPlayer().size() == 4 && check[idRoom] == true) {
                                        check[idRoom] = false;
                                        us.setIdRoom(conn, player.getId(), idRoom);
                                        notifyAllPlayersStartingGame();
                                        System.out.println("Game Started!\n");

                                        deck.shuffle();
                                        deal();
                                        whoseTurn = pile.findStarter(room.get(idRoom).getPlayer());
                                        sending();
                                        play(socket);
                                    } else if (room.get(idRoom).getPlayer().size() == 4 && check[idRoom] == false) {
                                        outputStream = new ObjectOutputStream(client.getOutputStream());
                                        outputStream.writeObject(new PlayerData(5));
                                    }
                                }
                                if (input == 100) {

                                    dos = new DataOutputStream(client.getOutputStream());
                                    dis = new DataInputStream(client.getInputStream());
                                    String user = dis.readUTF();
                                    String pass = dis.readUTF();
                                    PlayerData p = us.checkLogin(conn, user, pass);
                                    if (p != null) {
                                        String isLogin = us.checkIsLogin(conn, user, pass);
                                        if (isLogin.equalsIgnoreCase("no")) {
                                            Date date = new Date();
                                            String str = date.toString() + " " + p.getId() + " Connect\n";
                                            bw.write(str);
                                            us.setIsLogin(conn, p.getId(), "yes");//set trạng thái login
                                            dos.writeUTF("Yes");
                                            outputStream = new ObjectOutputStream(client.getOutputStream());
                                            outputStream.writeObject(p);
                                            c.setId(p.getId());
                                        } else {
                                            dos.writeUTF("islogin");
                                        }
                                    } else {
                                        dos.writeUTF("No");
                                    }
                                }
                                if (input == 404) {
                                    client.close();
                                    System.out.println("Đóng client " + c.getId());
                                    us.setIsLoginAndRoom(conn, c.getId(), "no", -1);
                                }
                                if (input == 101) {
                                    List<PlayerData> list = us.listPlayer(conn);
                                    outputStream = new ObjectOutputStream(client.getOutputStream());
                                    outputStream.writeObject(list);
                                }
                                if (input == 102) {
                                    dis = new DataInputStream(client.getInputStream());
                                    int id = dis.readInt();
                                    us.setIsLoginAndRoom(conn, id, "no", -1);
                                    Date date = new Date();
                                    String str = date.toString() + " " + id + " Disconnect\n";
                                    bw.write(str);
                                }
                                if (input == 103) {
                                    dis = new DataInputStream(client.getInputStream());
                                    String coin = dis.readUTF();
                                    int money = Integer.parseInt(coin);
                                    Room r = new Room(room.size(), money);
                                    room.add(r);
                                    inputStream = new ObjectInputStream(client.getInputStream());
                                    Object o = inputStream.readObject();
                                    if (o instanceof PlayerData) {

                                        PlayerData player = (PlayerData) o;
                                        room.get(room.size() - 1).addPlayer(new Player(player.getId(), player.getPlayerName(),
                                                false, (int) player.getMoney(), player.getIdRoom()));
                                        System.out.println(String.format("Player %s joined room %d!", player.getPlayerName(), room.size() - 1));
                                    } else {
                                        throw new IllegalArgumentException("Player Data is invalid");
                                    }

                                    outputStream = new ObjectOutputStream(client.getOutputStream());
                                    outputStream.writeObject(new PlayerData(room.get(idRoom).getPlayerIndex()));
                                    room.get(room.size() - 1).addClient(client);
                                }
                            }
                        } catch (SocketException e) {
                            e.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                        try {
                            bw.close();
                            fw.close();
                        } catch (IOException ex) {
                            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                t.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methods
    public void play(ServerSocket socket) {
        int turnCounter = 1;
        try {
            do {
                pile.newRound(whoseTurn, room.get(idRoom).getPlayer());

                //received played turn from client
                Socket inputClient = socket.accept();
                ObjectInputStream inputStream = new ObjectInputStream(inputClient.getInputStream());
                ClientToServerData data = getPlayerTurnInfo(inputStream);

                if (handleTurn(room.get(idRoom).getPlayer().get(whoseTurn), pile, turnCounter, data)) {
                    //send result to client
                    ServerSendingThread sendingThread = new ServerSendingThread(room.get(idRoom).getOutputClient(), this);
                    sendingThread.start();
                    turnCounter++;
                    System.out.println("*** END OF TURN ***************************************************************\n");
                }
                isGameOver = pile.isGameOver(room.get(idRoom).getPlayer());
            } while (!isGameOver);

            Player player = room.get(idRoom).getPlayer().stream()
                    .filter(Player::isHandEmpty)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Something went wrong!"));
            us.setCoin(conn, player.getId(), player.getMoney()+room.get(idRoom).getBet()*4);
            ServerSendingThread sendingThread = new ServerSendingThread(room.get(idRoom).getOutputClient(), this, false, String.format("PLAYER %s WINS! GAME OVER!%n", player.getName()));
            sendingThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean handleTurn(Player player, PlayedPile pile, int turnCounter, ClientToServerData data) {
        //handle played turn
        try {
            playTurn(player, pile, turnCounter, data);
            do {
                whoseTurn = (whoseTurn + 1) % 4;
            } while (checkPlayerAlreadyPassed(whoseTurn));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ServerSendingThread sendingThread = new ServerSendingThread(Collections.singletonList(room.get(idRoom).getOutputClient().get(whoseTurn)), this, true, e.getMessage());
            sendingThread.start();
            return false;
        }
    }

    private boolean checkPlayerAlreadyPassed(int whoseTurn) {
        return this.getPlayers().get(whoseTurn).isPassing();
    }

    private void playTurn(Player player, PlayedPile pile, int turnCounter, ClientToServerData data) {
        System.out.println(String.format(TURN_TITLE_FORMAT, turnCounter, player.getName()));
        if (!player.isPassing()) {
            pile.playerTurn(player, player.getName(), player.checkAI(), turnCounter, data);
        } else {
            System.out.println(String.format(PASS_ROUND_TITLE_FORMAT, player.getName()));
        }
    }

    private ClientToServerData getPlayerTurnInfo(ObjectInputStream inputStream) {
        try {
            return (ClientToServerData) inputStream.readObject();
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not get input from client");
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Received invalid data from client");
        }
    }

    private void deal() {
        while (!deck.isEmpty()) {
            room.get(idRoom).getPlayer().forEach(player -> player.drawCard(deck));
        }

        room.get(idRoom).getPlayer().forEach(player -> player.sortHand(true));
    }

    private void prepareDeck() {
        Card temp;
        int size = deck.size();
        int shiftSize = 1;
        int currVal;
        int aceIndex = 0;
        int twoIndex = 1;
        int aceVal = 12;
        int twoVal = 49;

        for (int i = 0; i < size; i++) {
            temp = deck.removeAt(0);
            currVal = temp.getSVal();

            if (i == aceIndex) {
                temp.setSVal(aceVal);
                temp.setRank(14);
                aceIndex += 13;
                aceVal += 12;
                shiftSize += 1;
            } else if (i == twoIndex) {
                temp.setSVal(twoVal);
                twoIndex += 13;
                twoVal += 1;
            } else {
                temp.setSVal(currVal - shiftSize);
            }

            deck.placeTop(temp);
        }
    }

    public PlayedPile getPile() {
        return pile;
    }

    public List<Player> getPlayers() {
        return room.get(idRoom).getPlayer();
    }

    public int getWhoseTurn() {
        return whoseTurn;
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
