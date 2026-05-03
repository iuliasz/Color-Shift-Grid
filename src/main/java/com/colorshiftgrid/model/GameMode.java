package com.colorshiftgrid.model;

public abstract class GameMode {

    public abstract boolean checkWin(Board board);

    public abstract int getProgess(Board board);

    public abstract int getMoveLimit();
}
