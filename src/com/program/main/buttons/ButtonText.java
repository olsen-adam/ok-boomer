package com.program.main.buttons;

import com.program.main.GUI;
import com.program.main.GUIdrawType;
import com.program.main.Grid;
import com.program.main.MouseIn;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.io.FileNotFoundException;

public class ButtonText extends ObjButton{
    /* ------------- INCLUDES: ------------
    int x, y;
    BttID bttID;
    Grid grid;
    mouseIn = imouseIn;
    -------------------------------------- */
    int width, height, textOff;
    private String text;
    private String mapName;
    Font font = new Font("Serif", Font.BOLD, 20);
    private int Ax, Ay, iAw; //START ONLY
    private int yO, h; //LOAD MAP ONLY
    private int mBWidth, mBHeight, mBConst, mBXConst, mBAdd, numMB; //LOAD MAP ONLY
    private boolean loadMenu;
    private GUIdrawType guiDrawType;

    public ButtonText(int xx, int yy, BttID ibttID, Grid igrid, MouseIn imouseIn) {
        super(xx, yy, ibttID, igrid, imouseIn);
        /*@Overwritten:
        public ObjButton(int xx, int yy, BttID ibttID) {
            x = xx;
            y = yy;
            bttID = ibttID;
            grid = igrid
            mouseIn = imouseIn;*/

        switch (bttID) {
            case TextStart:
                width = 120;
                height = 40;
                text = "Start";
                textOff = 15;
                Ax = 10;
                Ay = 25;
                iAw = 0;
                break;
            case TextReset:
                width = 120;
                height = 30;
                textOff = 10;
                text = "Reset";
                break;
            case TextMapEdit:
                width = 150;
                height = 55;
                textOff = 20;
                text = "Map Editor";
                break;
            case TextLoadMap:
                width = 340;
                height = 30;
                textOff = 10;
                text = "Load Map";

                loadMenu = false;
                mapName = grid.getPresetName(grid.getPresetOn());
                yO = 34;
                h = 260;
                mBWidth = 210;
                mBHeight = 40;
                mBConst = 42;
                mBXConst = 10;
                mBAdd = mBHeight+10;
                numMB = grid.getNumPresets()+1;
                grid.getGUI().setLoadMenuVars(yO,h,mBWidth, mBHeight, mBConst,mBXConst, mBAdd, numMB);
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

            if (!grid.getifRunning()) {
                arrowDetect(mx,my);
            }
        }

        /// TICK FROM STATE

        switch (bttID) {
            case TextStart:
                if (isClicked) {
                    if (!(grid.getMoveID() == grid.getNumMoves()))
                    grid.flipRunning();
                }
                if (grid.getifRunning()) text = "Stop";
                else text = "Start";
                break;
            case TextReset:
                if (isClicked) {
                    grid.resetBoard();
                }
                break;
            case TextMapEdit:
                if (isClicked) {
                    grid.flipEditingMap();
                }
                if (grid.getifEditingMap()) text = "Exit";
                else text = "Map Editor";
                break;
            case TextLoadMap:
                if (grid.getifEditingMap()) {
                    text = "Save";
                } else text = "Load Map";

                int mx = mouseIn.getMouseX();
                int my = mouseIn.getMouseY();
                if (isClicked) {
                    loadMenu = !loadMenu;
                    if (text == "Save") grid.saveFile();
                } else if (mouseIn.getPressed()) {
                    if (!(mx > x && mx < x+width &&
                            my > y && my < y+height)
                            && !(mx > x && mx < x+width && //
                                    my > y + yO & my < y + yO + h)) // Not in dropbox
                    loadMenu = false;
                }
                if (mClicked && loadMenu) {
                    for (int i = 0; i < numMB; i++) {
                        int yyy = y + mBConst + mBAdd * i;
                        if (mx > x + mBXConst && mx < x + mBXConst + mBWidth &&
                                my > yyy && my < yyy + mBHeight) {
                            if (i == numMB-1)
                            {
                                try {
                                    grid.openFile();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                grid.loadPreset(i);
                                mapName = grid.getPresetName(i);
                            }
                            loadMenu = false;
                        }
                    }
                }

                /*if (grid.getifEditingMap()) {
                    text = "Save";
                } else text = "Load Map";
                if (isClicked) {
                    if (text == "Load Map") {
                        try {
                            grid.openFile();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {grid.saveFile();}
                }*/
                break;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(grid.getLineCol());
        g.setFont(font);
        g.drawRect(x,y,width,height);

        Graphics2D g2 = (Graphics2D)g;
        FontRenderContext frc = g2.getFontRenderContext();
        int w = (int)font.getStringBounds(text, frc).getWidth();
        int lO = 0;
        if (bttID == BttID.TextLoadMap) lO = 100;

        g.drawString(text,x + (width-w)/2 - lO,y+height - textOff);

        switch (bttID) {
            case TextStart:
                int Aw = (int) font.getStringBounds("<", frc).getWidth();
                if (iAw == 0) iAw = Aw;
                g.drawString("<", x - Ax - Aw, y + Ay);
                g.drawString(">", x + width + Ax, y + Ay);
                break;
            case TextLoadMap:
                int bX = 150;
                int bH = 20;
                g.drawRect(x + width - bX, y + (height-bH)/2, 130, bH);
                g.drawString(mapName, x + width - bX + 20, y+height - textOff);
                //g.drawString(String.valueOf(loadMenu),x,y-10);
                if (loadMenu) grid.getGUI().setDrawType(GUIdrawType.MapSelect);
                break;
        }
    }

    public void arrowDetect(int mx, int my) {
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
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
}
