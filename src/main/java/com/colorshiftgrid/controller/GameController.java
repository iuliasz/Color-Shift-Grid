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
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;


import java.util.List;
import java.util.Stack;

public class GameController {
    private final Board board;
    private final GameView view;
    private final PuzzleSolver solver;
    private final Stack<GameState> history;
    private final LevelGenerator generator;

    private GameMode mode;
    private int steps;
    private int[][] currentTargetGrid;
    private List<Move> currentSolution;

    public GameController(Board board,GameView view,GameMode mode){
        this.board=board;
        this.view=view;
        this.mode=mode;
        this.history=new Stack<>();
        this.solver = new PuzzleSolver();
        this.generator=new LevelGenerator();
        this.steps = 0;
        this.currentTargetGrid = null;
        this.currentSolution = List.of();

        changeMode(getCurrentModeName());
    }

    public void handleClick(int row, int col) {
        if (mode.getMoveLimit() != -1 && steps >= mode.getMoveLimit()) {
            showMessage(Alert.AlertType.ERROR,
                    "GAME OVER",
                    "MOVE LIMIT REACHED",
                    "You used all " + mode.getMoveLimit() + " moves.\nUse UNDO, RESTART, or HINT.");
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
            this.steps=previousState.steps();

            board.resetTo(previousState.grid());

            view.clearHintHighlight();
            updateView();
        }
    }

    public void restart() {
        changeMode(getCurrentModeName());
    }

    private String getCurrentModeName() {

        if (mode instanceof ClassicMode) {
            return "Classic Mode";
        }

        if (mode instanceof ChallengeMode) {
            return "Challenge Mode";
        }

        if (mode instanceof PatternMode) {
            return "Pattern Mode";
        }

        return "Classic Mode";
    }

    public void checkWin() {
        if (mode.checkWin(board)) {
            view.clearHintHighlight();
            view.playWinSound();

            showMessage(
                    Alert.AlertType.INFORMATION,
                    "VICTORY",
                    "★  PUZZLE SOLVED  ★",
                    "Completed in " + steps + " steps!"
            );

        }
    }


    public void updateView(){
        view.updateGrid(board.getGrid());
        view.updateStats(steps, mode.getProgress(board), mode.getMoveLimit());
    }

    public void changeMode(String modeName) {
        int size = board.getSize();

        switch (modeName) {
            case "Classic Mode" -> {
                this.mode = new ClassicMode();
                this.currentTargetGrid = null;

                board.resetTo(generator.generateLevel(size, 4).getGrid());
                view.setTargetButtonVisible(false);
            }

            case "Challenge Mode" -> {
                this.mode = new ChallengeMode(15);
                this.currentTargetGrid = null;

                board.resetTo(generator.generateLevel(size, 5).getGrid());
                view.setTargetButtonVisible(false);
            }

            case "Pattern Mode" -> {
                Board startBoard = generator.generateLevel(size, 4);

                Board targetBoard = new Board(size);
                targetBoard.resetTo(startBoard.getGrid());
                generator.applyRandomMoves(targetBoard, 4);

                board.resetTo(startBoard.getGrid());

                this.currentTargetGrid = targetBoard.copyGrid();
                this.mode = new PatternMode(this.currentTargetGrid);

                view.setTargetButtonVisible(true);
            }
        }

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
                    "HINT",
                    "NO HINT NEEDED",
                    "Board is already solved or no path found."
            );
            return;
        }

        Move nextMove = currentSolution.get(0);

        view.highlightCell(nextMove.row(), nextMove.col());
        view.updateHint(
                "Click row " + (nextMove.row() + 1)
                        + ", col " + (nextMove.col() + 1)
                        + " | Optimal moves left: " + currentSolution.size()
        );
    }

    public void autoStep() {
        List<Move> solution = getCurrentSolution();

        if (!solution.isEmpty()) {
            Move move = solution.get(0);
            handleClick(move.row(), move.col());
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
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);

        DialogPane dp = alert.getDialogPane();
        dp.setStyle(
                "-fx-background-color: #0a0a14;" +
                "-fx-border-color: #00e5ff;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 6px;" +
                "-fx-background-radius: 6px;"
        );

        VBox box = getVBox(header, content);
        dp.setContent(box);

        // Style OK button
        dp.getButtonTypes().stream().findFirst().ifPresent(bt -> dp.lookupButton(bt).setStyle(
                "-fx-font-family: 'Courier New'; -fx-font-size: 13px; -fx-font-weight: bold;" +
                "-fx-text-fill: #00e5ff; -fx-background-color: transparent;" +
                "-fx-border-color: #00e5ff; -fx-border-width: 2px;" +
                "-fx-border-radius: 3px; -fx-background-radius: 3px;" +
                "-fx-padding: 6px 20px; -fx-cursor: hand;"
        ));

        alert.showAndWait();
    }

    private static @NotNull VBox getVBox(String header, String content) {
        Label headerLbl = new Label(header);
        headerLbl.setStyle(
                "-fx-font-family: 'Courier New'; -fx-font-size: 18px; -fx-font-weight: bold;" +
                "-fx-text-fill: #00e5ff;"
        );

        Label contentLbl = new Label(content);
        contentLbl.setStyle(
                "-fx-font-family: 'Courier New'; -fx-font-size: 18px;" +
                "-fx-text-fill: #e0f7ff; -fx-padding: 8 0 0 0;"
        );

        VBox box = new VBox(10, headerLbl, contentLbl);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 24px 32px;");
        return box;
    }

    public void showInfo() {
        String infoText =
                """
                        ▸ Click on cell to change its color.
                        
                        ▸ Neighbour cells (Up, Down, Left, Right)
                          will change their color simultaneously!
                        
                        ▸ CLASSIC: One color grid.
                        ▸ CHALLENGE: Win before the step limit.
                        ▸ PATTERN: Change to pattern grid.
                        
                        ▸ Press key I: info
                        ▸ Press key U: undo
                        ▸ Press key R: restart
                        ▸ Press key H: hint
                        ▸ Press key A: auto
                        ▸ Press key T: target""";


        showMessage(Alert.AlertType.INFORMATION, "TUTORIAL", "HOW TO PLAY", infoText);
    }

}

