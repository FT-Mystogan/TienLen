package main.client;

import main.tools.Card;
import main.utils.DefaultUtils;
import main.utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MyCard extends JLabel implements MouseListener {
    private Card card;

    public MyCard(Card card) throws IOException {
        super();
        this.card = card;
        Utils utils = new DefaultUtils();
        String imagePath = utils.generateImagePathFromCardValue(utils.resolveSuite(card.getSuite()), utils.resolveRank(card.getRank()));
        BufferedImage myPicture = ImageIO.read(new File(imagePath));
        Image img = myPicture.getScaledInstance(70,105, Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(img);
        this.setIcon(imageIcon);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //TO-DO: do nothing for now
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!(e.getSource() instanceof MyCard)) {
            throw new IllegalArgumentException("Something went wrong");
        }

        MyCard component = (MyCard) e.getSource();
        if (!(component.getParent() instanceof MyPanel)) {
            throw new IllegalArgumentException("Something went wrong");
        }

        MyPanel parent = (MyPanel) component.getParent();
        if (this.getY() <= 0) {
            this.setLocation(this.getX(), this.getY() + 10);
            if (parent.getPlayer().getHand().contains(component.getCard())) {
                parent.getPlayedCards().remove(component.getCard());
            }
        } else {
            this.setLocation(this.getX(), this.getY() - 10);
            if (parent.getPlayer().getHand().contains(component.getCard())) {
                parent.getPlayedCards().add(component.getCard());
            }

        }
        parent.repaint();
        this.repaint();
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

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
