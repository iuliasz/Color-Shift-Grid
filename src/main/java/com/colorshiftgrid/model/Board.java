package com.colorshiftgrid.model;

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
                grid[row][col]=0;
            }
        }

        int moves = 4;

        for(int i=0; i<moves; i++){

            int row=(int)(Math.random()*size);
            int col=(int)(Math.random()*size);

            applyMove(row,col);
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
