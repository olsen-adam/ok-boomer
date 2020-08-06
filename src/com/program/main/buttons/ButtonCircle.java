package com.program.main.buttons;

import com.program.main.GUIdrawType;
import com.program.main.Grid;
import com.program.main.MouseIn;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.program.main.buttons.BttID.CircleNext;

public class ButtonCircle extends ObjButton{
    /* ------------- INCLUDES: ------------
    int x, y;
    BttID bttID;
    Grid grid;
    mouseIn = imouseIn;
    -------------------------------------- */
    public int rad;
    private boolean mOver, drawCircle, held;
    private BufferedImage icon;
    private float rVal, gVal, bVal;

    private BufferedImage imgOther; //ImgOther only for start button
    public boolean forward; //Only for next button
    public final int buttonNextRad; //For Start/Next

    public ButtonCircle(int xx, int yy, BttID ibttID, Grid igrid, MouseIn imouseIn) {
        super(xx, yy, ibttID, igrid, imouseIn);
        /*@Overwritten:
        public ObjButton(int xx, int yy, BttID ibttID) {
            x = xx;
            y = yy;
            bttID = ibttID;
            grid = igrid
            mouseIn = imouseIn;*/
        Color defaultCol = grid.windowCol;
        rVal = defaultCol.getRed();
        gVal = defaultCol.getGreen();
        bVal = defaultCol.getBlue();

        buttonNextRad = 18;

        switch (bttID) {
            case CircleStart:
                rad = 24;
                BttManager temp = grid.getBttManager();
                try {
                    icon = ImageIO.read(getClass().getResourceAsStream("/start.png"));
                    imgOther = ImageIO.read(getClass().getResourceAsStream("/stop.png"));
                } catch(IOException e) {
                    e.printStackTrace();
                }
                    //Create Circle Nexts
                    BttManager btm = grid.getBttManager();
                    int xOff = 14;
                    ButtonCircle btt = new ButtonCircle(x-xOff-buttonNextRad*2,y+(rad-buttonNextRad),
                            CircleNext,grid,mouseIn);
                        btt.forward = false;
                    btm.addButton(btt);
                    btt = new ButtonCircle(x+rad*2+xOff,y+(rad-buttonNextRad),
                            CircleNext,grid,mouseIn);
                    btt.forward = true;
                    btm.addButton(btt);
                break;
            case CircleReset:
                rad = 20;
                try {
                    icon = ImageIO.read(getClass().getResourceAsStream("/refresh.png"));
                } catch(IOException e) {
                    e.printStackTrace();
                }
                break;
            case CircleNext:
                rad = buttonNextRad;
                try {
                    icon = ImageIO.read(getClass().getResourceAsStream("/nextmovearrow.png"));
                } catch(IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        setTint();
    }

    @Override
    public void tick() {
        int mx = mouseIn.getMouseX();
        int my = mouseIn.getMouseY();
        int cX = x + rad;
        int cY = y + rad;
        float cxd = Math.abs(mx-cX);
        float cyd = Math.abs(my-cY);
        mOver = (Math.pow(cxd,2) + Math.pow(cyd,2) <= Math.pow(rad, 2));
        boolean isClicked;
        if (mOver && mouseIn.getClicked()) isClicked = true;
        else isClicked = false;

        //Darken
        if (mouseIn.getPressed() && mOver) {
            Color col = grid.clickCol;
            rVal = col.getRed();
            gVal = col.getGreen();
            bVal = col.getBlue();
            held = true;
        } else if (mouseIn.getReleased() || !mOver) {
            held = false;
        }

        /// TICK FROM STATE
        switch (bttID) {
            case CircleStart:
                if (isClicked) {
                    if (!(grid.getMoveID() == grid.getNumMoves())) {
                        grid.flipRunning();
                    }
                }
                break;
            case CircleReset:
                if (isClicked) {
                    grid.resetBoard();
                }
                break;
            case CircleNext:
                if (isClicked) {
                    if (grid.getifRunning()) grid.flipRunning();
                    grid.moveFromArray(forward);
                }
                break;
        }
        //Animate
        Color colTo;
        if (mOver) colTo = grid.hoverCol;
        else colTo = grid.windowCol;
        if (mOver || (!(rVal == colTo.getRed()) || !(gVal == colTo.getGreen()) || !(bVal == colTo.getBlue()))) {
            //Change Col
            if (!held) {
                rVal = changeCol(rVal, colTo.getRed());
                gVal = changeCol(gVal, colTo.getGreen());
                bVal = changeCol(bVal, colTo.getBlue());
            }
            drawCircle = true;
        } else drawCircle = false;
    }

    @Override
    public void render(Graphics g) {
        int xOff = 0, yOff = 0;
        switch (bttID) {
            case CircleStart:
                xOff = 3;
                yOff = -1;
                break;
            case CircleReset:
                //xOff = 1;
                yOff = -1;
                break;
            case CircleNext:
                //xOff = 6;
                yOff = -1;
                break;
        }

        if (drawCircle) {
            g.setColor(new Color((int) rVal, (int) gVal, (int) bVal));
            g.fillOval(x, y, rad * 2, (int) (rad * 2 * 0.95));
        }
        int iW = icon.getWidth(), iH = icon.getHeight();
        if (!forward) g.drawImage(icon, x+(rad*2-iW)/2+xOff, y+(rad*2-iH)/2+yOff, iW, iH, null);
        else {
            Graphics2D g2 = (Graphics2D)g;
            g2.drawImage(icon, x+(rad*2-iW)/2+xOff + iW, y+(rad*2-iH)/2+yOff, -iW, iH, null);
        }
    }

    /*public void arrowDetect(int mx, int my) {
        if (bttID == BttID.TextStart) {
            //System.out.println("if Running works");
            //System.out.println(iAw);
            int c = 10;
            if (mx > x - Ax - iAw && mx < x - Ax && my > y + c && my < y + Ay) { //back
                //System.out.println("left");
                grid.moveFromArray(false);
            }
            if (mx > x + width + Ax && mx <  x + width + Ax + iAw && my > y + c && my < y + Ay) { //forward
                //System.out.println("right");
                grid.moveFromArray(true);
            }
        }
    }*/

    private static void tint(BufferedImage img, Color col) {

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color pixelColor = new Color(img.getRGB(x, y), true);
                int r = (col.getRed());
                int g = (col.getGreen());
                int b = (col.getBlue());
                int a = pixelColor.getAlpha();
                int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                img.setRGB(x, y, rgba);
            }
        }
    }

    public void swapStartImage() {
        BufferedImage temp = icon;
        icon = imgOther;
        imgOther = temp;
    }

    private float changeCol(float colVal, int colValTo) {
        float mult = 0.10f;
        colVal += ((float) colValTo-colVal)*mult;
        if (Math.abs((float) colValTo-colVal) < 1) colVal = colValTo;
        return colVal;
    }

    @Override
    public void setTint() {
        tint(icon,grid.lineCol);
        if (bttID == BttID.CircleStart) {
            tint(imgOther,grid.lineCol);
        }
        Color defaultCol = grid.windowCol;
        rVal = defaultCol.getRed();
        gVal = defaultCol.getGreen();
        bVal = defaultCol.getBlue();
    }
}
