package com.colorshiftgrid.view;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GameView {
    private BorderPane root;
    private GridPane gridPane;
    private Rectangle[][] cells;
    private Label statsLabel;

    public GameView(int gridSize) {
        root = new BorderPane();
        gridPane = new GridPane();
        cells = new Rectangle[gridSize][gridSize];
        statsLabel = new Label("Steps: 0 | Progress: 0%");

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(5); // spațiu orizontal între pătrățele
        gridPane.setVgap(5); // spațiu vertical între pătrățele

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Rectangle rect = new Rectangle(80, 80);
                rect.setFill(Color.GRAY);
                rect.setArcWidth(10);     // colțuri rotunjite pentru un design drăguț
                rect.setArcHeight(10);
                cells[i][j] = rect;
                gridPane.add(rect, j, i);
            }
        }

        root.setCenter(gridPane);
        VBox topBox = new VBox(statsLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-padding: 20px; -fx-font-size: 18px; -fx-font-weight: bold;");
        root.setTop(topBox);
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

    public void updateStats(int steps, int progress) {
        statsLabel.setText("Steps: " + steps + " | Progress: " + progress + "%");
    }

    private Color getColor(int value) {
        return switch (value) {
            case 0 -> Color.RED;
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.YELLOW;
            default -> Color.DARKGRAY;
        };
    }

    /*
    public void bindController(GameController controller) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                final int row = i;
                final int col = j;
                cells[i][j].setOnMouseClicked(event -> controller.handleClick(row, col));
            }
        }
    }
    public void render(Board board) {
        updateGrid(board.getGrid());
    }
    */
}