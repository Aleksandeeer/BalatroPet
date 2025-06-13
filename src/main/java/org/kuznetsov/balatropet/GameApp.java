package org.kuznetsov.balatropet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import org.jetbrains.annotations.NotNull;
import org.kuznetsov.balatropet.constants.Constants;
import org.kuznetsov.balatropet.models.*;

import java.util.*;

public class GameApp extends Application {

    // CARDS
    private final List<Card> playerHand = new ArrayList<>();
    private final Map<Card, StackPane> cardToLabel = new HashMap<>();
    private final List<Card> selectedCards = new ArrayList<>();
    private Deck deck;

    // LABELS
    private final Label resultLabel = new Label(Constants.Messages.COMBINATION + "-");
    private final Label deckCountLabel = new Label(Constants.Messages.REMAIN_IH_THE_DECK + "-");
    private final Label scoreLabel = new Label(Constants.Messages.SCORE + "-");


    @Override
    public void start(Stage primaryStage) {
        GameState.HAND_SIZE = Constants.Init_parameters.HAND_SIZE;
        GameState.initializeLevels(1);
        deck = createDeck();

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(800, 600);

        // Контейнер для руки игрока
        HBox handBox = new HBox(10);
        handBox.setAlignment(Pos.CENTER);

        // Label с количеством оставшихся карт
        deckCountLabel.setFont(new Font(16));
        updateDeckCount(); // сразу обновим

        // Кнопки управления
        HBox buttonBox = getHBox(primaryStage);

        // Инфо о комбинации
        resultLabel.setFont(new Font(18));
        scoreLabel.setFont(new Font(18));

        // Сборка UI
        root.getChildren().addAll(handBox, deckCountLabel, buttonBox, resultLabel, scoreLabel);

        Scene scene = new Scene(root);
        primaryStage.setTitle(Constants.APP_NAME);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Нарисовать стартовую руку
        drawPlayerHand(handBox);
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

    private void drawPlayerHand(HBox handBox) {
        handBox.getChildren().clear();
        playerHand.clear();
        selectedCards.clear();
        cardToLabel.clear();

        if (deck.getDeck().size() < GameState.HAND_SIZE) {
            deck = createDeck();
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
            deck = createDeck();
        }

        // Заменяем выбранные карты новыми
        for (int i = 0; i < playerHand.size(); i++) {
            Card card = playerHand.get(i);
            if (selectedCards.contains(card)) {
                Card newCard = deck.getDeck().removeFirst();
                playerHand.set(i, newCard);
            }
        }

        selectedCards.clear();
        updateDeckCount();
        updateFullHandUI();
        resultLabel.setText("");
    }

    private void updateFullHandUI() {
        HBox handBox = (HBox) cardToLabel.values().iterator().next().getParent();
        handBox.getChildren().clear();
        cardToLabel.clear();

        for (Card card : playerHand) {
            StackPane view = createCardView(card);
            cardToLabel.put(card, view);
            handBox.getChildren().add(view);
        }
    }

    private StackPane createCardView(Card card) {
        String suit = card.getSuit().name(); // HEART, SPADE
        int nominal = card.getNominal();     // from 2 to 14
        String path = "/cards/%s/%d.png".formatted(suit, nominal);

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(60);
        imageView.setFitHeight(90);

        return getStackPane(card, imageView);
    }

    @NotNull
    private StackPane getStackPane(Card card, ImageView imageView) {
        StackPane container = new StackPane(imageView);
        container.setStyle("-fx-border-color: black; -fx-border-width: 2;");
        container.setOnMouseClicked(e -> {
            if (selectedCards.contains(card)) {
                selectedCards.remove(card);
                container.setStyle("-fx-border-color: black; -fx-border-width: 2;");
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

    private Deck createDeck() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                cards.add(new Card(
                        (i + 1) * (j + 2),
                        Suit.values()[i],
                        j + 2,
                        true,
                        Deck.getSymbols()[j],
                        Deck.getSuit()[i]
                ));
            }
        }
        return new Deck(cards);
    }

    private void updateDeckCount() {
        deckCountLabel.setText(Constants.Messages.REMAIN_IH_THE_DECK + deck.getDeck().size());
    }

    public static void main(String[] args) {
        GameState.HAND_SIZE = 9;
        launch(args);
    }
}