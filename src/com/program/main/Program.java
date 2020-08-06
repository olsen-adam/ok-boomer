package com.program.main;

import com.program.main.buttons.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

import static com.program.main.buttons.BttID.*;

public class Program extends Canvas implements Runnable{

    public static void main(String args[]) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        new Program();
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        /*Font[] fonts;
        fonts =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts( );
        for (int i = 0; i < fonts.length; i++) {
            System.out.print(fonts[i].getFontName( ) + " : ");
            System.out.print(fonts[i].getFamily( ) + " : ");
            System.out.print(fonts[i].getName( ));
            System.out.println( );
        }*/
    }

    public static final int WIDTH = 1000, HEIGHT = 700;
    private Color bgCol;

    private Window window;
    private Thread thread;
    private Grid grid;
    private MouseIn mouseIn;
    private BttManager bttManager;
    private GUI gui;
    private PtManager ptManager;

    private boolean running = false;

    public Program() {
        mouseIn = new MouseIn();
        grid = new Grid(mouseIn, this);
        gui = new GUI(mouseIn);
        ptManager = new PtManager();
        grid.setPtManager(ptManager);
        grid.setGUI(gui);
        gui.setGrid(grid);
        this.addMouseListener(mouseIn);
        this.addMouseMotionListener(mouseIn);
        createButtons();

        window = new Window(WIDTH, HEIGHT, "Pathfinding", this);
    }

    public synchronized void init() {
        thread = new Thread(this);
        thread.start();

        running = true;
    }

    public synchronized void end() {
        try{
            thread.join();
            running = false;
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountofTicks = 60.0;
        double ns = 1000000000/amountofTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now-lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            if (running) render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                //System.out.println("FPS: "+frames);
                frames = 0;
            }
        }
    }

    private void tick() {
        grid.tick();
        bttManager.tick();
        gui.tick();
        ptManager.tick();
        mouseIn.tick();//DO THIS LAST
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        //Draw Stuff
        g.setColor(bgCol);
        g.fillRect(0,0,WIDTH,HEIGHT);

        grid.render(g);
        bttManager.render(g);
        mouseIn.render(g);
        ptManager.render(g);
        gui.render(g); // DO THIS LAST

        g.dispose();
        g2.dispose();
        bs.show();
    }

    public void createButtons() {
        bttManager = new BttManager();
        grid.setBttManager(bttManager);
        //ButtonText bSt = new ButtonText(grid.getGridStartX()+30,grid.getGridStartY()-50,TextStart,grid,mouseIn);
        //bttManager.addButton(new ButtonCircle(grid.getGridStartX()+100,grid.getGridStartY()+50,TextStart,grid,mouseIn));
        //grid.setButtonStart(bSt);
            ButtonCircle bSt = new ButtonCircle(108,13,CircleStart,grid,mouseIn);
            grid.setButtonStart(bSt);
        bttManager.addButton(bSt);
        bttManager.addButton(new ButtonCircle(226,17,CircleReset,grid,mouseIn));
        bttManager.addButton(new ButtonText(439,0,TextMapEdit,grid,mouseIn));
        //bttManager.addButton(new ButtonText(grid.getGridStartX()+150,grid.getGridStartY()-40,TextNewMap,grid,mouseIn));
        int sbx = grid.sideBarX;
        ButtonText bl = new ButtonText(sbx,20,TextLoadMap,grid,mouseIn);
        grid.setButtonLoad(bl);
        gui.setButtonLoad(bl);
        bttManager.addButton(bl);
        int tx = 30;
        ButtonToggle btMD = null;
        ButtonToggle btRedund = null;
        for (int i  = 0; i < 4; i++) {
            int ty = 105+63*i;
            ButtonToggle bt;
            BttID bid = CircleStart, Iid = CircleStart;
            switch (i){
                case 0: bid = ToggleRedund;
                    bt = new ButtonToggle(sbx+tx, ty, bid,grid,mouseIn);
                    btRedund = bt;
                    Iid = InfoRedund; break;
                case 1: bid = ToggleMDist;
                    bt = new ButtonToggle(sbx+tx, ty, bid,grid,mouseIn);
                    btMD = bt;
                    Iid = InfoMDist; break;
                case 2: bid = ToggleAStar;
                    bt = new ButtonToggle(sbx+tx, ty, bid,grid,mouseIn);
                    bt.setIndependent(btMD);
                    bt.setBundled(btRedund);
                    btMD.setIndependent(bt);
                    btRedund.setBundled(bt);
                    Iid = InfoAStar; break;
                case 3: bid = ToggleColor;
                    bt = new ButtonToggle(sbx+tx, ty, bid,grid,mouseIn);
                    Iid = InfoColor; break;
                default:
                    throw new IllegalStateException("Unexpected value: " + i);
            }
            bttManager.addButton(bt);
            bttManager.addButton(new ButtonInfo(sbx+tx+243, ty-2, Iid,grid,mouseIn));
        }
        ButtonBar b = new ButtonBar(sbx+tx,405,SpeedBar,grid,mouseIn);
        //grid.setButtonBar(b);
        bttManager.addButton(b);
    }

    public void setBgCol(Color c) {
        bgCol = c;
    }
}
