package com.colorshiftgrid.util;

import com.colorshiftgrid.model.Board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelGenerator {

    public Board generateLevel(int size, int moves) {
        Board board = new Board(size);
        applyRandomMoves(board, moves);
        return board;
    }

    public int suggestedScrambleMoves(int size) {
        if (size <= 3) return 4;
        if (size == 4) return 7;
        return 10;
    }

    public Board generateDefaultLevel(int size){
        return generateLevel(size,suggestedScrambleMoves(size));
    }

    public void applyRandomMoves(Board board, int moves) {
        int size = board.getSize();

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