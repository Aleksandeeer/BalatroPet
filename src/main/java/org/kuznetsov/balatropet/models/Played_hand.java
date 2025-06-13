package org.kuznetsov.balatropet.models;

import lombok.Getter;

@Getter
public enum Played_hand {
    HIGH_CARD("Старшая карта"),
    PAIR("Пара"),
    TWO_PAIR("Две пары"),
    THREE_OF_A_KIND("Сет"),
    STRAIGHT("Стрит"),
    FLUSH("Флеш"),
    FULL_HOUSE("Фулл-хаус"),
    FOUR_OF_A_KIND("Каре"),
    STRAIGHT_FLUSH("Стрит-флеш"),
    ROYAL_FLUSH("Роял-флеш");

    private final String russianName;

    Played_hand(String russianName) {
        this.russianName = russianName;
    }

}
