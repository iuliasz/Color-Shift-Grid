package com.colorshiftgrid.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    private int[][] grid;
    private int size;

    public Board(int size){
        this.size = size;
        grid = new int[size][size];

        initGrid();
    }

    private void initGrid(){
        for(int row=0; row<size; row++){
            for(int col=0; col<size; col++){
                grid[row][col]=0; // starts with red
            }
        }

        int scrambleMoves = (size <= 3) ? 5 : (size <= 4) ? 8 : 12;

        List<int[]> positions = new ArrayList<>();
        for (int row = 0; row < size; row++)
            for (int col = 0; col < size; col++)
                positions.add(new int[]{row, col});

        int[] lastMove = {-1, -1};
        for (int i = 0; i < scrambleMoves; i++) {
            Collections.shuffle(positions);
            for (int[] pos : positions) {
                if (pos[0] == lastMove[0] && pos[1] == lastMove[1]) continue;
                applyMove(pos[0], pos[1]);
                lastMove = pos;
                break;
            }
        }
    }


    public boolean isValid(int row, int col){
        return row>=0 && row<size && col>=0 && col<size;
    }

    public void updateCell(int row, int col){
        if(isValid(row, col)){
            grid[row][col] = (grid[row][col]+1)%4;
        }
    }

    public void applyMove(int row, int col){
        // selected cell
        updateCell(row,col);
        // N
        updateCell(row-1,col);
        // S
        updateCell(row+1,col);
        // E
        updateCell(row,col+1);
        // W
        updateCell(row,col-1);
    }

    public void reset(){
        initGrid();
    }

    public int[][] getGrid(){
        return grid;
    }
}
