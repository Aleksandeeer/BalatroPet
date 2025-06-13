package org.kuznetsov.balatropet.constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Constants {
    public static String APP_NAME = "Balatro Pet Clone";

    public static class Init_parameters {
        public static int HAND_SIZE = 9;
    }

    public static class Messages {
        public static String PLAY = "Разыграть";
        public static String FOLD = "Сбросить";
        public static String RESTART = "Заново";

        public static String REMAIN_IH_THE_DECK = "Осталось в колоде: ";
        public static String CHOOSE_CARD_TO_PLAY = "Выберите карты для разыгрывания.";
        public static String NO_CARDS_SELECTED = "Нет выбранных карт для сброса.";
        public static String COMBINATION = "Комбинация: ";
        public static String SCORE = "Счет: ";
    }
}
