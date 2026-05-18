package com.colorshiftgrid;

import com.colorshiftgrid.model.Board;
import com.colorshiftgrid.model.ClassicMode;
import com.colorshiftgrid.model.GameMode;
import com.colorshiftgrid.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private int steps = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int gridSize = 5;

        Board board = new Board(gridSize);
        GameView view = new GameView(gridSize);
        GameMode mode = new ClassicMode();

        // bind clicks directly
        view.bindClickHandler((row, col) -> {
            board.applyMove(row, col);
            steps++;

            view.updateGrid(board.getGrid());
            view.updateStats(steps, mode.getProgress(board));

            if (mode.checkWin(board)) {
                System.out.println("WON");
            }
        });

        view.updateGrid(board.getGrid());
        view.updateStats(steps, mode.getProgress(board));

        Scene scene = new Scene(view.createLayout(), 600, 600);

        primaryStage.setTitle("Color Shift Grid");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}