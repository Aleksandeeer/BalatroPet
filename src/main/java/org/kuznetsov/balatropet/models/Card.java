package org.kuznetsov.balatropet.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Card {
    int id;
    Suit suit;
    int nominal;
    boolean is_enable; // true -> default
    String nominal_symbol;
    String suit_symbol;
}
