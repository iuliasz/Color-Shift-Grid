package com.colorshiftgrid.model;

public class GameState {
    private int[][] grid;
    private int steps;

    public GameState(int[][] grid, int steps) {
        this.steps = steps;
        int rows = grid.length;
        int cols = grid[0].length;
        this.grid = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.grid[i][j] = grid[i][j];
            }
        }
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getSteps() {
        return steps;
    }
}