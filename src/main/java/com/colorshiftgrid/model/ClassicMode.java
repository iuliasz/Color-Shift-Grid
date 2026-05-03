package com.colorshiftgrid.model;

import org.jetbrains.annotations.NotNull;

public abstract class ClassicMode extends GameMode{

    @Override
    public boolean checkWin(Board board){
        int[][] grid = board.getGrid();
        int firstColor = grid[0][0];

        for(int row=0; row<grid.length; row++){
            for(int col=0; col<grid.length; col++){
                if(grid[row][col]!=firstColor){
                    return false;
                }
            }
        }

        return true;
    }

    public int getProgress(Board board){
        int[][] grid = board.getGrid();
        int[] colorCount = new int[4];

        for(int row=0; row<grid.length; row++){
            for(int col=0; col<grid.length; col++){
                colorCount[grid[row][col]]++;
            }
        }

        int maxColorCount=colorCount[0];

        for(int i=1; i<colorCount.length; i++){
            if(colorCount[i]>maxColorCount){
                maxColorCount=colorCount[i];
            }
        }
        return maxColorCount;
    }

    public int getMoveLimit(){
        return -1;
    }
}
