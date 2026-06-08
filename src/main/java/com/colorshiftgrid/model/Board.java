package com.colorshiftgrid.model;

public class Board {
    private final int[][] grid;
    private final int size;

    public Board(int size){
        this.size = size;
        this.grid = new int[size][size];
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

    public void resetTo(int[][] sourceGrid) {
        for (int row = 0; row < size; row++) {
            System.arraycopy(sourceGrid[row], 0, grid[row], 0, size);
        }
    }

    public int[][] getGrid(){
        return grid;
    }

    public int[][] copyGrid() {
        int[][] copy = new int[size][size];

        for (int row = 0; row < size; row++) {
            System.arraycopy(grid[row], 0, copy[row], 0, size);
        }

        return copy;
    }

    public int getSize() {
        return size;
    }
}
