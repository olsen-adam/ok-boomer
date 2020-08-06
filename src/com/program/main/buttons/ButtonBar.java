package com.program.main.buttons;

import com.program.main.Grid;
import com.program.main.MouseIn;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ButtonBar extends ObjButton {
    /* ------------- INCLUDES: ------------
    int x, y;
    BttID bttID;
    Grid grid;
    mouseIn = imouseIn;
    -------------------------------------- */
    public int width, height, barWidth, baseBarW;
    String text;
    Font font = new Font("Serif", Font.BOLD, 20);
    boolean held = false;
    private BufferedImage imgMark, imgShadow;
    public int tableX = x-3, tableY = y+81, arcS = 10;

    public ButtonBar(int xx, int yy, BttID ibttID, Grid igrid, MouseIn imouseIn) {
        super(xx, yy, ibttID, igrid, imouseIn);
        /*@Overwritten:
        public ObjButton(int xx, int yy, BttID ibttID) {
            x = xx;
            y = yy;
            bttID = ibttID;
            grid = igrid
            mouseIn = imouseIn;*/
        x -= 3;
        width = 280;
        barWidth = (int) (width*0.3);
        baseBarW = barWidth;
        height = 30;
        text = "Speed";
        try {
            imgMark = ImageIO.read(getClass().getResourceAsStream("/mark.png"));
            tint(imgMark,grid.hoverCol);
            imgShadow = ImageIO.read(getClass().getResourceAsStream("/circleShadow.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tick() {
        int mx = mouseIn.getMouseX();
        int my = mouseIn.getMouseY();
        if (held) {
            barWidth = mx-x;
            if (Math.abs(baseBarW-barWidth) <= 17) barWidth = baseBarW;
            else barWidth = clamp(barWidth,(int)(width*0.1), width);
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
        g.setFont(grid.fontBase);
        g.drawString(text,x+5,y-40);

        int trackH = (int) (height*0.47);
        int off = (height-trackH)/2;
        g.setColor(grid.clickCol);
        g.fillOval(x+width-trackH-off,y+off,trackH,trackH);
        g.fillRect(x+off+(trackH/2),y+off,
                (x+width-trackH-off)-(x+off),trackH);
        //Start Circle
        g.setColor(grid.sliderCol);
        g.fillOval(x+off,y+off,trackH,trackH);
        g.fillRect(x+off+(trackH/2),y+off,
                (x+10+(int)((width-height)*((float)barWidth/(float)width)))-(x+off+(trackH/2)),
                trackH);
        //Mark
        int mW = imgMark.getWidth();
        int mH = (int) (imgMark.getHeight()/1.1);
        g.drawImage(imgMark,x+baseBarW+mW, y+height/2-mH/2, mW, mH, null);

        //THE circle
        g.setColor(grid.sliderClickCol);
        int cH = (int) (height*0.95);
            mW = (int) (imgShadow.getWidth()*1);
            mH = (int) (imgShadow.getHeight()*1);
            g.drawImage(imgShadow,
                    x + (int) ((width-height)*((float) barWidth/(float) width))-(mW-cH)/2+0,
                    y+(height-cH)/2-(mW-cH)/2+1, mW, mH, null);
        g.fillOval(x + (int) ((width-height)*((float) barWidth/(float) width)),y+(height-cH)/2,height,cH);

        //TIMES VISITED TABLE
        //if (bttID == BttID.ToggleColor)
        /*
        {
            g.setColor(grid.windowCol);
            g.fillRoundRect(tableX,tableY,width,123,arcS,arcS);
            g.setColor(grid.lineCol);
            String sv = "Squares Visited:", srv = "Squares Revisited:";
            g.drawString(sv, tableX+20, tableY+35);
            g.drawString(srv, tableX+20, tableY+85);
            //NUMBERS
            g.setFont(grid.fontBold);
            Graphics2D g2 = (Graphics2D)g;
            FontRenderContext frc = g2.getFontRenderContext();
            int w = (int)grid.fontBold.getStringBounds(sv, frc).getWidth();
            int xO = 4;
            g.drawString(String.valueOf(grid.getVisitedData(false)), tableX+xO+w+2, tableY+35);
            w = (int)grid.fontBold.getStringBounds(srv, frc).getWidth();
            g.drawString(String.valueOf(grid.getVisitedData(true)), tableX+xO+w, tableY+85);
        }*/
    }

    public void resetBar() {
        barWidth = baseBarW;
    }

    public int clamp(int var, int min, int max) {
        if (var < min) var = min;
        if (var > max) var = max;
        return var;
    }

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

    @Override
    public void setTint() {
        tint(imgMark,grid.hoverCol);
    }
}