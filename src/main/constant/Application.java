package main.constant;

import main.tools.Card;

import java.awt.*;

public class Application {
    private Application(){}
    public static final int PORT = 5000;
    public static final String BACK_CARD_IMAGE_PATH = "resources\\images\\back.png";
    public static final Color BACKGROUND_COLOR = Color.decode("0x1b6d3c");
    public static final Card LOWEST_CARD = new Card(1, 3);
    public static final String IMAGE_PATH_FORMAT = "resources\\images\\%s\\%s.png";
}
