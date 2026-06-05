package com.colorshiftgrid.controller;

import com.colorshiftgrid.model.Board;
import com.colorshiftgrid.model.GameMode;
import com.colorshiftgrid.model.GameState;
import com.colorshiftgrid.view.GameView;
import com.colorshiftgrid.model.ClassicMode;
import com.colorshiftgrid.model.ChallengeMode;
import com.colorshiftgrid.model.PatternMode;
import com.colorshiftgrid.model.LevelGenerator;
import javafx.scene.control.Alert;

import java.util.Stack;

public class GameController {
    private Board board;
    private GameView view;
    private GameMode mode;
    private Stack<GameState> history;
    private int steps;
    private int[][] initialGrid;
    private int[][] currentTargetGrid = null;

    public GameController(Board board,GameView view,GameMode mode){
        this.board=board;
        this.view=view;
        this.mode=mode;
        this.history=new Stack<>();
        this.steps=0;

        int[][] currGrid= board.getGrid();
        this.initialGrid=new int[currGrid.length][currGrid[0].length];
        for(int i=0;i<currGrid.length;i++){
            for(int j=0;j<currGrid.length;j++){
                this.initialGrid[i][j]=currGrid[i][j];
            }
        }

        updateView();
    }

    public void handleClick(int row, int col) {
        if (mode.getMoveLimit() != -1 && steps >= mode.getMoveLimit()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Game Over");
            alert.setHeaderText("Ai pierdut! 💀");
            alert.setContentText("Ai atins limita maximă de " + mode.getMoveLimit() + " pași. Folosește butonul de Undo sau Restart pentru a încerca din nou.");
            alert.showAndWait();
            return;
        }
        history.push(new GameState(board.getGrid(), steps));
        board.applyMove(row, col);
        steps++;
        updateView();
        checkWin();
    }

    public void undo(){
        if(!history.isEmpty()){
            GameState previousState=history.pop();
            this.steps=previousState.getSteps();
            int[][] savedGrid=previousState.getGrid();
            int[][] currGrid=board.getGrid();
            for(int i=0;i<currGrid.length;i++){
                for(int j=0;j< currGrid.length;j++){
                    currGrid[i][j]=savedGrid[i][j];
                }
            }

            updateView();
        }
    }

    public void restart(){
        int[][] currGrid= board.getGrid();
        for(int i=0;i<currGrid.length;i++){
            for(int j=0;j<currGrid.length;j++){
                currGrid[i][j]=initialGrid[i][j];
            }
        }
        history.clear();
        steps=0;
        updateView();
    }

    public boolean checkWin() {
        if (mode.checkWin(board)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Victorie");
            alert.setHeaderText("🎉 FELICITĂRI! 🎉");
            alert.setContentText("Ai rezolvat puzzle-ul în " + steps + " pași!");
            alert.showAndWait();
            return true;
        }
        return false;
    }

    public void updateView(){
        view.updateGrid(board.getGrid());
        view.updateStats(steps, mode.getProgress(board));
    }

    public void changeMode(String modeName) {
        switch (modeName) {
            case "Classic Mode":
                this.mode = new ClassicMode();
                this.currentTargetGrid = null;
                view.setTargetButtonVisible(false);
                break;

            case "Challenge Mode":
                this.mode = new ChallengeMode(20);
                this.currentTargetGrid = null;
                view.setTargetButtonVisible(false);
                break;

            case "Pattern Mode":
                LevelGenerator generator = new LevelGenerator();
                Board targetBoard = generator.generateLevel(board.getGrid().length, 5);
                this.currentTargetGrid = targetBoard.getGrid();
                this.mode = new PatternMode(this.currentTargetGrid);
                view.setTargetButtonVisible(true);
                break;
        }
        restart();
    }
    public void showTargetPattern() {
        if (currentTargetGrid != null) {
            view.showTargetWindow(currentTargetGrid);
        }
    }
}
