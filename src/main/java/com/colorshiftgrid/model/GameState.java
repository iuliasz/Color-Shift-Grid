package com.colorshiftgrid.model;

public record GameState(int[][] grid, int steps) {
    public GameState(int[][] grid, int steps) {
        this.steps = steps;
        int rows = grid.length;
        int cols = grid[0].length;
        this.grid = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(grid[i], 0, this.grid[i], 0, cols);
        }
    }
}