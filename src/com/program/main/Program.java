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
        gui = new GUI();
        ptManager = new PtManager();
        grid.setPtManager(ptManager);
        grid.setGUI(gui);
        gui.setGrid(grid);
        this.addMouseListener(mouseIn);
        this.addMouseMotionListener(mouseIn);
        createButtons();

        window = new Window(WIDTH, HEIGHT, "bruh", this);
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

        //Draw Stuff
        g.setColor(bgCol);
        g.fillRect(0,0,WIDTH,HEIGHT);

        bttManager.render(g);
        grid.render(g);
        mouseIn.render(g);
        ptManager.render(g);
        gui.render(g); // DO THIS LAST

        g.dispose();
        bs.show();
    }

    public void createButtons() {
        bttManager = new BttManager();
        grid.setBttManager(bttManager);
        bttManager.addButton(new ButtonText(grid.getGridStartX()+30,grid.getGridStartY()-50,TextStart,grid,mouseIn));
        bttManager.addButton(new ButtonText(grid.getGridStartX()+200,grid.getGridStartY()-40,TextReset,grid,mouseIn));
        bttManager.addButton(new ButtonText(grid.getGridStartX()+grid.getGridActualW()-150,grid.getGridStartY()-60,TextMapEdit,grid,mouseIn));
        int sbx = 384;
        ButtonText bl = new ButtonText(WIDTH-sbx,20,TextLoadMap,grid,mouseIn);
        grid.setButtonLoad(bl);
        gui.setButtonLoad(bl);
        bttManager.addButton(bl);
        int tx = 10;
        for (int i  = 0; i < 4; i++) {
            int ty = 120+60*i;
            BttID bid = TextStart, Iid = TextStart;
            switch (i){
                case 0: bid = ToggleRedund; Iid = InfoRedund; break;
                case 1: bid = ToggleMDist; Iid = InfoMDist; break;
                case 2: bid = ToggleAStar; Iid = InfoAStar; break;
                case 3: bid = ToggleColor; Iid = InfoColor; break;
            }
            bttManager.addButton(new ButtonToggle(WIDTH-sbx+tx, ty, bid,grid,mouseIn));
            bttManager.addButton(new ButtonInfo(WIDTH-sbx+tx+300, ty-4, Iid,grid,mouseIn));
        }
        ButtonBar b = new ButtonBar(WIDTH-sbx+120,370,TextLoadMap,grid,mouseIn);
        grid.setButtonBar(b);
        bttManager.addButton(b);
    }

    public void setBgCol(Color c) {
        bgCol = c;
    }

    public int getWIDTH() {
        return WIDTH;
    }
    public int getHEIGHT() {
        return HEIGHT;
    }
}
