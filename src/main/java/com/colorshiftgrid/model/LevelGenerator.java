package com.colorshiftgrid.model;

import java.util.Random;

public class LevelGenerator {

    public Board generateLevel(int size, int moves) {
        Board board = new Board(size);
        applyRandomMoves(board, moves);
        return board;
    }

    public void applyRandomMoves(Board board, int moves) {
        Random random = new Random();
        int size = board.getGrid().length;
        for (int i = 0; i < moves; i++) {
            int randomRow = random.nextInt(size);
            int randomCol = random.nextInt(size);
            board.applyMove(randomRow, randomCol);
        }
    }
}