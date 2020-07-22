package com.program.main.buttons;

import com.program.main.Grid;
import com.program.main.MouseIn;

import java.awt.*;
import java.awt.font.FontRenderContext;

public class ButtonToggle extends ObjButton {
    /* ------------- INCLUDES: ------------
    int x, y;
    BttID bttID;
    Grid grid;
    mouseIn = imouseIn;
    -------------------------------------- */
    int width, height;
    boolean toggled;
    String text;
    Font font = new Font("Serif", Font.BOLD, 20);

    public ButtonToggle(int xx, int yy, BttID ibttID, Grid igrid, MouseIn imouseIn) {
        super(xx, yy, ibttID, igrid, imouseIn);
        /*@Overwritten:
        public ObjButton(int xx, int yy, BttID ibttID) {
            x = xx;
            y = yy;
            bttID = ibttID;
            grid = igrid
            mouseIn = imouseIn;*/
        width = 64;
        height = 28;
        toggled = false;

        switch (bttID) {
            case ToggleRedund:
                text = "Eliminate Redundancies";
                break;
            case ToggleMDist:
                text = "Manhattan Distance";
                break;
            case ToggleAStar:
                text = "A* Algorithm";
                break;
            case ToggleColor:
                text = "Colored Floor";
                break;
        }
    }

    @Override
    public void tick() {
        boolean mClicked = mouseIn.getClicked();
        boolean isClicked = false;
        if (mClicked) {
            int mx = mouseIn.getMouseX();
            int my = mouseIn.getMouseY();
            isClicked = (mx > x && mx < x+width &&
                    my > y && my < y+height);
            if (isClicked) toggled = !toggled;
        }
        switch (bttID) {
            case ToggleRedund:
                break;
            case ToggleMDist:
                break;
            case ToggleAStar:
                break;
            case ToggleColor:
                grid.setDrawFloor(toggled);
                break;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(grid.getLineCol());
        g.drawRect(x,y,width,height);
        int tOff = 0;
        int boxS = (int) (height*0.75);
        int inBorder = (height-boxS)/2;
        if (toggled) tOff = width-boxS-inBorder*2;
        g.drawRect(x+tOff+inBorder,y+inBorder,boxS,boxS);

        int tx = 15;
        int ty = 20;
        g.drawString(text, x+width+tx, y + ty);

        //TIMES VISITED TABLE
        if (bttID == BttID.ToggleColor) {
            int tableX = x;
            int tableY = y+95;
            g.drawRect(tableX,tableY,320,90);
            g.drawString("Squares Visited: " + grid.getVisitedData(false), tableX+20, tableY+30);
            g.drawString("Squares Revisited: " + grid.getVisitedData(true), tableX+20, tableY+70);
        }
    }
}
