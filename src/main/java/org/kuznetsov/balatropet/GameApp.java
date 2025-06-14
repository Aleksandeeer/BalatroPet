package org.kuznetsov.balatropet;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.kuznetsov.balatropet.constants.Constants;
import org.kuznetsov.balatropet.models.Card;
import org.kuznetsov.balatropet.models.Deck;
import org.kuznetsov.balatropet.models.PokerHandEvaluator;
import org.kuznetsov.balatropet.models.Suit;
import javafx.stage.Stage;

import java.util.*;

public class GameApp extends Application {

    // CARDS
    private final List<Card> playerHand = new ArrayList<>();
    private final Map<Card, StackPane> cardToLabel = new HashMap<>();
    private final List<Card> selectedCards = new ArrayList<>();
    private Deck deck;

    // LABELS
    private final Label resultLabel = new Label(Constants.Messages.DEFAULT_COMBINATION);
    private final Label deckCountLabel = new Label(Constants.Messages.REMAIN_IH_THE_DECK + "-");
    private final Label scoreLabel = new Label(Constants.Messages.DEFAULT_SCORE);

    ImageView deckImage;
    HBox handBox;
    Pane overlayPane;

    Random rand = new Random();

    @Override
    public void start(Stage primaryStage) {
        initGame();

        primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(Constants.Paths.PATH_TO_LOGO))
        ));

        // UI-сцена (VBox — основной вертикальный макет)
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(Constants.Sizes.GAME_WIDTH, Constants.Sizes.GAME_HEIGHT);

        Image bgImage = new Image(Objects.requireNonNull(getClass().getResource(Constants.Paths.PATH_TO_GREEN_BACKGROUND)).toExternalForm());

        BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                bgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bgSize
        );

        root.setBackground(new Background(backgroundImage));

        // * 1. Рука игрока
        HBox handBox = new HBox(10);
        handBox.setAlignment(Pos.CENTER);

        // * 2. Очки и комбинация
        resultLabel.setFont(new Font(Constants.Sizes.FONT_SIZE));
        resultLabel.setTextFill(Constants.Colors.FONT_COLOR);
        scoreLabel.setFont(new Font(Constants.Sizes.FONT_SIZE));
        scoreLabel.setTextFill(Constants.Colors.FONT_COLOR);
        VBox infoBox = new VBox(5, scoreLabel, resultLabel);
        infoBox.setAlignment(Pos.CENTER);

        // * 3. Колода и счётчик
        deckCountLabel.setFont(new Font(Constants.Sizes.FONT_SIZE));
        updateDeckCount();
        deckImage = new ImageView(new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream(rand.nextInt(0, 1) == 1 ?
                        Constants.Paths.PATH_TO_BLACK_BACK : Constants.Paths.PATH_TO_RED_BACK))));
        deckImage.setFitWidth(Constants.Sizes.CARD_WIDTH);
        deckImage.setFitHeight(Constants.Sizes.CARD_HEIGHT);
        VBox deckBox = new VBox(5, deckCountLabel, deckImage);
        deckBox.setAlignment(Pos.CENTER);

        // * 4. Кнопки управления
        HBox buttonBox = getHBox(primaryStage);

        // Сборка интерфейса
        root.getChildren().addAll(
                handBox,      // 1
                infoBox,      // 2
                deckBox,      // 3
                buttonBox     // 4
        );

        // Поверх — слой для анимаций
        Pane overlay = new Pane();
        overlay.setPickOnBounds(false);

        StackPane stackRoot = new StackPane(root, overlay);

        Scene scene = new Scene(stackRoot);
        primaryStage.setTitle(Constants.APP_NAME);
        primaryStage.setScene(scene);
        primaryStage.show();

        this.handBox = handBox;
        this.overlayPane = overlay;

        drawPlayerHand(handBox);
    }

    private void initGame() {
        PokerHandEvaluator.setScore(0);
        PokerHandEvaluator.setPlayedHand(null);
        scoreLabel.setText(Constants.Messages.DEFAULT_SCORE);
        resultLabel.setText(Constants.Messages.DEFAULT_COMBINATION);

        GameState.HAND_SIZE = Constants.Init_parameters.HAND_SIZE;
        GameState.initializeGameState(1);

        deck = new Deck();
    }

    @NotNull
    private HBox getHBox(Stage primaryStage) {
        Button playButton = new Button(Constants.Messages.PLAY);
        playButton.setOnAction(e -> evaluatePlayedCards());

        Button discardButton = new Button(Constants.Messages.FOLD);
        discardButton.setOnAction(e -> discardAndRefill());

        Button restartButton = new Button(Constants.Messages.RESTART);
        restartButton.setOnAction(e -> start(primaryStage));

        HBox buttonBox = new HBox(15, playButton, discardButton, restartButton);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }

    // TODO: дописать и заменить в restartButton.setOnAction()
    private void restart() {
        initGame();
    }

    private void drawPlayerHand(HBox handBox) {
        handBox.getChildren().clear();
        playerHand.clear();
        selectedCards.clear();
        cardToLabel.clear();

        if (deck.getDeck().size() < GameState.HAND_SIZE) {
            deck = new Deck();
        }

        Collections.shuffle(deck.getDeck());
        for (int i = 0; i < GameState.HAND_SIZE; i++) {
            Card card = deck.getDeck().removeFirst();
            playerHand.add(card);

            StackPane view = createCardView(card);
            cardToLabel.put(card, view);
            handBox.getChildren().add(view);
        }

        updateDeckCount();
    }

    private void discardAndRefill() {
        if (selectedCards.isEmpty()) {
            resultLabel.setText(Constants.Messages.NO_CARDS_SELECTED);
            return;
        }

        if (deck.getDeck().size() < selectedCards.size()) {
            deck = new Deck();
        }

        List<Card> toReplace = new ArrayList<>(selectedCards);
        selectedCards.clear();

        for (int i = 0; i < playerHand.size(); i++) {
            Card oldCard = playerHand.get(i);
            if (toReplace.contains(oldCard)) {
                final int index = i;
                StackPane oldView = cardToLabel.get(oldCard);

                Animations.animateCardToDeck(oldView, () -> {
                    Card newCard = deck.getDeck().removeFirst();
                    playerHand.set(index, newCard);
                    StackPane newView = createCardView(newCard);
                    cardToLabel.remove(oldCard);
                    cardToLabel.put(newCard, newView);

                    Duration delay = Duration.millis(index * 150);
                    Animations.animateCardDrawToHand(
                            overlayPane,
                            handBox,
                            index,
                            newView,
                            deckImage,
                            rand.nextInt(0,1) == 1 ? Constants.Paths.PATH_TO_BLACK_BACK :
                                    Constants.Paths.PATH_TO_RED_BACK,
                            delay
                    );
                });
            }
        }

        updateDeckCount();
        resultLabel.setText("");
    }

    private StackPane createCardView(Card card) {
        String suit = card.getSuit().name(); // HEART, SPADE
        int nominal = card.getNominal();     // from 2 to 14

        Image image = new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream(Constants.Paths.PATH_TO_CARD.formatted(suit, nominal))));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(Constants.Sizes.CARD_WIDTH);
        imageView.setFitHeight(Constants.Sizes.CARD_HEIGHT);

        return getStackPane(card, imageView);
    }

    @NotNull
    private StackPane getStackPane(Card card, ImageView imageView) {
        StackPane container = new StackPane(imageView);
        container.setStyle(Constants.Styles.BLACK_BORDER + "-fx-border-width: 2;");
        container.setOnMouseClicked(e -> {
            if (selectedCards.contains(card)) {
                selectedCards.remove(card);
                container.setStyle(Constants.Styles.BLACK_BORDER + "-fx-border-width: 2;");
            } else {
                if (selectedCards.size() < 5) {
                    selectedCards.add(card);
                    container.setStyle("-fx-border-color: red; -fx-border-width: 3;");
                }
            }
        });
        return container;
    }

    private void evaluatePlayedCards() {
        if (selectedCards.isEmpty()) {
            resultLabel.setText(Constants.Messages.CHOOSE_CARD_TO_PLAY);
        } else {
            PokerHandEvaluator.evaluateHand(selectedCards);
            scoreLabel.setText(Constants.Messages.SCORE + PokerHandEvaluator.getScore());
            resultLabel.setText(Constants.Messages.COMBINATION + PokerHandEvaluator.getPlayedHand().getRussianName());
        }
    }

    private void updateDeckCount() {
        deckCountLabel.setText(Constants.Messages.REMAIN_IH_THE_DECK + deck.getDeck().size());
        deckCountLabel.setTextFill(Constants.Colors.FONT_COLOR);
    }

    public static void main(String[] args) {
        GameState.HAND_SIZE = 9;
        launch(args);
    }
}