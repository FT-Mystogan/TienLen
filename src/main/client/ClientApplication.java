package main.client;

import main.dto.PlayerData;
import main.dto.ServerToClientData;
import main.tools.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static main.constant.Application.*;

@SuppressWarnings("java:S110")
public class ClientApplication extends JFrame implements Runnable {

    private PlayerData playerData;
    private final MyPanel southPanel;
    private final MyPanel northPanel;
    private final MyPanel westPanel;
    private final MyPanel eastPanel;
    private final MyPanel centerPanel;

    public ClientApplication() {
        this.setSize(1200, 700);
        this.setMinimumSize(new Dimension(1200, 700));
        southPanel = new MyPanel();
        southPanel.setBackground(Color.DARK_GRAY);
        southPanel.setMinimumSize(new Dimension(1200, 120));
        southPanel.setSize(1200, 120);

        northPanel = new MyPanel();
        northPanel.setBackground(BACKGROUND_COLOR);

        westPanel = new MyPanel(new BorderLayout());
        westPanel.setBackground(BACKGROUND_COLOR);
        westPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        eastPanel = new MyPanel(new BorderLayout());
        eastPanel.setBackground(BACKGROUND_COLOR);
        eastPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        centerPanel = new MyPanel();
        centerPanel.setBackground(BACKGROUND_COLOR);
        this.getContentPane().add(BorderLayout.SOUTH, southPanel);
        this.getContentPane().add(BorderLayout.NORTH, northPanel);
        this.getContentPane().add(BorderLayout.WEST, westPanel);
        this.getContentPane().add(BorderLayout.EAST, eastPanel);
        this.getContentPane().add(BorderLayout.CENTER, centerPanel);
    }

    @Override

    public void run() {
        
        try {
            Socket socket = initFrameChoosingServer();
            boolean check= false;
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    int hoi = JOptionPane.showConfirmDialog(null, "Bạn có muốn thoát chương trình không?",
                            null, JOptionPane.YES_NO_OPTION);
                    if (hoi == JOptionPane.YES_OPTION) {
                        try {
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            dos.writeInt(404);
                        } catch (IOException ex) {
                            Logger.getLogger(ClientApplication.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.exit(0);
                    }
                }
            });
            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            waitingForStartingSign(socket);
            this.setTitle(this.playerData.getPlayerName());
            this.setVisible(true);
            boolean resize = false;
            boolean isGameOver = false;
            while (!isGameOver){
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Object object = inputStream.readObject();
                if (object instanceof ServerToClientData) {
                    ServerToClientData inputData = (ServerToClientData) object;
                    if (inputData.isHasError()) {
                        displayPopUp(centerPanel, inputData.getMessage());
                        continue;
                    }
                    //This implementation fixes the problem that panel needs to be resized to show all the component.
                    resize = refreshUI(inputData, resize);
                    isGameOver = inputData.isGameOver();
                }
            };

            notifyWinner(socket);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            displayPopUp(centerPanel, e.getMessage());
        }

    }

    private void notifyWinner(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        Object object = inputStream.readObject();
        if (object instanceof ServerToClientData) {
            ServerToClientData inputData = (ServerToClientData) object;
            displayPopUp(this.centerPanel, inputData.getMessage());
            this.dispose();
        }
    }

    private void waitingForStartingSign(Socket socket) {
        try {
            displayPopUp(this.centerPanel, "Waiting for other players...");
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Object object = inputStream.readObject();
            displayPopUp(this.centerPanel, String.valueOf(object));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private Socket initFrameChoosingServer() {
        /*ServerSubmissionForm form = new ServerSubmissionForm();
        form.run();*/
        HomePage form = new HomePage();
        form.setVisible(true);
        Socket client = form.getClient();
        while (form.getInputPlayerData() == null) {
            System.out.println();
        }
        this.playerData = form.getInputPlayerData();
        form.dispose();
        return client;
    }

    private boolean refreshUI(ServerToClientData inputData, boolean resize) {

        southPanel.removeAll();
        northPanel.removeAll();
        westPanel.removeAll();
        eastPanel.removeAll();
        centerPanel.removeAll();

        southPanel.setPlayedCards(new ArrayList<>());
        Player player = inputData.getPlayers().get(playerData.getPlayerId());

        southPanel.setPlayer(player);
        player.getHand().forEach(card -> {
            try {
                southPanel.add(BorderLayout.CENTER, new MyCard(card));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        inputData.getCurrentSet().forEach(card -> {
            try {
                centerPanel.add(new MyCard(card));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        int whoseTurn = inputData.getWhoseTurn();

        initDataForOtherPanels(playerData.getPlayerId(), inputData);
        createSetButton(whoseTurn);

        if (resize) {
            this.setSize(1201, 700);
            resize = false;
        } else {
            this.setSize(1202, 700);
            resize = true;
        }

        southPanel.repaint();
        northPanel.repaint();
        westPanel.repaint();
        eastPanel.repaint();
        centerPanel.repaint();
        return resize;
    }

    private void initDataForOtherPanels(int playerId, ServerToClientData inputData) {
        IntStream.range(0, 4).filter(index -> index != playerId).forEach(index -> {
            String numberOfCard = String.valueOf(inputData.getPlayers().get(index).getHand().size());
            String playerName = inputData.getPlayers().get(index).getName();
            int whoseTurn = inputData.getWhoseTurn();

            switch (playerId) {
                case 0:
                    setNumberOfCardsLeftForOthers(index, 1, 2, 3, numberOfCard, playerName, whoseTurn);
                    break;
                case 1:
                    setNumberOfCardsLeftForOthers(index, 2, 3, 0, numberOfCard, playerName, whoseTurn);
                    break;
                case 2:
                    setNumberOfCardsLeftForOthers(index, 3, 0, 1, numberOfCard, playerName, whoseTurn);
                    break;
                case 3:
                    setNumberOfCardsLeftForOthers(index, 0, 1, 2, numberOfCard, playerName, whoseTurn);
                    break;
                default:
                    break;
            }
        });
    }

    private void setNumberOfCardsLeftForOthers(int index, int west, int north, int east, String numberOfCard, String playerName, int whoseTurn) {
        try {
            BufferedImage myPicture = ImageIO.read(new File(BACK_CARD_IMAGE_PATH));
            Image img = myPicture.getScaledInstance(70, 105, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(img);
            JLabel label = new JLabel();
            label.setIcon(imageIcon);
            label.setText("<html>Player: " + playerName
                    + "<br>Cards left: " + numberOfCard);
            label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));

            if (index != whoseTurn) {
                label.setForeground(Color.WHITE);
            } else {
                label.setForeground(Color.RED);
            }

            if (index == north) {
                northPanel.add(label);
                return;
            }

            label.setVerticalTextPosition(SwingConstants.BOTTOM);
            label.setHorizontalTextPosition(SwingConstants.CENTER);

            if (index == west) {
                westPanel.add(BorderLayout.CENTER, label);
                return;
            }

            if (index == east) {
                eastPanel.add(BorderLayout.CENTER, label);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSetButton(int whoseTurn) {
        MyButton submitBtn = new MyButton("Play");
        MyButton passBtn = new MyButton("Pass");

        passBtn.setSize(50, 10);
        submitBtn.setSize(50, 10);

        submitBtn.setEnabled(playerData.getPlayerId() == whoseTurn);
        passBtn.setEnabled(playerData.getPlayerId() == whoseTurn);

        southPanel.add(submitBtn);
        southPanel.add(passBtn);

    }

    private void displayPopUp(MyPanel panel, String message) {
        JOptionPane.showMessageDialog(panel.getParent(), message);
    }
}
