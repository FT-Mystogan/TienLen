package main.constant;

public class ApplicationError {
    private ApplicationError(){}
    public static final String SUITE_ERROR = "Suite Error - Correct Options: 1 (Spades), 2 (Clubs), 3 (Diamonds), 4 (Hearts)";
    public static final String RANK_ERROR = "Rank Error - Correct Options: 1 (Ace), 2-10, 11 (Jack), 12 (Queen), 13 (King)";
    public static final String SECONDARY_VALUE_ERROR = "Secondary Value Error - Must be greater than 0";
    public static final String PLAYED_SET_INVALID = "Played set is invalid";
    public static final String FIRST_ROUND_INVALID = "The first turn must include the 3 of Spades";
    public static final String INVALID_SELECTION = "**Invalid Selection**";
}
