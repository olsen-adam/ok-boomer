package com.program.main;

public class GridFunctions {
    Grid grid;

    public GridFunctions(Grid grid) {
        this.grid = grid;
    }

    public void animateMan(float manDis) {
        float mult = 0.85f;
        float subt = 0.6f;

        if (manDis > 0) {
            manDis *= mult;
            manDis -= subt;
            if (manDis < 0) manDis = 0;
        }
        grid.setmanDis(manDis);
    }
}
