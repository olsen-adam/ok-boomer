package com.program.main.buttons;

import com.program.main.Grid;
import com.program.main.MouseIn;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ButtonInfo extends ObjButton {
    /* ------------- INCLUDES: ------------
    int x, y;
    BttID bttID;
    Grid grid;
    mouseIn = imouseIn;
    -------------------------------------- */
    int width, height, rad;
    String text;
    //Font font = new Font("Serif", Font.BOLD, 20);
    boolean isOver = false;
    BufferedImage imgShadow;

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
        try {
            imgShadow = ImageIO.read(getClass().getResourceAsStream("/circleShadow.png"));
        } catch(IOException e) {
            e.printStackTrace();
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
        //g.setFont(font);
        int mW = imgShadow.getWidth();
        int mH = imgShadow.getHeight();
        int cH = (int) (height*0.95);
        g.drawImage(imgShadow,
                x-(mW-width)/2-1,
                y-5, mW, mH-1, null);

        //g.drawString(String.valueOf(isOver),x,y-20);
        Color icol = grid.lightLineCol;
        String s = "";
        if (isOver) {
            switch (bttID) {
                case InfoRedund:
                    s = "Redundancies are removed \n" +
                        "as part of the A* algorithm.";
                    break;
                case InfoMDist:
                    s = "Manhattan Distance and A* \n" +
                        "are independent algorithms.";
                    break;
                case InfoAStar:
                    s = "Redundancies are removed \n" +
                        "as part of the A* algorithm. \n" +
                        "Manhattan Distance and A* \n" +
                        "are independent algorithms.";
                    break;
                case InfoColor:
                    s = "Visualize how many times \n" +
                        "a tile was passed over!";
                    break;
            }
            grid.getGUI().requestDrawInfo(s, y+height+5, bttID);
            g.setColor(grid.hoverCol);
            icol = (grid.lineCol);
        } else g.setColor(grid.windowCol);

        g.fillOval(x,y,width,height);
        g.setFont(grid.fontBold);
        g.setColor(icol);
        g.drawString("i",x+12,y+20);
    }

    @Override
    public void setTint() {
        //tint(imgMark,grid.hoverCol);
    }
}
