package com.colorshiftgrid.util;

import com.colorshiftgrid.model.Board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LevelGenerator {

    public Board generateLevel(int size, int moves) {
        Board board = new Board(size,false);
        applyRandomMoves(board, moves);
        return board;
    }

    public void applyRandomMoves(Board board, int moves) {
        int size = board.getGrid().length;

        List<int[]> positions = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                positions.add(new int[]{row, col});
            }
        }

        int[] lastMove = {-1, -1};

        for (int i = 0; i < moves; i++) {
            Collections.shuffle(positions);

            for (int[] position : positions) {
                if (position[0] == lastMove[0] && position[1] == lastMove[1]) {
                    continue;
                }

                board.applyMove(position[0], position[1]);
                lastMove = position;
                break;
            }
        }
    }
}