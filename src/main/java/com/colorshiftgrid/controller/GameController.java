package com.colorshiftgrid.controller;

import com.colorshiftgrid.model.Board;
import com.colorshiftgrid.model.GameMode;
import com.colorshiftgrid.model.GameState;
import com.colorshiftgrid.util.Move;
import com.colorshiftgrid.util.PuzzleSolver;
import com.colorshiftgrid.view.GameView;
import com.colorshiftgrid.model.ClassicMode;
import com.colorshiftgrid.model.ChallengeMode;
import com.colorshiftgrid.model.PatternMode;
import com.colorshiftgrid.util.LevelGenerator;
import javafx.scene.control.Alert;

import java.util.List;
import java.util.Stack;

public class GameController {
    private Board board;
    private GameView view;
    private final PuzzleSolver solver;
    private Stack<GameState> history;

    private GameMode mode;
    private int steps;
    private int[][] initialGrid;
    private int[][] currentTargetGrid;
    private List<Move> currentSolution;

    public GameController(Board board,GameView view,GameMode mode){
        this.board=board;
        this.view=view;
        this.mode=mode;
        this.history=new Stack<>();
        this.solver = new PuzzleSolver();
        this.steps = 0;
        this.initialGrid = board.copyGrid();
        this.currentTargetGrid = null;
        this.currentSolution = List.of();

        updateView();
    }

    public void handleClick(int row, int col) {
        if (mode.getMoveLimit() != -1 && steps >= mode.getMoveLimit()) {
            showMessage(
                    Alert.AlertType.ERROR,
                    "Game Over",
                    "You lost!",
                    "You reached the move limit: " + mode.getMoveLimit()
                            + ". Use Undo, Restart or Hint and try again."
            );
            return;
        }
        history.push(new GameState(board.getGrid(), steps));
        board.applyMove(row, col);
        steps++;

        view.clearHintHighlight();
        updateView();
        checkWin();
    }

    public void undo(){
        if(!history.isEmpty()){
            GameState previousState=history.pop();
            this.steps=previousState.getSteps();

            board.resetTo(previousState.getGrid());

            view.clearHintHighlight();
            updateView();
        }
    }

    public void restart(){
        board.resetTo(initialGrid);
        history.clear();
        steps = 0;

        view.clearHintHighlight();
        updateView();
    }

    public void setMode(GameMode mode){
        this.mode=mode;
        updateView();
    }

    public boolean checkWin() {
        if (mode.checkWin(board)) {
            view.clearHintHighlight();

            showMessage(
                    Alert.AlertType.INFORMATION,
                    "VICTORY",
                    "🎉 WELL DONE 🎉",
                    "Solved the puzzle in " + steps + " steps!"
            );

            return true;
        }
        return false;
    }


    public void updateView(){
        view.updateGrid(board.getGrid());
        view.updateStats(steps, mode.getProgress(board), mode.getMoveLimit());
    }

    public void changeMode(String modeName) {
        LevelGenerator generator = new LevelGenerator();
        int size = board.getGrid().length;

        switch (modeName) {
            case "Classic Mode" -> {
                this.mode = new ClassicMode();
                this.currentTargetGrid = null;

                board.resetTo(generator.generateLevel(size, 4).getGrid());
                view.setTargetButtonVisible(false);
            }

            case "Challenge Mode" -> {
                this.mode = new ChallengeMode(12);
                this.currentTargetGrid = null;

                board.resetTo(generator.generateLevel(size, 5).getGrid());
                view.setTargetButtonVisible(false);
            }

            case "Pattern Mode" -> {
                Board startBoard = generator.generateLevel(size, 4);

                Board targetBoard = new Board(size, false);
                targetBoard.resetTo(startBoard.getGrid());
                generator.applyRandomMoves(targetBoard, 4);

                board.resetTo(startBoard.getGrid());

                this.currentTargetGrid = targetBoard.copyGrid();
                this.mode = new PatternMode(this.currentTargetGrid);

                view.setTargetButtonVisible(true);
            }
        }

        this.initialGrid = board.copyGrid();
        history.clear();
        steps = 0;

        view.clearHintHighlight();
        updateView();
    }

    public void showTargetPattern() {
        if (currentTargetGrid != null) {
            view.showTargetWindow(currentTargetGrid);
        }
    }

    public void showHint() {
        currentSolution = getCurrentSolution();

        if (currentSolution.isEmpty()) {
            showMessage(
                    Alert.AlertType.INFORMATION,
                    "Hint",
                    "No hint needed",
                    "The board is already solved or no path was found."
            );
            return;
        }

        Move nextMove = currentSolution.get(0);

        view.highlightCell(nextMove.getRow(), nextMove.getCol());
        view.updateHint(
                "Hint: click row " + (nextMove.getRow() + 1)
                        + ", column " + (nextMove.getCol() + 1)
                        + " | Remaining optimal moves: " + currentSolution.size()
        );
    }

    public void autoStep() {
        List<Move> solution = getCurrentSolution();

        if (!solution.isEmpty()) {
            Move move = solution.get(0);
            handleClick(move.getRow(), move.getCol());
        }
    }

    private List<Move> getCurrentSolution() {
        if (currentTargetGrid != null) {
            return solver.findShortestSolutionToTarget(board.getGrid(), currentTargetGrid);
        }

        return solver.findShortestSolutionToUniformColor(board.getGrid());
    }

    private void showMessage(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

