package com.program.main;

import com.program.main.buttons.ButtonText;

import java.awt.*;

public class GUI {
    private ButtonText buttonLoad;
    private Grid grid;

    private int bLyO, bLh;
    private int mBWidth, mBHeight, mBConst, mBXConst, mBAdd, numMB;
    private String text;

    private GUIdrawType guiDrawType;

    public GUI() {
        text = "";
        guiDrawType = GUIdrawType.None;
    }

    public void tick() {
    }

    public void render(Graphics g) {
        Color c = grid.getLineCol();
        switch (guiDrawType) {
            case Info:
                break;
            case MapSelect:
                int xx = buttonLoad.getX();
                int yy = buttonLoad.getY();
                int ww = mBWidth+mBXConst*2;
                g.setColor(grid.getBgCol());
                g.fillRect(xx,yy+bLyO,ww,bLh);
                g.setColor(c);
                g.drawRect(xx,yy+bLyO,ww,bLh);

                //Map presets
                for (int i = 0; i < numMB; i++) {
                    Color cc = grid.getBgCol();
                    if (grid.getPresetOn() == i) cc = new Color(80, 84, 90);
                    g.setColor(cc);
                    g.fillRect(xx+mBXConst,yy+mBConst+mBAdd*i,mBWidth,mBHeight);
                    g.setColor(grid.getLineCol());
                    g.drawRect(xx+mBXConst,yy+mBConst+mBAdd*i,mBWidth,mBHeight);
                    int sx = xx+mBXConst+20;
                    int sy = yy+mBConst+mBAdd*i+24;
                    if (!(i == numMB-1)) {
                        g.drawString(grid.getPresetName(i), sx, sy);
                    } else  {
                        g.drawString("Open From File", sx + 20, sy);
                    }
                }
                break;
        }
        guiDrawType = GUIdrawType.None;

        //INFO BOX
        g.setColor(grid.getLineCol());
        g.drawRoundRect(625,447,325,173,25,25);
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
}
