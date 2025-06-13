package org.kuznetsov.balatropet;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.InputStream;

public class Animations {

    public static int ANIMATION_DURATION_MS = 200;

    public static void animateCardToDeck(Node cardNode, Runnable after) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ANIMATION_DURATION_MS), cardNode);
        tt.setToX(300);  // X-смещение вправо (примерно к колоде)
        tt.setToY(-50);  // вверх немного
        tt.setOnFinished(e -> after.run());
        tt.play();
    }

    public static void animateCardDrawToHand(
            Pane overlay,
            HBox handBox,
            int index,
            StackPane finalCardView,
            Node deckNode,
            String backImagePath,
            Duration delay
    ) {
        if (deckNode == null || finalCardView == null || overlay == null || handBox.getScene() == null)
            return;

        Platform.runLater(() -> {
            // Позиции в координатах сцены
            Point2D deckScene = deckNode.localToScene(0, 0);
            Node targetPlaceholder = handBox.getChildren().get(index);
            Point2D targetScene = targetPlaceholder.localToScene(0, 0);

            // Конвертируем их в координаты overlay
            Point2D deckPos = overlay.sceneToLocal(deckScene);
            Point2D targetPos = overlay.sceneToLocal(targetScene);

            InputStream backStream = Animations.class.getResourceAsStream(backImagePath);
            if (backStream == null) {
                System.err.println("❌ Не найден файл рубашки по пути: " + backImagePath);
                return;
            }

            ImageView backImage = new ImageView(new Image(backStream));
            backImage.setFitWidth(60);
            backImage.setFitHeight(90);

            StackPane animatedCard = new StackPane(backImage);
            animatedCard.setLayoutX(deckPos.getX());
            animatedCard.setLayoutY(deckPos.getY());

            overlay.getChildren().add(animatedCard); // добавляем карту поверх

            PauseTransition wait = new PauseTransition(delay);
            wait.setOnFinished(e -> {
                TranslateTransition move = new TranslateTransition(Duration.millis(300), animatedCard);
                move.setToX(targetPos.getX() - deckPos.getX());
                move.setToY(targetPos.getY() - deckPos.getY());

                RotateTransition flip = new RotateTransition(Duration.millis(400), animatedCard);
                flip.setAxis(Rotate.Y_AXIS);
                flip.setFromAngle(0);
                flip.setToAngle(180);

                ParallelTransition transition = new ParallelTransition(move, flip);
                transition.setOnFinished(ev -> {
                    overlay.getChildren().remove(animatedCard);
                    handBox.getChildren().set(index, finalCardView);
                });

                transition.play();
            });
            wait.play();
        });
    }

}
