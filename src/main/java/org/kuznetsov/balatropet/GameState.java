package org.kuznetsov.balatropet;

public class GameState {
    public static int HAND_SIZE;
    public static int ANTE;
    public static int MONEY;

    public static final int[] chips = new int[] {10, 20, 40, 60, 70, 75, 80, 120, 150, 200};
    public static final int[] multiply = new int[] {1, 2, 2, 4, 5, 5, 6, 8, 10, 12};

    public static int HIGH_CARD_LVL;
    public static int PAIR_LVL;
    public static int TWO_PAIR_LVL;
    public static int THREE_OF_A_KIND_LVL;
    public static int STRAIGHT_LVL;
    public static int FLUSH_LVL;
    public static int FULL_HOUSE_LVL;
    public static int FOUR_OF_A_KIND_LVL;
    public static int STRAIGHT_FLUSH_LVL;
    public static int FLUSH_ROYAL_LVL;

    public static void initializeLevels(int n) {
        HIGH_CARD_LVL = n;
        PAIR_LVL = n;
        TWO_PAIR_LVL = n;
        THREE_OF_A_KIND_LVL = n;
        STRAIGHT_LVL = n;
        FLUSH_LVL = n;
        FULL_HOUSE_LVL = n;
        FOUR_OF_A_KIND_LVL = n;
        STRAIGHT_FLUSH_LVL = n;
        FLUSH_ROYAL_LVL = n;
    }
}
