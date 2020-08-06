package com.program.main;

import com.program.main.buttons.ButtonBar;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GridFunctions {
    Grid grid;
    private BufferedImage imgSqShadowL, imgSqShadowS;

    public GridFunctions(Grid grid) {
        this.grid = grid;

        try {
            imgSqShadowL = ImageIO.read(getClass().getResourceAsStream("/sqShadowBig.png"));
            imgSqShadowS = ImageIO.read(getClass().getResourceAsStream("/sqShadowSmall.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void animateMan(float manDis, float manWMult) {
        float mult = 0.85f;
        if (grid.moveTime < 25) {
            mult = grid.approach(85, 50, grid.moveTime, 25, 1);
            mult /= 100;
        }
        float subt = 0.6f;

        if (manDis > 0) {
            manDis *= mult;
            manDis -= subt;
            if (manDis < 0) manDis = 0;
            grid.setmanDis(manDis);
        }

        if (!grid.getifRunning()) mult = 0.8f;

        if (Math.abs(manWMult-1) > 0) {
            float add = 1-manWMult;
            add *= 1-mult;
            add += 0.005*Math.signum(add);
            if (Math.abs(add) <= 0.01) {
                grid.setManWMult(1);
            } else {
                grid.setManWMult(manWMult+add);
            }
        }
    }

    public void drawWindows(Graphics g) {
        //TOP BAR
        g.setColor(grid.windowCol);
        int barH = 62;
        g.drawImage(imgSqShadowS,-54, barH-92,
                (int) (grid.program.WIDTH*1.15), 101, null);
        g.fillRect(0,0, Program.WIDTH, barH);
        //g.setColor(grid.lightLineCol);
        //g.fillRect(0,barH,grid.program.WIDTH,2);


        g.setColor(grid.clickCol);
        int gOff = 18;
        g.fillRoundRect(grid.prefGridX+gOff/2,grid.prefGridY+gOff/2,
                grid.gridSize-gOff,grid.gridSize-gOff,15,15);
        int gW = grid.getGridActualW(), gH = grid.getGridActualH();
        int low = getLower(gW, gH);
        int high = getHigher(gW, gH);
        int gX = 0, gY = 0;
        float ratio = ((float) high/(float) low);
        if (low == gW) gX = (int) ratio;
        else gY = (int) ratio;
        g.drawImage(imgSqShadowL,grid.getGridStartX()-27+gX/2, grid.getGridStartY()-21+gY/2,
                (int) (gW*1.095), (int) (gH*1.09), null);

        g.setColor(grid.windowCol);
        int tableX = grid.sideBarX;
        int tableY = grid.prefGridY+2;
        int tableW = 330, tH = 405;
        int arcS = 10;
            //Shadow
            int sW = (int) (imgSqShadowL.getWidth()/1.06);
            int sH = (int) (imgSqShadowL.getHeight()/1.03);
            g.drawImage(imgSqShadowL,tableX-15, tableY-15, sW, sH, null);
        g.fillRoundRect(tableX,tableY,tableW,tH,arcS,arcS);

        tableY += tH+20;
            //Small SHADOWWW
                sW = (int) (imgSqShadowS.getWidth()/1.04);
                sH = (int) (imgSqShadowS.getHeight()/1.1);
                g.drawImage(imgSqShadowS,tableX-14, tableY-12, sW, sH, null);

        g.setColor(grid.windowCol);
        g.fillRoundRect(tableX,tableY,tableW,123,arcS,arcS);
        g.setColor(grid.lineCol);
        g.setFont(grid.fontBase);
        tableX += 10;
        tableY += 43;
        int add = 44;
        String sv = "Squares Visited:", srv = "Squares Revisited:";
        g.drawString(sv, tableX+20, tableY);
        g.drawString(srv, tableX+20, tableY+add);
        //NUMBERS
        g.setFont(grid.fontBold);
        Graphics2D g2 = (Graphics2D)g;
        FontRenderContext frc = g2.getFontRenderContext();
        int w = (int)grid.fontBold.getStringBounds(sv, frc).getWidth();
        int xO = 4;
        g.drawString(String.valueOf(grid.getVisitedData(false)), tableX+xO+w+2, tableY);
        w = (int)grid.fontBold.getStringBounds(srv, frc).getWidth();
        g.drawString(String.valueOf(grid.getVisitedData(true)), tableX+xO+w, tableY+add);
    }

    private int getLower(int a, int b){
        if (a <= b) return a;
        return b;
    }
    private int getHigher(int a, int b){
        if (a >= b) return a;
        return b;
    }
}
