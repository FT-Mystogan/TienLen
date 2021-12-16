package main.tools;

import main.dto.ClientToServerData;
import main.utils.DefaultUtils;
import main.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static main.constant.Application.LOWEST_CARD;
import static main.constant.ApplicationError.INVALID_SELECTION;
import static main.constant.ApplicationError.PLAYED_SET_INVALID;

@SuppressWarnings("java:S106")
public class PlayedPile {
    private final Utils utils = new DefaultUtils();
    // Data Fields
    private final List<Card> currSet;
    private final List<Card> cardSel;
    private boolean playAny;
    private boolean isSingle;
    private boolean isDouble;
    private boolean isTriple;
    private boolean isStraight;
    private boolean isBomb;
    private int straightSize;
    boolean validSSize = false;
    private int typeSel;
    
    PlayedPile() {
        currSet = new ArrayList<>();
        playAny = true;
        isSingle = false;
        isDouble = false;
        isTriple = false;
        isStraight = false;
        isBomb = false;
        straightSize = 3;
        typeSel = 0;
        cardSel = new ArrayList<>();
    }

    public void playerTurn(Player player, String playerName, boolean isAI, int turnNum, ClientToServerData data) {
        int userCommand;
        int userTypeSelection;
        cardSel.clear();

        if (isAI) {
            aiTurn(player, playerName);
        } else {
            System.out.println("Current Play:");
            printCurrPlay();

            //begins checking valid data
            if (data.isPassing()) {
                userCommand = 2;
                System.out.println("User wants to pass this round!");
            } else {
                userCommand = 1;
                System.out.println("User wants to play this round!");
            }

            switch (userCommand) {
                case 1:
                    ArrayList<Integer> cIndices = new ArrayList<>();
                    userTypeSelection = data.getTypeSelection();
                    System.out.println("[1] Single [2] Double [3] Triple [4] Straight [5] Bomb");
                    System.out.println("The number of the type of set user would like to play: " + userTypeSelection);

                    checkTypeSelection(userTypeSelection);

                    int numOfCards = data.getPlayedSet().size();

                    if (typeSel == 4) {
                        validSSize = false;
                        System.out.println("The size of the Straight user would like to play: " + numOfCards);

                        if (playAny) {
                            if (numOfCards > 2 && numOfCards < player.handSize()) {
                                straightSize = numOfCards;
                                validSSize = true;
                            } else {
                                System.out.println("**Invalid Size - Select a size between 3 and " + player.handSize() + "**");
                                throw new IllegalArgumentException("Invalid Size - Select a size between 3 and " + player.handSize());
                            }
                        } else if (numOfCards > straightSize) {
                            System.out.println("**Invalid Size - Current Straight Size is " + straightSize + "**");
                            throw new IllegalArgumentException("Invalid Size - Current Straight Size is " + straightSize);
                        } else {
                            validSSize = true;
                        }
                    }

                    if (userTypeSelection == 4 && !validSSize) {
                        throw new IllegalArgumentException(PLAYED_SET_INVALID);
                    }
                    player.displayHand();
                    System.out.println("The number(s) of the card(s) you would like to play (ie. 1 2 3): " + displayCardsIndex(data.getPlayer(), data.getPlayedSet()));

                    cardSel.clear();

                    data.getPlayedSet().forEach(card -> cIndices.add(data.getPlayer().getIndexOf(card)));
                    cardSel.addAll(data.getPlayedSet());

                    if (cardSel.size() > numOfCards) {
                        throw new IllegalArgumentException(PLAYED_SET_INVALID);
                    }

                    if (!isValid(userTypeSelection, cardSel, turnNum)) {
                        throw new IllegalArgumentException(PLAYED_SET_INVALID);
                    }

                    playSelection(playerName);
                    removeFromHand(player, cIndices);
                    break;
                case 2:
                    if (playAny) {
                        System.out.println("You cannot pass when you are able to play anything!");
                        throw new IllegalArgumentException("You cannot pass when you are able to play anything!");
                    } else {
                        System.out.println("~~ main.Player " + playerName + " is passing ~~");
                        player.setPassing(true);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private String displayCardsIndex(Player player, List<Card> playedSet) {
        StringBuilder result = new StringBuilder();
        playedSet.forEach(card -> {
            if (player.getIndexOf(card) != -1) {
                result.append(player.getIndexOf(card) + 1);
                result.append(" ");
            } else {
                throw new IllegalArgumentException("This playedSet is invalid");
            }
        });
        return result.toString();
    }

    // Methods
    public int findStarter(List<Player> players) {
        Player player = players.stream()
                .filter(p -> p.checkAt(0).equals(LOWEST_CARD))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No player has 3 of Spades"));

        return players.indexOf(player);
    }

    public boolean isGameOver(List<Player> players) {
        boolean gameOver = false;
        Optional<Player> player = players.stream().filter(Player::isHandEmpty).findFirst();
        if (player.isPresent()) {
            System.out.println(String.format("PLAYER %d WINS! GAME OVER!%n", players.indexOf(player.get()) + 1));
            gameOver = true;
        }

        return gameOver;
    }

    public void newRound(int whoseTurn, List<Player> players) {
        if (checkPasses(whoseTurn, players)) {
            currSet.clear();
            playAny = true;
        }
    }

    public void printCurrPlay() {
        if (currSet.isEmpty()) {
            printCurrRound();
        } else {
            currSet.forEach(card -> System.out.println("|" + card + "|"));
        }
    }

    private void aiTurn(Player currAI, String playerName) {
        System.out.println("~~ main.Player " + playerName + " is passing ~~");
        currAI.setPassing(true);
    }

    private void checkTypeSelection(int userTypeSelection) {
        switch (userTypeSelection) {
            case 1:
                typeSel = isSingle || playAny ? userTypeSelection : throwException();
                break;
            case 2:
                typeSel = isDouble || playAny ? userTypeSelection : throwException();
                break;
            case 3:
                typeSel = isTriple || playAny ? userTypeSelection : throwException();
                break;
            case 4:
                typeSel = isStraight || playAny ? userTypeSelection : throwException();
                break;
            case 5:
                typeSel = isSingle || isBomb || playAny ? userTypeSelection : throwException();
                break;
            default:
                throwException();
        }
    }

    private int throwException() {
        System.out.println(INVALID_SELECTION);
        printCurrRound();
        throw new IllegalArgumentException(INVALID_SELECTION);
    }

    private boolean isValid(int userTypeSel, List<Card> toPlay, int turnNum) {
        boolean validPlay = false;

        if (userTypeSel == 1 && toPlay.size() == 1 && (playAny || isSingle)) {
            validPlay = checkSingle(toPlay, turnNum);
            if (validPlay) {
                setTypeFlags(true, false, false, false, false);
                if (playAny) {
                    playAny = false;
                }
            }
        } else if (userTypeSel == 2 && toPlay.size() == 2 && (playAny || isDouble)) {
            validPlay = checkDouble(toPlay, turnNum);
            if (validPlay) {
                setTypeFlags(false, true, false, false, false);
                if (playAny) {
                    playAny = false;
                }
            }
        } else if (userTypeSel == 3 && toPlay.size() == 3 && (playAny || isTriple)) {
            validPlay = checkTriple(toPlay, turnNum);
            if (validPlay) {
                setTypeFlags(false, false, true, false, false);
                if (playAny) {
                    playAny = false;
                }
            }
        } else if (userTypeSel == 4 && toPlay.size() == straightSize && (playAny || isStraight)) {
            validPlay = checkStraight(toPlay, turnNum);
            if (validPlay) {
                setTypeFlags(false, false, false, true, false);
                if (playAny) {
                    playAny = false;
                }
            }
        } else if (userTypeSel == 5 && toPlay.size() == 6 && (playAny || isSingle || isBomb)) {
            validPlay = checkBomb(toPlay, turnNum);
            if (validPlay) {
                setTypeFlags(false, false, false, false, true);
                if (playAny) {
                    playAny = false;
                }
            }
        }

        return validPlay;
    }

    private boolean checkSingle(List<Card> toPlay, int turnNum) {
        System.out.println("CHECKING SINGLE . . .");
        if (turnNum == 1 && !utils.checkValidFirstRound(toPlay)) {
            return false;
        }

        if (playAny) {
            return true;
        }

        if (currSet.size() != 1) {
            throw new IllegalArgumentException("The current played set of cards is not a Single");
        }

        Card selectedCard = toPlay.get(0);
        Card currentCard = currSet.get(0);

        if (selectedCard.getRank() == 1
                || selectedCard.getRank() == 2
                || currentCard.getRank() == 1
                || currentCard.getRank() == 2) {
            return selectedCard.getSVal() > currentCard.getSVal();
        }

        if (selectedCard.getRank() > currentCard.getRank()) {
            return true;
        }

        if (selectedCard.getRank() == currentCard.getRank()) {
            return selectedCard.getSuite() > currentCard.getSuite();
        }

        return false;
    }

    private boolean checkDouble(List<Card> toPlay, int turnNum) {
        System.out.println("CHECKING DOUBLE . . .");
        if (turnNum == 1 && !utils.checkValidFirstRound(toPlay)) {
            return false;
        }

        Collections.sort(toPlay);
        Card tpFirstC = toPlay.get(0);
        Card tpSecondC = toPlay.get(1);

        if (tpFirstC.getRank() == tpSecondC.getRank()) {
            if (playAny) {
                return true;
            }
            Card highestCurrC = currSet.get(1);
            if (highestCurrC.getRank() == 2 && tpSecondC.getSVal() < highestCurrC.getSVal()) {
                throw new IllegalArgumentException(PLAYED_SET_INVALID);
            }
            if (tpFirstC.getRank() == 2 || tpSecondC.getRank() == 2) {
                return true;
            }
            if (tpSecondC.getRank() > highestCurrC.getRank()) {
                return true;
            }

            if (tpSecondC.getRank() == highestCurrC.getRank()) {
                return tpSecondC.getSuite() > highestCurrC.getSuite();
            }
        }
        return false;
    }

    private boolean checkTriple(List<Card> toPlay, int turnNum) {
        System.out.println("CHECKING TRIPLE . . .");

        if (turnNum == 1 && !utils.checkValidFirstRound(toPlay)) {
            return false;
        }

        Collections.sort(toPlay);
        Card tpFirstC = toPlay.get(0);
        Card tpSecondC = toPlay.get(1);
        Card tpThirdC = toPlay.get(2);

        if (tpFirstC.getRank() == tpSecondC.getRank() && tpSecondC.getRank() == tpThirdC.getRank()) {
            if (playAny) {
                return true;
            }
            Card highestCurrC = currSet.get(2);
            if (highestCurrC.getRank() == 2) {
                throw new IllegalArgumentException(PLAYED_SET_INVALID);
            }
            if (tpFirstC.getRank() == 2 || tpSecondC.getRank() == 2 || tpThirdC.getRank() == 2) {
                return true;
            }
            if (tpThirdC.getRank() > highestCurrC.getRank()) {
                return true;
            }
            if (tpThirdC.getRank() == highestCurrC.getRank()) {
                return tpThirdC.getSuite() > highestCurrC.getSuite();
            }

        }
        return false;
    }

    private boolean checkStraight(List<Card> toPlay, int turnNum) {
        System.out.println("CHECKING STRAIGHT . . .");
        boolean isConsec = false;

        if (turnNum == 1 && !utils.checkValidFirstRound(toPlay)) {
            return false;
        }

        Collections.sort(toPlay);
        for (int i = 0; i < toPlay.size() - 1; i++) {
            if (toPlay.get(i).getRank() == 2 || toPlay.get(i + 1).getRank() == 2) {
                throw new IllegalArgumentException("You cannot use a 2 in a Straight");
            }
            if ((toPlay.get(i).getRank() + 1) == toPlay.get(i + 1).getRank()) {
                isConsec = true;
            } else {
                isConsec = false;
                break;
            }
        }

        if (isConsec) {
            if (playAny) {
                return true;
            }

            if (currSet.size() != straightSize) {
                throw new IllegalArgumentException("The current played set of cards does not have the same straight size as your selection");
            }

            Card highestTpC = toPlay.get(straightSize - 1);
            Card highestCurrC = currSet.get(straightSize - 1);
            if (highestTpC.getRank() > highestCurrC.getRank()) {
                return true;
            } else if (highestTpC.getRank() == highestCurrC.getRank()) {
                return highestTpC.getSuite() > highestCurrC.getSuite();
            }
        }
        return false;
    }

    private boolean checkBomb(List<Card> toPlay, int turnNum) {
        System.out.println("CHECKING BOMB . . .");

        if (turnNum == 1) {
            throw new IllegalArgumentException("You cannot play a Bomb on Turn 1");
        }

        boolean validBomb = false;

        Collections.sort(toPlay);
        Card tpFirstD = toPlay.get(1);
        Card tpSecondD = toPlay.get(3);
        Card tpThirdD = toPlay.get(5);

        if (tpFirstD.getRank() == toPlay.get(0).getRank()
                && tpSecondD.getRank() == toPlay.get(2).getRank()
                && tpThirdD.getRank() == toPlay.get(4).getRank()
                && (tpFirstD.getRank() + 1 == tpSecondD.getRank())
                && (tpSecondD.getRank() + 1 == tpThirdD.getRank())) {
            validBomb = true;
        }

        if (validBomb) {
            if (playAny) {
                throw new IllegalArgumentException("You can only Bomb a 2 or another Bomb");
            }
            if (isSingle) {
                if (currSet.size() != 1) {
                    throw new IllegalArgumentException("The current played set of cards is not a valid play to Bomb");
                }
                if (currSet.get(0).getRank() == 2) {
                    return true;
                } else {
                    throw new IllegalArgumentException("You can only Bomb a Single that is a 2");
                }
            }
            if (isBomb) {
                if (currSet.size() != 6) {
                    throw new IllegalArgumentException("The current played set of cards is not a Bomb");
                }

                Card highestCurrC = currSet.get(5);
                if (tpThirdD.getRank() > highestCurrC.getRank()) {
                    return true;
                }
                if (tpThirdD.getRank() == highestCurrC.getRank()) {
                    return tpThirdD.getSuite() > highestCurrC.getSuite();
                }
            }
        }

        throw new IllegalArgumentException("Your selected Bomb was not a set of 3 consecutive Doubles");
    }

    private boolean checkPasses(int whoseTurn, List<Player> players) {
        boolean allPassed = true;
        List<Player> others = players.stream()
                .filter(player -> players.indexOf(player) != whoseTurn).collect(Collectors.toList());
        for (Player player :
                others) {
            allPassed = allPassed && player.isPassing();
        }
        if (allPassed) {
            others.forEach(player -> player.setPassing(false));
        }
        return allPassed;
    }

    private void playSelection(String playerName) {
        currSet.clear();
        currSet.addAll(cardSel);
        cardSel.clear();
        System.out.println("main.Player " + playerName + " played:");
        printCurrPlay();
    }

    private void removeFromHand(Player currP, ArrayList<Integer> cIndices) {
        for (int i = cIndices.size() - 1; i >= 0; i--) {
            currP.removeCardAt(cIndices.get(i));
        }
    }

    private void setTypeFlags(boolean isSingle, boolean isDouble, boolean isTriple, boolean isStraight, boolean isBomb) {
        this.isSingle = isSingle;
        this.isDouble = isDouble;
        this.isTriple = isTriple;
        this.isStraight = isStraight;
        this.isBomb = isBomb;
    }

    private void printCurrRound() {
        if (playAny) {
            System.out.println(">> Current Round is Anything <<");
        } else if (isSingle) {
            System.out.println(">> Current Round is Single <<");
        } else if (isDouble) {
            System.out.println(">> Current Round is Double <<");
        } else if (isTriple) {
            System.out.println(">> Current Round is Triple <<");
        } else if (isStraight) {
            System.out.println(">> Current Round is Straight of size: " + straightSize + " <<");
        } else if (isBomb) {
            System.out.println(">> Current Round is Bomb <<");
        }
    }

    public List<Card> getCurrSet() {
        return currSet;
    }
}
