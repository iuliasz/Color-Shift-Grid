package com.colorshiftgrid;

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
        try {
            // grid 5x5
            GameView view = new GameView(5);

            // create a test matrix
            // rules: 0=red, 1=blue, 2=green, 3=yellow
            int[][] testGrid = {
                    {0, 1, 2, 3, 0},
                    {1, 2, 3, 0, 1},
                    {2, 3, 0, 1, 2},
                    {3, 0, 1, 2, 3},
                    {0, 1, 2, 3, 0}
            };

            // color the grid
            view.updateGrid(testGrid);

            // take the layout
            Scene scene = new Scene(view.createLayout(), 600, 600);

            primaryStage.setTitle("Color Shift Grid");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}