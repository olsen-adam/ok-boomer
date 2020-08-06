package com.program.main;

import com.program.main.buttons.BttID;
import com.program.main.buttons.ButtonText;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GUI {
    private ButtonText buttonLoad;
    private Grid grid;
    private final MouseIn mouseIn;
    private BufferedImage imgSqShadowS;

    private int bLyO, bLh, infoY;
    private final int rwMax = 204, rhMax = 230;
    float infoW, infoH;
    private BttID infoBttID;
    private int mBWidth, mBHeight, mBConst, mBXConst, mBAdd, numMB;
    boolean animating = false;

    private GUIdrawType guiDrawType;

    public GUI(MouseIn mouseIn) {
        infoW = 0;
        infoH = 0;

        this.mouseIn = mouseIn;
        guiDrawType = GUIdrawType.None;
        try {
            imgSqShadowS = ImageIO.read(getClass().getResourceAsStream("/sqShadowSmall.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    //private int infoPriority = -1;
    private String infoText = "";

    public void tick() {
        if (animating) {
            float mult = 0.27f;
            float add = 0.5f;
            animInfo(mult, add);
            animating = false;
        }
    }

    public void render(Graphics g) {
        if (guiDrawType == GUIdrawType.MapSelect) { //Make a switch statement if new draw types added
            int xx = buttonLoad.getX();
            int yy = buttonLoad.getY();
            int ww = mBWidth + mBXConst * 2;
            g.setColor(grid.bgCol);
            g.fillRect(xx, yy + bLyO, ww, bLh);
            g.setColor(grid.lightLineCol);
            g.drawRect(xx, yy + bLyO, ww, bLh);

            //Map presets
            for (int i = 0; i < numMB; i++) {
                Color cc;
                int mx = mouseIn.getMouseX();
                int my = mouseIn.getMouseY();
                if (mx > xx + mBXConst && mx < xx + mBXConst + mBWidth &&
                        my > yy + mBConst + mBAdd * i && my < yy + mBConst + mBAdd * i + mBHeight)
                    cc = grid.clickCol;
                else {
                    if (grid.getPresetOn() == i) cc = grid.hoverCol;
                    else cc = grid.windowCol;
                }
                g.setColor(cc);
                g.fillRect(xx + mBXConst, yy + mBConst + mBAdd * i, mBWidth, mBHeight);
                g.setColor(grid.getLineCol());
                //g.drawRect(xx + mBXConst, yy + mBConst + mBAdd * i, mBWidth, mBHeight);
                int sx = xx + mBXConst + 20;
                int sy = yy + mBConst + mBAdd * i + 24;
                if (!(i >= numMB - 2)) {
                    g.drawString(grid.getPresetName(i), sx, sy);
                } else {
                    String tt = "Open From .map File";
                    if (i == numMB-1) tt = "Open From .txt File";
                    g.drawString(tt, sx + 0, sy);
                }
            }
        }
        guiDrawType = GUIdrawType.None;

        //INFO BOX
        if (!(infoText == "")) {
            animating = true;
            int
            mx = mouseIn.getMouseX(),
            //my = mouseIn.getMouseY(),
            rw = (int)infoW,
            rh = (int)infoH,
            rx = mx-rw-10,
            ry = infoY;
                //Shadow
                int sW = (int)(rw*1.12);//(int) (imgSqShadowS.getWidth()/1.04);
                int sH = (int)(rh*1.2);//(int) (imgSqShadowS.getHeight()/1.1);
                g.drawImage(imgSqShadowS,rx-10, ry-19, sW, sH, null);
            g.setColor(grid.windowCol);
            g.fillRoundRect(rx, ry, rw, rh, 25, 25);
            g.setColor(grid.lightLineCol);
            int lineHeight = g.getFontMetrics().getHeight();
            int txtY = ry + 10;
            for (String line : infoText.split("\n"))
                g.drawString(line, rx+15, txtY += lineHeight);
            infoText = "";
            //infoPriority = -1;
            if (infoBttID == BttID.InfoColor) {
                Color[] cols = new Color[4];
                cols[0] = new Color(50,70,255);
                cols[1] = new Color(50,255,255);
                cols[2] = new Color(255,152,50);
                cols[3] = new Color(255,30,30);
                String[] info = new String[4];
                info[0] = "= 1 Pass";
                info[1] = "= 10 Passes";
                info[2] = "= 30 Passes";
                info[3] = "= >100 Passes";
                for (int i = 0; i < 4; i++) {
                    int consY = 87, consX = 22, add = 31;
                    g.setColor(cols[i]);
                    g.fillRoundRect(rx+consX, ry+consY+add*i, 18,18, 2,2);
                    g.setColor(grid.lightLineCol);
                    g.drawString(info[i], rx+consX+25, ry+consY+14+add*i);
                }
            }
        } else {
            infoW = 0;
            infoH = 0;
        }
    }

    public void setDrawType(GUIdrawType guiDrawType) {
        this.guiDrawType = guiDrawType;
        //System.out.println("BRUH");
    }

    public void setButtonLoad(ButtonText buttonLoad) {
        this.buttonLoad = buttonLoad;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public void setLoadMenuVars(int yO, int h, int w, int mh, int yc, int xc, int a, int p) {
        this.bLyO = yO;
        this.bLh = h;

        this.mBWidth = w;
        this.mBHeight = mh;
        this.mBConst = yc;
        this.mBXConst = xc;
        this.mBAdd = a;
        this.numMB = p;
    }

    public void requestDrawInfo(String text, int infoY, BttID bttID) {
        infoText = text;
        this.infoY = infoY;
        infoBttID = bttID;
    }

    private void animInfo(float mult, float add) {
        int rw, rh;
        rw = rwMax; rh = rhMax;

        if (Math.abs(rw-infoW) >= 1) {
            infoW += ((float) rw - infoW) * mult;
            infoW += add;
        } else infoW = rw;

        if (Math.abs(rh-infoH) >= 1) {
            infoH += ((float) rh - infoH) * mult;
            infoH += add;
        } else infoH = rh;
    }
}
