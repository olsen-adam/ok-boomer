package com.program.main.buttons;

import com.program.main.Grid;
import com.program.main.MouseIn;

import java.awt.*;
import java.awt.font.FontRenderContext;

public class ButtonBar extends ObjButton {
    /* ------------- INCLUDES: ------------
    int x, y;
    BttID bttID;
    Grid grid;
    mouseIn = imouseIn;
    -------------------------------------- */
    int width, height, barWidth, baseBarW;
    String text;
    Font font = new Font("Serif", Font.BOLD, 20);
    boolean held = false;

    public ButtonBar(int xx, int yy, BttID ibttID, Grid igrid, MouseIn imouseIn) {
        super(xx, yy, ibttID, igrid, imouseIn);
        /*@Overwritten:
        public ObjButton(int xx, int yy, BttID ibttID) {
            x = xx;
            y = yy;
            bttID = ibttID;
            grid = igrid
            mouseIn = imouseIn;*/
        width = 200;
        barWidth = (int) (width*0.3);
        baseBarW = barWidth;
        height = 30;
        text = "Speed";
    }

    @Override
    public void tick() {
        int mx = mouseIn.getMouseX();
        int my = mouseIn.getMouseY();
        if (held) {
            barWidth = mx-x;
            barWidth = clamp(barWidth,(int)(width*0.1), width);
            if (mouseIn.getReleased()) held = false;
            int newSpd;
            if (barWidth == width) {
                newSpd = 1;
            } else {
                int diff = Math.abs(baseBarW - barWidth);
                if (barWidth <= baseBarW) {
                    newSpd = (int) (30 + diff * 1.8);
                } else {
                    newSpd = 30 - 26 * (barWidth - baseBarW) / (width - baseBarW);
                }
            }
            //System.out.println((newSpd));
            grid.setMoveTime(newSpd);
        } else {
            if (mx > x && mx < x + width &&
                    my > y && my < y + height) {
                if (mouseIn.getPressed()) {
                    held = true;
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(grid.getLineCol());
        g.fillRect(x,y,barWidth,height);
        g.drawRect(x,y,width,height);
        g.drawString(text,x-90,y+20);
    }

    public void resetBar() {
        barWidth = baseBarW;
    }

    public int clamp(int var, int min, int max) {
        if (var < min) var = min;
        if (var > max) var = max;
        return var;
    }
}