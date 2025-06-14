package org.kuznetsov.balatropet.constants;

import lombok.Getter;
import lombok.Setter;

import javafx.scene.paint.Color;

@Getter
@Setter
public class Constants {
    public static String APP_NAME = "Balatro Pet Clone";

    public static class Styles {
        public static String BLACK_BORDER = "-fx-border-color: black; ";
    }

    public static class Colors {
        public static Color FONT_COLOR = Color.WHITE;
    }

    public static class Sizes {
        public static int CARD_WIDTH = 60;
        public static int CARD_HEIGHT = 90;
        public static int FONT_SIZE = 18;

        public static int GAME_WIDTH = 800;
        public static int GAME_HEIGHT = 535;
    }

    public static class Paths {
        public static String PATH_TO_ROOT = "/cards";
        public static String PATH_TO_BLACK_BACK = PATH_TO_ROOT + "/back/backB.png";
        public static String PATH_TO_RED_BACK = PATH_TO_ROOT + "/back/backR.png";
        public static String PATH_TO_CARD = PATH_TO_ROOT + "/%s/%d.png";
        public static String PATH_TO_GREEN_BACKGROUND = PATH_TO_ROOT + "/background/Green_back.jpg";
    }

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

        public static String DEFAULT_SCORE = SCORE + "-";
        public static String DEFAULT_COMBINATION = COMBINATION + "-";
    }
}
