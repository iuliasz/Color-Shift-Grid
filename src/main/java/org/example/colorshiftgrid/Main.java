package org.example.colorshiftgrid; // Asigură-te că pachetul este corect

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
            // 1. Creăm instanța interfeței grafice pentru o grilă fixă de 5x5
            GameView view = new GameView(5);

            // 2. Creăm o matrice "de test" ca să vedem culorile în acțiune
            // Regulile: 0 = Roșu, 1 = Albastru, 2 = Verde, 3 = Galben
            int[][] testGrid = {
                    {0, 1, 2, 3, 0},
                    {1, 2, 3, 0, 1},
                    {2, 3, 0, 1, 2},
                    {3, 0, 1, 2, 3},
                    {0, 1, 2, 3, 0}
            };

            // 3. Trimitem matricea către GameView pentru a colora efectiv dreptunghiurile
            view.updateGrid(testGrid);

            // 4. Preluăm layout-ul construit în GameView și îl punem în scenă
            Scene scene = new Scene(view.createLayout(), 600, 600);

            primaryStage.setTitle("Color Shift Grid");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}