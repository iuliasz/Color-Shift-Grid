package com.colorshiftgrid;

import com.colorshiftgrid.controller.GameController;
import com.colorshiftgrid.model.Board;
import com.colorshiftgrid.model.ClassicMode;
import com.colorshiftgrid.model.GameMode;
import com.colorshiftgrid.util.LevelGenerator;
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

        Board board=new Board(gridSize);
        LevelGenerator generator=new LevelGenerator();
        generator.applyRandomMoves(board,5);
        GameView view = new GameView(gridSize);
        GameMode mode = new ClassicMode();

        GameController controller=new GameController(board,view,mode);
        view.bindController(controller);

        Scene scene = new Scene(view.createLayout(), 600, 600);
        primaryStage.setTitle("Color Shift Grid");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}