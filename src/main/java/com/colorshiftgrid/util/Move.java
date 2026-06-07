package com.colorshiftgrid.util;

import org.jetbrains.annotations.NotNull;

public record Move(int row, int col) {

    @Override
    public @NotNull String toString() {
        return "(" + (row + 1) + ", " + (col + 1) + ")";
    }
}