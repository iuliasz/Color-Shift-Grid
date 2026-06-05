package com.colorshiftgrid.view;

import com.colorshiftgrid.controller.GameController;
import com.colorshiftgrid.model.Board;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class GameView {
    private final BorderPane root;
    private final GridPane gridPane;
    private final Rectangle[][] cells;

    private final Label statsLabel;
    private final Label hintLabel;

    private final Button undoBtn;
    private final Button restartBtn;
    private final Button hintBtn;
    private final Button targetBtn;
    private final Button autoStepBtn;

    private final ComboBox<String> modeSelector;

    public GameView(int gridSize) {
        root = new BorderPane();
        gridPane = new GridPane();
        cells = new Rectangle[gridSize][gridSize];

        statsLabel = new Label("Steps: 0 | Progress: 0%");
        hintLabel = new Label("Hint: press Hint when you feel stuck.");

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(8);
        gridPane.setVgap(8);
        gridPane.setStyle("-fx-padding: 20px;");

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Rectangle rect = new Rectangle(84, 84);

                rect.setFill(Color.GRAY);
                rect.setArcWidth(18);
                rect.setArcHeight(18);
                rect.setStroke(Color.rgb(40, 40, 40));
                rect.setStrokeWidth(2);

                cells[i][j] = rect;
                gridPane.add(rect, j, i);
            }
        }

        root.setCenter(gridPane);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f9fbff, #e9eef8);");

        Label titleLabel = new Label("Color Shift Grid");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        modeSelector = new ComboBox<>();
        modeSelector.getItems().addAll("Classic Mode", "Challenge Mode", "Pattern Mode");
        modeSelector.setValue("Classic Mode");
        modeSelector.setStyle("-fx-font-size: 14px;");

        statsLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        hintLabel.setStyle("-fx-font-size: 14px;");

        VBox topBox = new VBox(10, titleLabel, modeSelector, statsLabel, hintLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-padding: 20px;");
        root.setTop(topBox);

        undoBtn = new Button("Undo");
        restartBtn = new Button("Restart");
        hintBtn = new Button("Hint");
        autoStepBtn = new Button("Auto step");
        targetBtn = new Button("Show Target");

        targetBtn.setVisible(false);

        styleButton(undoBtn);
        styleButton(restartBtn);
        styleButton(hintBtn);
        styleButton(autoStepBtn);
        styleButton(targetBtn);

        HBox bottomBox = new HBox(14, undoBtn, restartBtn, hintBtn, autoStepBtn, targetBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-padding: 20px;");
        root.setBottom(bottomBox);
    }

    public Parent createLayout() {
        return root;
    }

    public void updateGrid(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                cells[i][j].setFill(getColor(grid[i][j]));
            }
        }
    }

    public void updateStats(int steps, int progress, int moveLimit) {
        String limitText = moveLimit == -1 ? "∞" : String.valueOf(moveLimit);
        statsLabel.setText("Steps: " + steps + " / " + limitText + " | Progress: " + progress + "%");
    }

    public void updateHint(String text) {
        hintLabel.setText(text);
    }

    public void highlightCell(int row, int col) {
        clearHintHighlight();

        cells[row][col].setStroke(Color.BLACK);
        cells[row][col].setStrokeWidth(5);
    }

    public void clearHintHighlight() {
        for (Rectangle[] row : cells) {
            for (Rectangle cell : row) {
                cell.setStroke(Color.rgb(40, 40, 40));
                cell.setStrokeWidth(2);
            }
        }

        hintLabel.setText("Hint: press Hint when you feel stuck.");
    }

    private Color getColor(int value) {
        return switch (value) {
            case 0 -> Color.TOMATO;
            case 1 -> Color.GOLD;
            case 2 -> Color.YELLOWGREEN;
            case 3 -> Color.CORNFLOWERBLUE;
            default -> Color.DARKGRAY;
        };
    }

    public void bindClickHandler(java.util.function.BiConsumer<Integer, Integer> handler) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                final int row = i;
                final int col = j;
                cells[i][j].setOnMouseClicked(e -> handler.accept(row, col));
            }
        }
    }

    public void bindController(GameController controller) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                final int row = i;
                final int col = j;
                cells[i][j].setOnMouseClicked(event -> controller.handleClick(row, col));
            }
        }
        undoBtn.setOnAction(event -> controller.undo());
        restartBtn.setOnAction(event -> controller.restart());
        hintBtn.setOnAction(event -> controller.showHint());
        autoStepBtn.setOnAction(event -> controller.autoStep());

        modeSelector.setOnAction(event -> controller.changeMode(modeSelector.getValue()));
        targetBtn.setOnAction(event -> controller.showTargetPattern());
    }

    public void showTargetWindow(int[][] targetGrid) {
        Stage targetStage = new Stage();
        GridPane miniGrid = new GridPane();

        miniGrid.setAlignment(Pos.CENTER);
        miniGrid.setHgap(4);
        miniGrid.setVgap(4);
        miniGrid.setStyle("-fx-padding: 20px; -fx-background-color: #f9fbff;");

        for (int i = 0; i < targetGrid.length; i++) {
            for (int j = 0; j < targetGrid[0].length; j++) {
                Rectangle rect = new Rectangle(44, 44);

                rect.setFill(getColor(targetGrid[i][j]));
                rect.setArcWidth(8);
                rect.setArcHeight(8);

                miniGrid.add(rect, j, i);
            }
        }

        Scene targetScene = new Scene(miniGrid);

        targetStage.setTitle("Target model");
        targetStage.setScene(targetScene);
        targetStage.setAlwaysOnTop(true);
        targetStage.show();
    }
    public void setTargetButtonVisible(boolean visible) {
        targetBtn.setVisible(visible);
    }

    private void styleButton(Button button) {
        button.setStyle(
                "-fx-font-size: 14px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 8px 14px;"
                        + "-fx-background-radius: 12px;"
        );
    }

    public void render(Board board) {
        updateGrid(board.getGrid());
    }
}