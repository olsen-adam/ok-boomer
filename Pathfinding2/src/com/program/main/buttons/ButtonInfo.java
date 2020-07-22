package com.program.main.buttons;

import com.program.main.Grid;
import com.program.main.MouseIn;

import java.awt.*;
import java.awt.font.FontRenderContext;

public class ButtonInfo extends ObjButton {
    /* ------------- INCLUDES: ------------
    int x, y;
    BttID bttID;
    Grid grid;
    mouseIn = imouseIn;
    -------------------------------------- */
    int width, height, rad;
    String text;
    Font font = new Font("Serif", Font.BOLD, 20);
    boolean isOver = false;

    public ButtonInfo(int xx, int yy, BttID ibttID, Grid igrid, MouseIn imouseIn) {
        super(xx, yy, ibttID, igrid, imouseIn);
        /*@Overwritten:
        public ObjButton(int xx, int yy, BttID ibttID) {
            x = xx;
            y = yy;
            bttID = ibttID;
            grid = igrid
            mouseIn = imouseIn;*/
        width = 28;
        height = width;
        rad = width/2;

        switch (bttID) {
            case InfoRedund:
                text = "Eliminate Redundancies";
                break;
            case InfoMDist:
                text = "Manhattan Distance";
                break;
            case InfoAStar:
                text = "A* Algorithm";
                break;
            case InfoColor:
                text = "Colored Floor";
                break;
        }
    }

    @Override
    public void tick() {
        int mx = mouseIn.getMouseX();
        int my = mouseIn.getMouseY();
        //isOver = (mx > x && mx < x+width &&
         //       my > y && my < y+height);
        int cX = x + width/2;
        int cY = y + height/2;
        float cxd = Math.abs(mx-cX);
        float cyd = Math.abs(my-cY);
        isOver = (Math.pow(cxd,2) + Math.pow(cyd,2) <= Math.pow(rad, 2));
    }

    @Override
    public void render(Graphics g) {
        g.setFont(font);

        g.drawOval(x,y,width,height);
        g.drawString("i",x+12,y+20);

        //g.drawString(String.valueOf(isOver),x,y-20);
    }
}
