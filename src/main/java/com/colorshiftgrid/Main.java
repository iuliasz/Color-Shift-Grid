package com.colorshiftgrid;

import com.colorshiftgrid.controller.GameController;
import com.colorshiftgrid.model.Board;
import com.colorshiftgrid.model.ClassicMode;
import com.colorshiftgrid.model.GameMode;
import com.colorshiftgrid.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int gridSize = 3;

        Board board = new Board(gridSize);
        GameView view = new GameView(gridSize);
        GameMode mode = new ClassicMode();

        GameController controller = new GameController(board, view, mode);
        view.bindController(controller);

        view.updateGrid(board.getGrid());
        view.updateStats(0, mode.getProgress(board), mode.getMoveLimit());

        Scene scene = new Scene(view.createLayout(), 600, 600);

        primaryStage.setTitle("Color Shift Grid");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}