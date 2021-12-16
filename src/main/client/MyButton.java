package main.client;

import main.dto.ClientToServerData;
import main.utils.DefaultUtils;
import main.utils.Utils;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static main.constant.Application.PORT;

@SuppressWarnings("java:S110")
public class MyButton extends JButton implements MouseListener {

    public MyButton(String text) {
        super(text);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //TO-DO: do nothing for now
    }

    @Override
    public void mousePressed(MouseEvent e) {
        try {
            if (this.isEnabled()) {
                Socket socket = new Socket("localhost", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                if (!(e.getSource() instanceof MyButton)) {
                    throw new IllegalArgumentException("Something went wrong");
                }

                MyButton component = (MyButton) e.getSource();
                if (!(component.getParent() instanceof MyPanel)) {
                    throw new IllegalArgumentException("Something went wrong");
                }
                MyPanel parent = (MyPanel) component.getParent();
                if (component.getText().equals("Play")) {
                    outputStream.writeObject(setOutputDataForServer(parent, false));
                } else if(component.getText().equals("Pass")) {
                    outputStream.writeObject(setOutputDataForServer(parent, true));
                }
                outputStream.flush();
                outputStream.close();
                socket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private ClientToServerData setOutputDataForServer(MyPanel panel, boolean isPassing) {
        Utils utils = new DefaultUtils();
        ClientToServerData clientToServerData = new ClientToServerData();
        clientToServerData.setPassing(isPassing);
        clientToServerData.setPlayedSet(isPassing ? null : panel.getPlayedCards());
        clientToServerData.setPlayer(panel.getPlayer());
        try {
            clientToServerData.setTypeSelection(utils.checkTypeSelection(panel.getPlayedCards()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.getParent(), e.getMessage());
        }

        return clientToServerData;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //TO-DO: do nothing for now
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //TO-DO: do nothing for now
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //TO-DO: do nothing for now
    }
}
