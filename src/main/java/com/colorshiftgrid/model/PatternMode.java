package com.colorshiftgrid.model;

public class PatternMode extends GameMode {
    private int[][] targetGrid;

    public PatternMode(int[][] targetGrid) {
        this.targetGrid = targetGrid;
    }

    @Override
    public boolean checkWin(Board board) {
        int[][] grid = board.getGrid();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] != targetGrid[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getProgress(Board board) {
        int[][] grid = board.getGrid();
        int totalCells = grid.length * grid[0].length;
        int matchingCells = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == targetGrid[row][col]) {
                    matchingCells++;
                }
            }
        }
        return (matchingCells * 100) / totalCells;
    }

    @Override
    public int getMoveLimit() {
        return -1;
    }
}