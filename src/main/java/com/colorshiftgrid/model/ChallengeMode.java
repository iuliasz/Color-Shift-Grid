package com.colorshiftgrid.model;

public class ChallengeMode extends GameMode {
    private int moveLimit;

    public ChallengeMode(int moveLimit) {
        this.moveLimit = moveLimit;
    }

    @Override
    public boolean checkWin(Board board) {
        int[][] grid = board.getGrid();
        int firstColor = grid[0][0];

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] != firstColor) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getProgress(Board board) {
        int[][] grid = board.getGrid();
        int[] colorCount = new int[4];
        int totalCells = grid.length * grid[0].length;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                colorCount[grid[row][col]]++;
            }
        }

        int maxColorCount = colorCount[0];
        for (int i = 1; i < colorCount.length; i++) {
            if (colorCount[i] > maxColorCount) {
                maxColorCount = colorCount[i];
            }
        }
        return (maxColorCount * 100) / totalCells;
    }

    @Override
    public int getMoveLimit() {
        return moveLimit;
    }
}