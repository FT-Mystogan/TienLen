package main.utils;

import main.tools.Card;

import java.util.Collections;
import java.util.List;

import static main.constant.Application.IMAGE_PATH_FORMAT;
import static main.constant.Application.LOWEST_CARD;
import static main.constant.ApplicationError.FIRST_ROUND_INVALID;
import static main.constant.Rank.*;
import static main.constant.Suite.*;

public class DefaultUtils implements Utils {

    @Override
    public String generateImagePathFromCardValue(String suite, String rank) {
        return String.format(IMAGE_PATH_FORMAT, suite, rank);
    }

    @Override
    public String resolveSuite(int suite) {
        switch (suite) {
            case 1:
                return SPADES;
            case 2:
                return CLUBS;
            case 3:
                return DIAMONDS;
            case 4:
                return HEARTS;
            default:
                throw new IllegalArgumentException("There no available suite for this value");
        }
    }

    @Override
    public String resolveRank(int rank) {
        switch (rank) {
            case 14:
            case 1:
                return ACE;
            case 2:
                return TWO;
            case 3:
                return THREE;
            case 4:
                return FOUR;
            case 5:
                return FIVE;
            case 6:
                return SIX;
            case 7:
                return SEVEN;
            case 8:
                return EIGHT;
            case 9:
                return NINE;
            case 10:
                return TEN;
            case 11:
                return JACK;
            case 12:
                return QUEEN;
            case 13:
                return KING;
            default:
                throw new IllegalArgumentException("There is no available rank for this value");
        }
    }

    @Override
    public int checkTypeSelection(List<Card> cards) {
        if (checkTripleSelection(cards)) {
            return 3;
        }

        if (checkStraightSelection(cards)) {
            return 4;
        }

        if (checkBombSelection(cards)) {
            return 5;
        }

        return cards.size();
    }

    @Override
    public boolean checkTripleSelection(List<Card> cards) {
        if (cards.size() != 3) {
            return false;
        }
        Collections.sort(cards);
        Card tpFirstC = cards.get(0);
        Card tpSecondC = cards.get(1);
        Card tpThirdC = cards.get(2);
        if (tpFirstC.getRank() == 2 || tpSecondC.getRank() == 2 || tpThirdC.getRank() == 2) {
            throw new IllegalArgumentException("You cannot use 2 in a Triple");
        } else
            return tpFirstC.getRank() == tpSecondC.getRank() && tpSecondC.getRank() == tpThirdC.getRank();
    }

    @Override
    public boolean checkStraightSelection(List<Card> cards) {
        if (cards.size() < 3) {
            return false;
        }

        if (cards.stream().anyMatch(card -> card.getRank() == 2)) {
            throw new IllegalArgumentException("You cannot use a 2 in a Straight");
        }

        boolean valid = false;
        Collections.sort(cards);
        for (int i = 0; i < cards.size() - 1; i++) {
            if ((cards.get(i).getRank() + 1) == cards.get(i + 1).getRank()) {
                valid = true;
            } else {
                valid = false;
                break;
            }
        }
        return valid;
    }

    @Override
    public boolean checkBombSelection(List<Card> cards) {
        if (cards.size() != 6) {
            return false;
        }
        Collections.sort(cards);
        Card tpFirstD = cards.get(1);
        Card tpSecondD = cards.get(3);
        Card tpThirdD = cards.get(5);

        if (tpFirstD.getRank() == cards.get(0).getRank() &&
                tpSecondD.getRank() == cards.get(2).getRank() &&
                tpThirdD.getRank() == cards.get(4).getRank()) {
            return (tpFirstD.getRank() + 1 == tpSecondD.getRank()) && (tpSecondD.getRank() + 1 == tpThirdD.getRank());
        }

        return false;
    }

    @Override
    public boolean checkValidFirstRound(List<Card> cards) {
        Collections.sort(cards);
        if (cards.get(0).equals(LOWEST_CARD)) {
            return true;
        } else {
            throw new IllegalArgumentException(FIRST_ROUND_INVALID);
        }
    }
}
