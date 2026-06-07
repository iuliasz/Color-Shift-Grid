package com.colorshiftgrid.util;

import java.util.*;
import java.util.function.Predicate;

public class PuzzleSolver {
    private static final int COLORS = 4;

    public List<Move> findShortestSolutionToUniformColor(int[][] startGrid) {
        return findShortestSolution(startGrid, state -> isUniform(decode(state, startGrid.length)));
    }

    public List<Move> findShortestSolutionToTarget(int[][] startGrid, int[][] targetGrid) {
        long targetState = encode(targetGrid);
        return findShortestSolution(startGrid, state -> state == targetState);
    }

    private List<Move> findShortestSolution(int[][] startGrid, Predicate<Long> targetPredicate) {
        int size = startGrid.length;
        long start = encode(startGrid);

        if (targetPredicate.test(start)) {
            return Collections.emptyList();
        }

        Queue<Long> queue = new ArrayDeque<>();
        Map<Long, Parent> parent = new HashMap<>();
        Set<Long> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            long current = queue.poll();

            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    long next = applyMove(current, size, row, col);

                    if (visited.contains(next)) {
                        continue;
                    }

                    visited.add(next);
                    parent.put(next, new Parent(current, new Move(row, col)));

                    if (targetPredicate.test(next)) {
                        return buildPath(start, next, parent);
                    }

                    queue.add(next);
                }
            }
        }

        return Collections.emptyList();
    }

    public long encode(int[][] grid) {
        long state = 0L;
        int bitIndex = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                state |= ((long) grid[row][col] & 3L) << bitIndex;
                bitIndex += 2;
            }
        }

        return state;
    }

    private int[][] decode(long state, int size) {
        int[][] grid = new int[size][size];
        int bitIndex = 0;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                grid[row][col] = (int) ((state >> bitIndex) & 3L);
                bitIndex += 2;
            }
        }

        return grid;
    }

    private long applyMove(long state, int size, int row, int col) {
        state = incrementCell(state, size, row, col);
        state = incrementCell(state, size, row - 1, col);
        state = incrementCell(state, size, row + 1, col);
        state = incrementCell(state, size, row, col - 1);
        state = incrementCell(state, size, row, col + 1);
        return state;
    }

    private long incrementCell(long state, int size, int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            return state;
        }

        int cellIndex = row * size + col;
        int bitIndex = cellIndex * 2;
        long mask = 3L << bitIndex;

        long value = (state & mask) >> bitIndex;
        long nextValue = (value + 1) % COLORS;

        state &= ~mask;
        state |= nextValue << bitIndex;

        return state;
    }

    private boolean isUniform(int[][] grid) {
        int first = grid[0][0];

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] != first) {
                    return false;
                }
            }
        }

        return true;
    }

    private List<Move> buildPath(long start, long end, Map<Long, Parent> parent) {
        LinkedList<Move> path = new LinkedList<>();
        long current = end;

        while (current != start) {
            Parent p = parent.get(current);
            path.addFirst(p.move);
            current = p.previousState;
        }

        return path;
    }

    private record Parent(long previousState, Move move) {
    }
}