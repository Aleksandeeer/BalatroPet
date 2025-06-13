package org.kuznetsov.balatropet.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Deck {
    List<Card> deck;
    @Getter
    public static String[] symbols = new String[] {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    @Getter
    public static String[] suit = new String[] {"♥", "♠", "♦", "♣"};

    public Deck() {
        deck = new ArrayList<>();
        for (int i = 0; i < 4; i++) { // suit
            for (int j = 1; j < 13; j++) { // nominal
                deck.add(new Card((i + 1) * j, Suit.values()[i], j, true, symbols[j], suit[i]));
            }
        }
    }

    public Deck(List<Card> deck) {
        this.deck = deck;
    }
}
