package com.program.main.buttons;

import com.program.main.Grid;
import com.program.main.MouseIn;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ButtonToggle extends ObjButton {
    /* ------------- INCLUDES: ------------
    int x, y;
    BttID bttID;
    Grid grid;
    mouseIn = imouseIn;
    -------------------------------------- */
    int width, height;
    public boolean toggled;
    String text;
    float percent;
    BufferedImage imgShadow;
    ButtonToggle independent, bundled;
    //Font font = new Font("Serif", Font.BOLD, 20);

    public ButtonToggle(int xx, int yy, BttID ibttID, Grid igrid, MouseIn imouseIn) {
        super(xx, yy, ibttID, igrid, imouseIn);
        /*@Overwritten:
        public ObjButton(int xx, int yy, BttID ibttID) {
            x = xx;
            y = yy;
            bttID = ibttID;
            grid = igrid
            mouseIn = imouseIn;*/
        width = 56;
        height = 28;
        toggled = false;
        percent = 0;
        try {
            imgShadow = ImageIO.read(getClass().getResourceAsStream("/circleShadow.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }

        switch (bttID) {
            case ToggleRedund:
                text = "Eliminate Redundancies";
                break;
            case ToggleMDist:
                text = "Manhattan Distance";
                break;
            case ToggleAStar:
                text = "A* Algorithm";
                break;
            case ToggleColor:
                text = "Colored Floor";
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
        }
        switch (bttID) {
            case ToggleRedund:
                if (isClicked) {
                    toggled = !toggled;
                    grid.togRedund = toggled;
                    if (bundled.toggled && !toggled) { //turning off
                        bundled.toggled = false;
                        grid.togAStar = false;
                    }
                    if (grid.getifRunning()) grid.flipRunning();
                }
                break;
            case ToggleMDist:
                if (isClicked) {
                    toggled = !toggled;
                    grid.togMDist = toggled;
                    if (independent.toggled) {
                        independent.toggled = !toggled;
                        grid.togAStar = toggled;
                    }
                    if (grid.getifRunning()) grid.flipRunning();
                }
                break;
            case ToggleAStar:
                if (isClicked) {
                    toggled = !toggled;
                    grid.togAStar = toggled;
                    if (independent.toggled) {
                        independent.toggled = !toggled;
                        grid.togMDist = toggled;
                    }
                    if (!bundled.toggled) {
                        bundled.toggled = toggled;
                        grid.togRedund = toggled;
                    }
                    if (grid.getifRunning()) grid.flipRunning();
                }
                break;
            case ToggleColor:
                if (isClicked) {
                    toggled = !toggled;
                    grid.setDrawFloor(toggled);
                }
                break;
        }
        //Animate
        float mult = 0.3f;
        float add = 0.01f;
        if (toggled) {
            if (!(percent == 1)) {
                percent += (1f-percent)*mult;
                percent += add;
                if (percent >= 0.99f) percent = 1;
            }
        } else {
            if (!(percent == 0)) {
                percent -= (percent)*mult;
                percent -= add;
                if (percent <= 0.01f) percent = 0;
            }
        }
    }

    @Override
    public void render(Graphics g) {

        /*g.setColor(grid.getLineCol());
        g.drawRect(x,y,width,height);
        /*int tOff = 0;
        int boxS = (int) (height*0.75);
        int inBorder = (height-boxS)/2;
        if (toggled) tOff = width-boxS-inBorder*2;
        g.drawRect(x+tOff+inBorder,y+inBorder,boxS,boxS);*/

        //base
        int trackH = (int) (height*0.75);
        int off = (height-trackH)/2;
        g.setColor(grid.clickCol);
        g.fillOval(x+width-trackH-off,y+off,trackH,trackH);
        g.fillRect(x+off+(trackH/2),y+off,
                (x+width-trackH-off)-(x+off),trackH);
            //Start Circle
            g.setColor(grid.sliderCol);
            g.fillOval(x+off,y+off,trackH,trackH);
            if (toggled) g.fillRect(x+off+(trackH/2),y+off,
                10+(x + (int) ((width-height)*percent))-(x+off+(trackH/2)),
                trackH);
        //THE circle
        if (toggled) g.setColor(grid.sliderClickCol);
        else {
            Color bulb = grid.windowCol;
            if (grid.getifEditingMap()) bulb = grid.lightLineCol;
            g.setColor(bulb);
        }
        int mW = imgShadow.getWidth();
        int mH = imgShadow.getHeight();
        int cH = (int) (height*0.95);
        g.drawImage(imgShadow,
                x + (int) ((width-height)*percent)-(mW-cH)/2,
                y+(height-cH)/2-(mW-cH)/2+2, mW-1, mH-3, null);
        g.fillOval(x + (int) ((width-height)*percent),y+(height-cH)/2,height,cH);

        //TEXT
        g.setColor(grid.getLineCol());
        g.setFont(grid.fontBase);
        int tx = 15;
        int ty = 20;
        g.drawString(text, x+width+tx, y + ty);
    }

    public void setIndependent(ButtonToggle independent) {
        this.independent = independent;
    }

    public void setBundled(ButtonToggle bundled) {
        this.bundled = bundled;
    }

    @Override
    public void setTint() {
        //tint(imgMark,grid.hoverCol);
    }
}