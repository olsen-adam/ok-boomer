package com.program.main.buttons;

import com.program.main.Grid;
import com.program.main.MouseIn;

import java.awt.*;

public abstract class ObjButton {
    protected int x, y;
    protected BttID bttID;
    protected Grid grid;
    protected MouseIn mouseIn;

    public ObjButton(int xx, int yy, BttID ibttID, Grid igrid, MouseIn imouseIn) {
        x = xx;
        y = yy;
        bttID = ibttID;
        grid = igrid;
        mouseIn = imouseIn;
    }

    public abstract void tick();
    public abstract void render(Graphics g);
}
