package main.utils;

import main.tools.Card;

import java.util.List;

public interface Utils {
    /**
     * This method will generate image path from suite and rank of a card.
     * @param suite There will be 4 values for suite.
     * @param rank There will be 13 values for card.
     * @return Image path of that card.
     */
    String generateImagePathFromCardValue(String suite, String rank);

    String resolveSuite(int suite);

    String resolveRank(int rank);

    int checkTypeSelection(List<Card> cards);

    boolean checkTripleSelection(List<Card> cards);

    boolean checkStraightSelection(List<Card> cards);

    boolean checkBombSelection(List<Card> cards);

    boolean checkValidFirstRound(List<Card> cards);
}
