package com.colorshiftgrid;

import com.colorshiftgrid.model.Board;
import com.colorshiftgrid.model.GameMode;
import com.colorshiftgrid.model.GameState;
import com.colorshiftgrid.view.GameView;

import java.util.Stack;

public class GameController {
    private Board board;
    private GameView view;
    private GameMode mode;
    private Stack<GameState> history;
    private int steps;
    private int[][] initialGrid;

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

    public void handleClick(int row,int col){
        history.push(new GameState(board.getGrid(),steps));
        board.applyMove(row,col);
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

    public void setMode(GameMode mode){
        this.mode=mode;
        updateView();
    }

    public boolean checkWin(){
        if(mode.checkWin(board)){
            System.out.println("🎉 FELICITĂRI! Ai câștigat în " + steps + " pași!");
            return true;
        }
        return false;
    }

    public void updateView(){
        view.updateGrid(board.getGrid());
        view.updateStats(steps, mode.getProgress(board));
    }
}
