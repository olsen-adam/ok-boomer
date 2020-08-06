package com.program.main.buttons;

import com.program.main.GUIdrawType;
import com.program.main.Grid;
import com.program.main.MouseIn;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class ButtonText extends ObjButton{
    /* ------------- INCLUDES: ------------
    int x, y;
    BttID bttID;
    Grid grid;
    mouseIn = imouseIn;
    -------------------------------------- */
    int width, height, textOff;
    private String text;

    private int targetEditHeight;//MAP EDIT ONLY
    private float floatH; //MAP EDIT ONLY
    private Color mapEditColor; //MAP EDIT ONLY
    private BufferedImage imgSqShadowS, imgWrench; //MAP EDIT ONLY

    private boolean loadMenu; //LOAD MAP ONLY
    private int yO, h; //LOAD MAP ONLY
    private String mapName; //LOAD MAP ONLY
    private int mBWidth, mBHeight, mBConst, mBXConst, mBAdd, numMB; //LOAD MAP ONLY
    private int fileOpenW, fileNewW, mapNameW, mOn = -1; //LOAD MAP ONLY
    private BufferedImage imgNewBack, imgNewFront, imgOpenBack, imgOpenFront,
            imgSaveBack, imgSaveFront; //LOAD MAP ONLY

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
            case TextMapEdit:
                width = 150;
                targetEditHeight = 53;
                height = targetEditHeight;
                floatH = height;
                mapEditColor = new Color(44,45,48);
                try {
                    imgSqShadowS = ImageIO.read(getClass().getResourceAsStream("/sqShadowSmall.png"));
                    imgWrench = ImageIO.read(getClass().getResourceAsStream("/wrench.png"));
                    tint(imgWrench,grid.bgCol);
                } catch(IOException e) {
                    e.printStackTrace();
                }

                textOff = 20;
                text = "Map Editor";
                break;
            case TextLoadMap:
                width = 330;
                height = 30;
                textOff = 10;
                text = "Load Map";

                loadMenu = false;
                mapName = grid.getPresetName(grid.getPresetOn());
                yO = 31;
                mBWidth = 190;
                mBHeight = 40;
                mBConst = 40;
                mBXConst = 5;
                mBAdd = mBHeight+5;
                numMB = grid.getNumPresets()+2;
                h = 240;
                grid.getGUI().setLoadMenuVars(yO,h,mBWidth, mBHeight, mBConst,mBXConst, mBAdd, numMB);
                fileNewW = 107;
                fileOpenW = 121;
                mapNameW = -1;
                try {
                    imgNewBack = ImageIO.read(getClass().getResourceAsStream("/newback.png"));
                    imgNewFront = ImageIO.read(getClass().getResourceAsStream("/newfront.png"));
                    imgOpenBack = ImageIO.read(getClass().getResourceAsStream("/openback.png"));
                    imgOpenFront = ImageIO.read(getClass().getResourceAsStream("/openfront.png"));
                    imgSaveBack = ImageIO.read(getClass().getResourceAsStream("/saveback.png"));
                    imgSaveFront = ImageIO.read(getClass().getResourceAsStream("/savefront.png"));
                    //tint(imgWrench,grid.bgCol);
                } catch(IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void tick() {
        boolean mClicked = mouseIn.getClicked();
        boolean isClicked;
        int mx = mouseIn.getMouseX();
        int my = mouseIn.getMouseY();
        boolean isOn = (mx > x && mx < x+width &&
                my > y && my < y+height);
        isClicked = mClicked && isOn;

        /// TICK FROM STATE

        switch (bttID) {
            case TextMapEdit:
                if (isClicked) {
                    if (grid.getifEditingMap()) {
                        //text = "Map Editor";
                        targetEditHeight = 53;
                        mapEditColor = grid.windowCol;
                    }
                    else {
                        //text = "Exit";
                        targetEditHeight = 69;
                        mapEditColor = grid.hoverCol;
                    }
                    grid.flipEditingMap();
                }
                if (!(floatH == targetEditHeight)) {
                    if (Math.abs(floatH-targetEditHeight) < 1) {
                        floatH = targetEditHeight;
                    } else {
                        floatH += ((float)targetEditHeight-floatH)*0.25;
                    }
                } height = (int) floatH;
                break;
            case TextLoadMap:
                /*if (grid.getifEditingMap()) {
                    text = "Save";
                } else text = "Load Map";*/
                if (isOn) {
                    if (mx < x+fileOpenW) {
                        mOn = 0; //OPEN
                        if (mClicked) {
                            loadMenu = !loadMenu;
                        }
                    } else {
                        if (mouseIn.getPressed()) {
                            loadMenu = false; // If not clicking loadbox
                        }
                        if (mx > x+fileOpenW+fileNewW) {
                            mOn = 2; //SAVE
                            if (mClicked) grid.saveFile();
                        }
                        else {
                            mOn = 1; //NEW
                            if (mClicked) {
                                //THE BIG NEW
                                for (int ii = 0; ii < 1; ii++) {
                                    if (grid.getifRunning()) grid.flipRunning();

                                    String in = JOptionPane.showInputDialog(null, "E.g. \"5, 5\"", "Enter Width, Height", JOptionPane.QUESTION_MESSAGE);

                                    String sWidth = "";
                                    String sHeight = "";
                                    boolean passedFirstInt = false;
                                    boolean valid = true;
                                    if (in == null) break;
                                    for (int i = 0; i < in.length(); i++) {
                                        char c = in.charAt(i);
                                        if (Character.isDigit(c)) {
                                            if (passedFirstInt) {
                                                sHeight += c;
                                            } else sWidth += c;
                                        } else {
                                            if (c == ',') {
                                                if (in.charAt(i + 1) == ' ') i++;
                                                passedFirstInt = true;
                                            } else {
                                                valid = false;
                                                break;
                                            }
                                        }
                                    }
                                    //Check characters
                                    if (!(valid)) {
                                        invalidMap("Invalid character(s)");
                                        break;
                                    }
                                    int iWidth;
                                    int iHeight;
                                    try {
                                        iWidth = Integer.parseInt(sWidth);
                                        iHeight = Integer.parseInt(sHeight);
                                    } catch (NumberFormatException nfe) {
                                        //Check number format
                                        invalidMap("Invalid Number Format");
                                        break;
                                    }
                                    //Check bounds
                                    int upper = 100;
                                    int lower = 2;
                                    if (iWidth < lower || iWidth > upper) {
                                        invalidMap("Width is out of Bounds");
                                        break;
                                    }
                                    if (iHeight < lower || iHeight > upper) {
                                        invalidMap("Height is out of Bounds");
                                        break;
                                    }
                                    //DO IT
                                    grid.newGridSize(iWidth, iHeight);
                                }
                            }
                        }
                    }
                    //if (text == "Save") grid.saveFile();
                } else {
                    mOn = -1;
                    if (mouseIn.getPressed()) {
                    if (!(mx > x && mx < x + width &&
                            my > y && my < y + height)
                            && !(mx > x && mx < x + mBWidth && //
                            my > y + yO & my < y + yO + h)) // Not in dropbox
                        loadMenu = false;
                    }
                }
                if (mClicked && loadMenu) {
                    for (int i = 0; i < numMB; i++) {
                        int yyy = y + mBConst + mBAdd * i;
                        if (mx > x + mBXConst && mx < x + mBXConst + mBWidth &&
                                my > yyy && my < yyy + mBHeight) {
                            if (i >= numMB-2)
                            {
                                if (i == numMB-2) { //OPEN FROM MAP
                                    try {
                                        grid.openFile(false, "Choose MovingAI File", "MAP files", "map");
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                } else { //OPEN FROM TXT
                                    try {
                                        grid.openFile(true, "Choose a Map", "TXT files", "txt");
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                grid.loadPreset(i);
                                setMapName(grid.getPresetName(i));
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
        /*g.setColor(grid.getLineCol());
        Font fn = grid.fontBase;
        g.setFont(fn);
        g.drawRect(x,y,width,height);

        Graphics2D g2 = (Graphics2D)g;
        FontRenderContext frc = g2.getFontRenderContext();
        int w = (int)fn.getStringBounds(text, frc).getWidth();
        int lO = 0;
        if (bttID == BttID.TextLoadMap) lO = 100;

        g.drawString(text,x + (width-w)/2 - lO,y+height - textOff);*/

        switch (bttID) {
            case TextMapEdit:
                int arc = 10;
                //g.setColor(grid.lightLineCol);
                g.drawImage(imgSqShadowS, x-7,y-arc, (int)(width*1.09),(int)((height+arc)*1.15), null);
                g.setColor(mapEditColor);
                g.fillRoundRect(x,y-arc,width,height+arc, arc, arc);
                //Wrench
                    g.drawImage(imgWrench, x+10,y+height-imgWrench.getHeight()-12, imgWrench.getWidth(), imgWrench.getHeight(), null);
                //Text
                    g.setFont(grid.fontBold);
                    g.setColor(grid.bgCol);
                    g.drawString(text,x+50,y+height-19);
                    if (grid.getifEditingMap()) {
                        g.setFont(grid.fontBase);
                        g.drawString("Exit",x+50,y+height-40-(targetEditHeight-(int)floatH)*3);
                    }

                /*Graphics2D graphics2D = (Graphics2D)g;
                graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics2D.drawImage(imgWrench, x, +10, (int)(imgWrench.getWidth()/1.5), (int)(imgWrench.getHeight()/1.5), null);*/

                break;
            case TextLoadMap:
                    //Map name box
                    /*int bX = 150;
                    int bH = 20;
                    g.drawRect(x + width - bX, y + (height-bH)/2, 130, bH);
                    g.drawString(mapName, x + width - bX + 20, y+height - textOff);*/
                //File
                g.setColor(grid.lightLineCol);
                g.setFont(grid.fontBase);
                g.drawString("File", x+2, y-5);
                g.drawString("_", x+2, y-5);
                if (mapNameW == -1) { //INIT map name width
                    Graphics2D g2 = (Graphics2D)g;
                    FontRenderContext frc = g2.getFontRenderContext();
                    mapNameW = (int)grid.fontBase.getStringBounds(mapName, frc).getWidth();
                } else g.drawString(mapName, x+width-mapNameW-2, y-5);
                //BG
                g.setColor(grid.bgCol);
                g.fillRect(x,y,width,height);
                    //Hover Col
                    if (!(mOn == -1)) {
                        int[] bWidths = new int[3];
                        bWidths[0] = fileOpenW;
                        bWidths[1] = fileNewW;
                        bWidths[2] = width-fileOpenW-fileNewW;
                        int bgXO = 0;
                        for (int i = 0; i < mOn; i++) {
                            bgXO += bWidths[i];
                        }
                        Color cc = grid.hoverCol;
                        int rr = cc.getRed()-10;int gg = cc.getGreen()-10;int bb = cc.getBlue()-10;
                        g.setColor(new Color(rr,gg,bb));
                        g.fillRect(x+bgXO,y,bWidths[mOn],height);
                    }
                //ICONS
                g.setColor(grid.lineCol);
                int icXO = 5, txtYO = 19, txtXO = 8;
                g.drawImage(imgOpenBack, x+icXO, y+(height-imgOpenBack.getHeight())/2, imgOpenBack.getWidth(), imgOpenBack.getHeight(), null);
                g.drawImage(imgOpenFront, x+icXO, y+(height-imgOpenBack.getHeight())/2, imgOpenBack.getWidth(), imgOpenBack.getHeight(), null);
                g.drawString("Load", x+icXO+txtXO+imgOpenBack.getWidth(), y+txtYO); g.drawString("_", x+icXO+txtXO+imgOpenBack.getWidth(), y+txtYO);

                g.drawImage(imgNewBack, x+icXO+fileOpenW, y+(height-imgNewBack.getHeight())/2, imgNewBack.getWidth(), imgNewBack.getHeight(), null);
                g.drawImage(imgNewFront, x+icXO+fileOpenW, y+(height-imgNewBack.getHeight())/2, imgNewFront.getWidth(), imgNewFront.getHeight(), null);
                g.drawString("New", x+icXO+txtXO+fileOpenW+imgNewBack.getWidth(), y+txtYO); g.drawString("_", x+icXO+txtXO+fileOpenW+imgNewBack.getWidth(), y+txtYO);

                g.drawImage(imgSaveBack, x+icXO+fileOpenW+fileNewW, y+(height-imgSaveBack.getHeight())/2, imgSaveBack.getWidth(), imgSaveBack.getHeight(), null);
                g.drawImage(imgSaveFront, x+icXO+fileOpenW+fileNewW, y+(height-imgSaveBack.getHeight())/2, imgSaveFront.getWidth(), imgSaveFront.getHeight(), null);
                g.drawString("Save", x+icXO+txtXO+fileOpenW+fileNewW+imgSaveBack.getWidth(), y+txtYO);
                    g.drawString("_", x+icXO+txtXO+fileOpenW+fileNewW+imgSaveBack.getWidth(), y+txtYO);

                if (loadMenu) grid.getGUI().setDrawType(GUIdrawType.MapSelect);
                break;
        }
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
        mapNameW = -1;
    }

    private void invalidMap(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    /*public int getWidth() {
        return width;
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

    @Override
    public void setTint() {
        if (bttID == BttID.TextMapEdit) {
            tint(imgWrench, grid.bgCol);
        }
    }
}
