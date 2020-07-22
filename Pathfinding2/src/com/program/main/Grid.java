package com.program.main;

import com.program.main.buttons.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Grid {
    private Random r = new Random();
    private final GridFunctions gridFunctions = new GridFunctions(this);
    //objects
    private MouseIn mouseIn;
    private Program program;
    private BttManager bttManager;
    private ButtonBar buttonBar;
    private ButtonText buttonLoad;
    private GUI gui;
    private PtManager ptManager;
    //board
    private int gridW, gridH, sqSize;
    private final int gridSize;
    private int gridStartX, gridStartY;
    public SquareID[][] board;
    private enum SquareID {
        Empty,
        Obstacle,
        Man,
        Goal;
    }
    private int[][] timesVisited;
    private int squaresVisited, squaresRevisited;
    //DRAW
    private Color bgCol;
    private Color lineCol;
    //states
    private boolean running, editingMap;
    private boolean mapEHolding;
    private boolean mapEHoldingMan;
    //elements
    private int manX, manY, manXPrev, manYPrev, manOrigX, manOrigY;
    private float manDis;
    private int goalX, goalY, goalOrigX, goalOrigY;
    private int timer, a, moveTime, origMoveTime, moveID, numMoves;
    private int[] moves;
    //Toggles
    private boolean drawFloor;

    public Grid(MouseIn imouseIn, Program program) {
        mouseIn = imouseIn;
        this.program = program;
        program.setBgCol(Color.white);

        //pretty colors
        bgCol = Color.white;
        lineCol = Color.black;

        //init
        gridSize = 550;
        drawFloor = false;

        running = false;
        editingMap = false;
        mapEHolding = false;
        mapEHoldingMan = false;

        timer = -1;
        a = 0;
        moveTime = 30;
        origMoveTime = moveTime;
        moveID = 0;
        numMoves = 10;
        moves = new int[numMoves];
        //temp moves
            moves[0] = 1;
            moves[1] = 2;
            moves[2] = 0;
            moves[3] = 3;
            moves[4] = 1;
            moves[5] = 2;
            moves[6] = 3;
            moves[7] = 1;
            moves[8] = 2;
            moves[9] = 3;

        initGrid(15,15);

        //man!
        //manX = r.nextInt(gridW);
        //manY = r.nextInt(gridH);
        manX = gridW/2;
        manY = gridH/2;
        manOrigX = manX;
        manOrigY = manY;
        manDis = 0f;
        board[manX][manY] = SquareID.Man;

        //Goal
        int i = 0;
        while (i < 1) {
            goalX = r.nextInt(gridW);
            goalY = r.nextInt(gridH);
            if (board[goalX][goalY] == SquareID.Empty) {
                board[goalX][goalY] = SquareID.Goal;
                i++;
            }
        }
        goalOrigX = goalX;
        goalOrigY = goalY;

        //obstacles
        int ii = 0;
        while (ii < 60)
        {
            int xx,yy;
            xx = r.nextInt(gridW);
            yy = r.nextInt(gridH);
            if (board[xx][yy] == SquareID.Empty) {
                board[xx][yy] = SquareID.Obstacle;
                ii++;
                //return;
            }
        }
    }

    public void tick() {
        gridFunctions.animateMan(manDis);
        boolean mClicked = mouseIn.getClicked();
        boolean mPressed = mouseIn.getPressed();
        boolean mReleased = mouseIn.getReleased();
        if (mClicked) a++;

        // RUN
        if (running) {
            timer++;
            if (timer%moveTime == 0) {
                /*if (moveID == numMoves)
                {
                    flipRunning();
                } else moveFromArray(true);*/
                randomMove();
            }
        }
        // EDIT
        if (editingMap && !running) {
            int mx = mouseIn.getMouseX();
            int my = mouseIn.getMouseY();
            int sqOnX = -1;
            int sqOnY = -1;
            for (int i = 0; i < gridH; i++) {
                for (int ii = 0; ii < gridW; ii++) {
                    if (mx > gridStartX + ii * sqSize && mx < gridStartX + ii * sqSize + sqSize &&
                            my > gridStartY + i * sqSize && my < gridStartY + i * sqSize + sqSize) {
                        sqOnX = ii;
                        sqOnY = i;
                    }
                }
            }
            if (!(sqOnX == -1) && !(sqOnY == -1)) {
                if (!mapEHolding) {
                    switch (board[sqOnX][sqOnY]) {
                        // MOUSE CLICKED/DRAGGED WHATEVER CODE //
                        case Empty:
                            if (mClicked)
                                board[sqOnX][sqOnY] = SquareID.Obstacle;
                            break;
                        case Obstacle:
                            if (mClicked)
                                board[sqOnX][sqOnY] = SquareID.Empty;
                            break;
                        case Man:
                            if (mPressed) {
                                board[sqOnX][sqOnY] = SquareID.Empty;
                                mapEHolding = true;
                                mapEHoldingMan = true;
                            }
                            break;
                        case Goal:
                            if (mPressed) {
                                board[sqOnX][sqOnY] = SquareID.Empty;
                                mapEHolding = true;
                                mapEHoldingMan = false;
                            }
                            break;
                    }
                }
            }
            if (mapEHolding) {
                if (mReleased ||
                        !(mx > gridStartX && mx < gridStartX+gridW*sqSize &&
                                my > gridStartY && my < gridStartY+gridH*sqSize)) {
                    int holdX = goalX;
                    int holdY = goalY;
                    SquareID holdID = SquareID.Goal;
                    if (mapEHoldingMan) {
                        holdX = manX;
                        holdY = manY;
                        holdID = SquareID.Man;
                    }
                    int partX = 0;
                    int partY = 0;
                    int pYoff = (int) (sqSize*0.99);
                    if (!(sqOnX == -1) && !(sqOnY == -1) && board[sqOnX][sqOnY] == SquareID.Empty) {
                        if (mapEHoldingMan) {
                            manX = sqOnX;
                            manY = sqOnY;
                        } else {
                            goalX = sqOnX;
                            goalY = sqOnY;
                        }
                        board[sqOnX][sqOnY] = holdID;
                        partX = sqOnX*sqSize+sqSize/2 + gridStartX;
                        partY = sqOnY*sqSize+pYoff + gridStartY;
                    } else {
                        board[holdX][holdY] = holdID;
                        partX = holdX*sqSize+sqSize/2 + gridStartX;
                        partY = holdY*sqSize+pYoff + gridStartY;
                    }
                    createParticles(partX,partY);
                    mapEHolding = false;
                }
            }
        }
            //System.out.println("timer: " + timer);
    }

    public void render(Graphics g) {
        //background
        //g.setColor(bgCol);
        //g.fillRect(0,0,program.WIDTH,program.HEIGHT);

        //debug
        g.setColor(lineCol);
        //g.drawString(String.valueOf(moveID),5,48);
        g.drawString(String.valueOf(mapEHoldingMan),5,48);
        //

        int lnW = 2;
        if (gridW >= 30) lnW = 1;
        if (gridH >= 30) lnW = 1;
        int manSize = (int) (sqSize*0.8);
        if (manSize%2 == 1) manSize++;

        //coloured squares!
        if (drawFloor) drawColorGrid(g);
        //grid
        g.setColor(lineCol);
        for (int i=0; i<gridW + 1; i++) {
            g.fillRect(gridStartX+i*sqSize,gridStartY,lnW,gridH*sqSize);
        }
        for (int i=0; i<gridH + 1; i++) {
            g.fillRect(gridStartX,gridStartY+i*sqSize,gridW*sqSize,lnW);
        }
        //Grid features
        for (int i=0; i<gridH; i++) {
            for (int ii=0; ii<gridW; ii++) {
                switch (board[ii][i]) {
                    case Obstacle:
                        g.setColor(lineCol);
                        g.fillRect(lnW/2+gridStartX + ii * sqSize + (sqSize - manSize) / 2,
                                lnW/2+gridStartY + i * sqSize + (sqSize - manSize) / 2,
                                manSize, manSize);
                        break;
                    case Man:
                        //Man Displacement
                        int xD = 0;
                        int yD = 0;
                        if (!(manX == manXPrev)) xD = manXPrev-manX;
                        if (!(manY == manYPrev)) yD = manYPrev-manY;
                        g.setColor(Color.red);
                        g.fillOval(lnW/2+gridStartX + ii * sqSize + (sqSize - manSize) / 2 + (xD*(int)manDis),
                                lnW/2+gridStartY + i * sqSize + (sqSize - manSize) / 2 + (yD*(int)manDis),
                                manSize, manSize);
                        break;
                    case Goal:
                        g.setColor(Color.blue);
                        g.fillOval(lnW/2+gridStartX + ii * sqSize + (sqSize - manSize) / 2,
                                lnW/2+gridStartY + i * sqSize + (sqSize - manSize) / 2,
                                manSize, manSize);
                        break;
                    }
                }
            }
        // IF holding man / goal
        if (mapEHolding){
            Color cc = Color.blue;
            if (mapEHoldingMan) cc = Color.red;
            g.setColor(cc);
            g.fillOval(mouseIn.getMouseX()-manSize/2,
                    mouseIn.getMouseY()-manSize/2,
                    manSize,manSize);
        }

        /*g.setColor(Color.black);
        g.fillRect(40,40,100,100);*/
    }

    private void initGrid(int gridW, int gridH) {
        this.gridW = gridW;
        this.gridH = gridH;
        board = new SquareID[gridW][gridH];
        timesVisited = new int[gridW][gridH];
        squaresVisited = 0;
        squaresRevisited = 0;

        int sS = gridW;
        if (gridH > gridW) sS = gridH;
        sqSize = (gridSize/sS);
        gridStartX = 50 + (gridSize - sqSize*gridW)/2;
        gridStartY = 70 + (gridSize - sqSize*gridH)/2;

        //start with empty grid
        for (int i=0; i<gridH; i++) {
            for (int ii=0; ii<gridW; ii++) {
                board[ii][i] = SquareID.Empty;
                timesVisited[ii][i] = 0;
            }
        }
    }

    public void saveFile() {
        if (board[goalX][goalY] == SquareID.Goal) { //Check if goal is there
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save As");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT files","txt");
            fileChooser.setFileFilter(filter);

            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                file = new File(file.toString() + ".txt");
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    PrintWriter printWriter = new PrintWriter(fileWriter);

                    //THE STUFF
                    printWriter.println(gridW);
                    printWriter.println(gridH);
                    for (int i = 0; i < gridH; i++) {
                        String line = "";
                        for (int ii = 0; ii < gridW; ii++) {
                            char c = '-';
                            switch (board[ii][i]) {
                                case Obstacle: c = 'x';
                                    break;
                                case Man: c = 'm';
                                    break;
                                case Goal: c = 'g';
                                    break;
                            }
                            line += c;
                        }
                        printWriter.println(line);
                    }
                    printWriter.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("INVALID MAP!!!!!");
            /*JFrame error = new JFrame("Cannot Save");
            error.setPreferredSize(new Dimension(300, 220));
            Container container = error.getContentPane();
            container.setLayout(null);

            JLabel text = new JLabel("INVALID MAP");
            text.setBounds(30,50,200,100);

            container.add(text);

            error.pack();
            error.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            error.setResizable(false);
            error.setLocationRelativeTo(null);
            error.setVisible(true);*/
        }
    }

    public void openFile() throws FileNotFoundException {
        JFileChooser fileChooser = new JFileChooser();
        //fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Choose a Map");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT files","txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            //String direct = "" +fileChooser.getCurrentDirectory();
            File file = fileChooser.getSelectedFile();

            Scanner inputTest = new Scanner(file);

            if (checkIfValidFile(inputTest)) { //BUILD SELECTED GRID
                if (running) flipRunning();
                buttonLoad.setMapName(file.getName());

                Scanner input = new Scanner(file);
                gridW = input.nextInt();
                gridH = input.nextInt();
                initGrid(gridW, gridH);
                for (int i = 0; i < gridH; i++) {
                    String line = input.next();
                    for (int ii = 0; ii < gridW; ii++) {
                        char c = line.charAt(ii);
                        switch (c) {
                            case 'x': board[ii][i] = SquareID.Obstacle;
                                break;
                            case 'm': board[ii][i] = SquareID.Man;
                                manX = ii;
                                manY = i;
                                manOrigX = manX;
                                manOrigY = manY;
                                break;
                            case 'g': board[ii][i] = SquareID.Goal;
                                goalX = ii;
                                goalY = i;
                                goalOrigX = goalX;
                                goalOrigY = goalY;
                                break;
                        }
                    }
                }
                input.close();
            } else ;
        }
    }

    private boolean checkIfValidFile(Scanner input) {
        int gridW = 0, gridH = 0;

        //Test for grid size
        if (input.hasNextInt()) gridW = input.nextInt();
        if (input.hasNextInt()) gridH = input.nextInt();
        if (gridW < 2 || gridH < 2) {input.close();return false;}

        //Test for number of lines
        ArrayList<String> lines = new ArrayList<String>();
        while(input.hasNext()) {
            lines.add(input.next());
        }
        if (!(lines.size() == gridH)) {input.close();return false;}

        //Test for number of chars in each string
        //Test for exactly ONE instance of m, and of g
        int ms = 0, gs = 0;
        for (int i = 0; i < gridH; i++) {
            String line = lines.get(i);
            if (!(line.length() == gridW)) {input.close();return false;}
            for (int ii = 0; ii < gridW; ii++) {
                char c = line.charAt(ii);
                if (c == 'm') ms++;
                if (c == 'g') gs++;
            }
        }
        if (!(ms == 1) || !(gs == 1)) {input.close();return false;}

        //you win!
        {input.close();return true;}
    }

    public void moveFromArray(boolean forward) {
        manXPrev = manX;
        manYPrev = manY;
        manDis = sqSize;

        int dir = 0;
        if (forward) {
            if (moveID == -1) moveID++;
            if (moveID < numMoves) {
                dir = moves[moveID];
                moveID++;
                moveMan(dir);
            }
        }
        else {
            if (moveID > 0) {
                moveID--;
                dir = moves[moveID];
                if (dir <= 1) dir += 2;
                else dir -= 2;
                moveMan(dir);
            }
        }
    }

    private void randomMove() {
        manXPrev = manX;
        manYPrev = manY;
        manDis = sqSize;

        int dir = 0;
        int i = 0;
        int ii = 0;
        while (i < 1 && ii < 100) {
            dir = r.nextInt(4);
            int Hmove = 0;
            int Vmove = 1;
            if (dir%2 == 1){
                Vmove--;
                Hmove++;
            }
            if (dir >= 2) {
                Vmove *= -1;
                Hmove *= -1;
            }
            int o = 1;
            if (Hmove == 0) {
                if (Vmove == 1) o = 0;
                if (!(manY == (gridH-1)*o) && !(board[manX][manY-Vmove] == SquareID.Obstacle)) i++;
            } else {
                if (Hmove == -1) o = 0;
                if (!(manX == (gridW-1)*o) && !(board[manX + Hmove][manY] == SquareID.Obstacle)) i++;
            }
            ii++;
        } if (ii < 100) moveMan(dir);
    }

    private void moveMan(int dir) {
        board[manX][manY] = SquareID.Empty;
        int Hmove = 0;
        int Vmove = 1;
        if (dir%2 == 1){
            Vmove--;
            Hmove++;
        }
        if (dir >= 2) {
            Vmove *= -1;
            Hmove *= -1;
        }
        manX += Hmove;
        manY -= Vmove;
        board[manX][manY] = SquareID.Man;
        squaresVisited++;
        if (timesVisited[manX][manY] > 0) {
            squaresRevisited++;
        }
        timesVisited[manX][manY]++;

        /*
        if (dir == 0) System.out.println("up");
        if (dir == 1) System.out.println("right");
        if (dir == 2) System.out.println("down");
        if (dir == 3) System.out.println("left");
         */
    }

    private void drawColorGrid(Graphics G) {
        for (int i=0; i<gridH; i++) {
            for (int ii=0; ii<gridW; ii++) {
                if (timesVisited[ii][i] == 0) G.setColor(new Color(130, 130, 160));
                else {
                    int baseC = 50;
                    int rc = baseC, gc = baseC, bc = baseC;
                    int maxVisit = 100;
                    float per = (float) (timesVisited[ii][i]) / maxVisit;
                    float m1 = 0.1f, m2 = 0.22f, m3 = 0.4f;
                    if (per > 1) per = 1;
                    if (per <= m1) {
                        bc = 255;
                        gc = approach(baseC, 255, per, 0, m1);
                    } else {
                        if (per <= m2) {
                            gc = 255;
                            bc = approach(255, baseC, per, m1, m2);
                            rc = approach(baseC, 255, per, m1, m2);
                        } else {
                            rc = 255;
                            if (per <= m3) {
                                gc = approach(255, baseC, per, m2, m3);
                            } else {
                                gc = approach(baseC, 0, per, m3, 1);
                                bc = approach(baseC, 0, per, m3, 1);
                            }
                        }
                    }

                    G.setColor(new Color(rc, gc, bc));
                }
                G.fillRect(gridStartX + sqSize * ii, gridStartY + sqSize * i, sqSize, sqSize);
                /*if (board[ii][i] == SquareID.Empty) {
                    G.setColor(Color.black);
                    G.drawString(String.valueOf(timesVisited[ii][i]),
                            gridStartX + sqSize * ii + 3,
                            gridStartY + sqSize * i + 17);
                }*/
            }
        }
    }

    private void createParticles(int x, int y) {
        //bttManager.addButton(new ButtonToggle(WIDTH-sbx+tx, ty, bid,grid,mouseIn));
        float rand = r.nextInt(2) +4;
        float c = (rand-1)/2;
        for (int i = 0; i < rand; i++) {
            float ihsp = ((int) Math.signum(i-c)+(i-c)*0.3f);
            float range = 0.8f;
            ihsp = 2.2f*ihsp+r.nextFloat()*range-range/2+0.5f;
            ptManager.addParticle(new Particle(x,y,ihsp,ptManager));
        }
    }

    public void resetBoard() {
        if (running) flipRunning();
        buttonBar.resetBar();
        moveTime = origMoveTime;
        moveID = 0;
        board[manX][manY] = SquareID.Empty;
        board[goalX][goalY] = SquareID.Empty;
        manX = manOrigX;
        manY = manOrigY;
        goalX = goalOrigX;
        goalY = goalOrigY;
        board[manX][manY] = SquareID.Man;
        board[goalX][goalY] = SquareID.Goal;
        for (int i=0; i<gridH; i++) {
            for (int ii=0; ii<gridW; ii++) {
                timesVisited[ii][i] = 0;
            }
        }
        squaresVisited = 0;
        squaresRevisited = 0;
    }

    public void flipRunning() {
        running = !running;
        manDis = 0;
        timer = -1;
    }

    public void flipEditingMap() {
        editingMap = !editingMap;
        if (editingMap) {
            program.setBgCol(Color.darkGray);
            lineCol = Color.lightGray;
            if (running) {
                flipRunning();
            }
        } else {
            program.setBgCol(Color.white);
            lineCol = Color.black;
        }
    }

    public void setDrawFloor(boolean tf) {
        drawFloor = tf;
    }

    public void setBttManager(BttManager bttManager) {
        this.bttManager = bttManager;
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void setPtManager(PtManager ptManager) { this.ptManager = ptManager; }

    public void setButtonBar(ButtonBar buttonBar) {
        this.buttonBar = buttonBar;
    }

    public void setButtonLoad(ButtonText buttonLoad) {
        this.buttonLoad = buttonLoad;
    }

    public void setmanDis(float manDis) { this.manDis = manDis; }

    public void setMoveTime(int moveTime) {
        this.moveTime = moveTime;
    }

    public GUI getGUI(){
    return this.gui;
    }

    public int getGridStartX(){
        return this.gridStartX;
    }

    public int getGridStartY(){
        return this.gridStartY;
    }

    public int getVisitedData(boolean revisited) {
        if (revisited) {
            return squaresRevisited;
        } return squaresVisited;
    }

    public int getGridActualW() { return sqSize*gridW; }

    public boolean getifRunning() { return running; }

    public boolean getifEditingMap() { return editingMap; }

    public Color getLineCol() { return lineCol; }

    public Color getBgCol() { return bgCol; }

    public int getMoveID() { return moveID; }

    public int getNumMoves() { return numMoves; }

    private int approach(int lower, int upper, float progress, float pLower, float pUpper) {
        float percent = (progress-pLower)/(pUpper-pLower);
        int var = (int) (lower+(float)(upper-lower)*percent);
        return var;
    }
}

