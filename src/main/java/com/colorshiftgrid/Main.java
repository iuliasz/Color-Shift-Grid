package com.colorshiftgrid;

import com.colorshiftgrid.controller.GameController;
import com.colorshiftgrid.model.Board;
import com.colorshiftgrid.model.ClassicMode;
import com.colorshiftgrid.model.GameMode;
import com.colorshiftgrid.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

public class Main extends Application {

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

        Scene scene = new Scene(view.createLayout(), 640, 680);
        scene.setFill(Color.web("#0a0a14"));

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.U) {
                controller.undo();
            } else if (event.getCode() == KeyCode.Z && event.isControlDown()) {
                controller.undo();
            } else if (event.getCode() == KeyCode.R) {
                controller.restart();
            } else if (event.getCode() == KeyCode.H) {
                controller.showHint();
            }else if (event.getCode() == KeyCode.A) {
                controller.autoStep();
            }else if (event.getCode() == KeyCode.I) {
                controller.showInfo();
            }else if (event.getCode() == KeyCode.T) {
                controller.showTargetPattern();
            }
        });

        primaryStage.setTitle("Color Shift Grid");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}