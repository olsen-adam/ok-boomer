package com.program.main;

import com.program.main.buttons.ButtonText;

import java.awt.*;

public class GUI {
    private ButtonText buttonLoad;
    private Grid grid;

    private int bLyO, bLh;
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
                int ww = buttonLoad.getWidth();
                g.setColor(grid.getBgCol());
                g.fillRect(xx,yy+bLyO,ww,bLh);
                g.setColor(c);
                g.drawRect(xx,yy+bLyO,ww,bLh);
                break;
        }
        guiDrawType = GUIdrawType.None;
    }

    public void setDrawType(GUIdrawType guiDrawType) {
        this.guiDrawType = guiDrawType;
        //System.out.println("BRUH");
    }

    public void setYoH(int yO, int h) {
        this.bLyO = yO;
        this.bLh = h;
    }

    public void setButtonLoad(ButtonText buttonLoad) {
        this.buttonLoad = buttonLoad;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }
}
