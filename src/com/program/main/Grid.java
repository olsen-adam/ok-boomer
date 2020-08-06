package com.program.main;

import com.program.main.buttons.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Grid {
    private Random r = new Random();
    //objects
    private final GridFunctions gridFunctions = new GridFunctions(this);
    private MouseIn mouseIn;
    public Program program;
    private BttManager bttManager;
    private ButtonCircle buttonStart;
    private ButtonText buttonLoad;

    private GUI gui;
    private PtManager ptManager;
    //board
    private int gridW, gridH, sqSize, lnW = 2;
    public final int gridSize, sideBarX, prefGridX, prefGridY;
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
    private boolean veryLarge;
    //DRAW
    public Color bgCol, lineCol, lightLineCol, windowCol,
            hoverCol, clickCol, sliderCol, sliderClickCol;
    public Font
            fontBase = new Font("SansSerif", Font.PLAIN, 15),
            fontBold = new Font("SansSerif", Font.BOLD, 16);
    //states
    private boolean running, editingMap, editErasing;
    private boolean mapEHolding;
    private boolean mapEHoldingMan;
    int manHatDist = 100;
    //elements
    private int manX, manY, manXPrev, manYPrev, manOrigX, manOrigY;
    private float manDis, manWMult = 1;
    private SquareID placeSilhoutte;
    private int goalX, goalY, goalOrigX, goalOrigY;
    public int timer, a, moveTime, origMoveTime, moveID, numMoves;
    private int[] moves;
    private final Color manCol = new Color(255, 84, 74), manColFg = new Color(219, 57, 75);
    private BufferedImage imgBurger, imgBurgerBig, imgBurgerSmall;
    //Toggles
    public boolean
            togRedund,
            togMDist,
            togAStar,
            drawFloor;
    //Preset maps
    private int presetOn;
    private final int numPresets = 3;
    private String[] presetName;
    private int[] presetWidth, presetHeight;
    private String[] presetData;

    public Grid(MouseIn imouseIn, Program program) {
        mouseIn = imouseIn;
        this.program = program;
        program.setBgCol(Color.white);

        //pretty colors
        setColorsDefault();

        //init
        gridSize = 550;
        sideBarX = Program.WIDTH-374;
        prefGridX = 39;
        prefGridY = 80;
        drawFloor = false;

        running = false;
        editingMap = false;
        mapEHolding = false;
        mapEHoldingMan = false;
        placeSilhoutte = SquareID.Empty;

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

        initPresets();
        loadPreset(0);

        //image
        try {
            imgBurgerSmall = ImageIO.read(getClass().getResourceAsStream("/burger.png"));
            imgBurger = imgBurgerSmall;
            imgBurgerBig = ImageIO.read(getClass().getResourceAsStream("/burgerBig.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        gridFunctions.animateMan(manDis, manWMult);

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
                manHatDist = Math.abs(manX-goalX) + Math.abs(manY-goalY);
            }
        }
        // EDIT
        if (editingMap) {
            placeSilhoutte = SquareID.Obstacle;
            int mx = mouseIn.getMouseX();
            int my = mouseIn.getMouseY();
            int sqOnX = -1;
            int sqOnY = -1;
            for (int i = 0; i < gridH; i++) {
                for (int ii = 0; ii < gridW; ii++) {
                    if (mx > gridStartX + ii * sqSize && mx <= gridStartX + ii * sqSize + sqSize &&
                            my > gridStartY + i * sqSize && my <= gridStartY + i * sqSize + sqSize) {
                        sqOnX = ii;
                        sqOnY = i;
                    }
                }
            }
            if (!(sqOnX == -1) && !(sqOnY == -1)) {
                if (!mapEHolding) {
                    if (mPressed) {editErasing = false;}
                    switch (board[sqOnX][sqOnY]) {
                        // MOUSE CLICKED/DRAGGED WHATEVER CODE //
                        case Empty:
                            if (mClicked || (mouseIn.getDragged() && !editErasing)) {
                                board[sqOnX][sqOnY] = SquareID.Obstacle;
                                if (running) flipRunning();
                            }
                            break;
                        case Obstacle:
                            if (mPressed) {editErasing = true;}
                            if (mClicked || (mouseIn.getDragged() && editErasing)) {
                                board[sqOnX][sqOnY] = SquareID.Empty;
                                if (running) flipRunning();
                            }
                            break;
                        case Man:
                            if (mPressed) {
                                board[sqOnX][sqOnY] = SquareID.Empty;
                                mapEHolding = true;
                                mapEHoldingMan = true;
                                if (running) flipRunning();
                            }
                            break;
                        case Goal:
                            if (mPressed) {
                                board[sqOnX][sqOnY] = SquareID.Empty;
                                mapEHolding = true;
                                mapEHoldingMan = false;
                                if (running) flipRunning();
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
                            manX = sqOnX; manXPrev = manX;
                            manY = sqOnY; manYPrev = manY;
                            manWMult = 1.4f;
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
                    mapEHoldingMan = false;
                }

                if (mapEHoldingMan) {
                    placeSilhoutte = SquareID.Man;
                } else placeSilhoutte = SquareID.Goal;
            }
        } else {
            placeSilhoutte = SquareID.Empty;
        }
            //System.out.println("timer: " + timer);
    }

    public void render(Graphics g) {
        //background
        //g.setColor(bgCol);
        //g.fillRect(0,0,program.WIDTH,program.HEIGHT);

        //debug
        /*g.setColor(lineCol);
        g.drawString(String.valueOf(manWMult),5,48);
        g.drawString(String.valueOf(manDis),5,68);
        //*/
        int manSize = (int) (sqSize*0.8);
        if (manSize%2 == 1) manSize++;

        gridFunctions.drawWindows(g);
        //coloured squares!
        if (drawFloor) drawColorGrid(g);
        else {
            //g.setColor(bgCol);
            //g.fillRect(gridStartX,gridStartY,gridW*sqSize, gridH*sqSize);
            //DEFAULT GRID COL
            for (int i=0; i<gridH; i++) {
                int odd = (i%2);
                for (int ii = 0; ii < gridW; ii++) {
                    Color cc;
                    if ((ii+odd)%2 == 0) cc = hoverCol;
                    else cc = windowCol;
                    g.setColor(cc);
                    g.fillRect(gridStartX + sqSize * ii, gridStartY + sqSize * i, sqSize, sqSize);
                }
            }
        }
        //grid
        if (drawFloor) {
            g.setColor(lineCol);
            for (int i = 0; i < gridW + 1; i++) {
                //if (i == gridW || i == 0)
                g.fillRect(gridStartX + i * sqSize -lnW/2, gridStartY-lnW/2, lnW, gridH * sqSize + lnW);
            }
            for (int i = 0; i < gridH + 1; i++) {
                //if (i == gridW || i == 0)
                g.fillRect(gridStartX-lnW/2, gridStartY + i * sqSize -lnW/2, gridW * sqSize + lnW, lnW);
            }
        }
        //Grid features
        for (int i=0; i<gridH; i++) {
            for (int ii=0; ii<gridW; ii++) {
                switch (board[ii][i]) {
                    case Empty:
                        //Silhouettes!!!
                        if (!(placeSilhoutte == SquareID.Empty)) {
                            int mx = mouseIn.getMouseX();
                            int my = mouseIn.getMouseY();
                            if (mx > gridStartX + ii * sqSize && mx <= gridStartX + ii * sqSize + sqSize &&
                                    my > gridStartY + i * sqSize && my <= gridStartY + i * sqSize + sqSize) {
                                int alpha = 90;
                                switch (placeSilhoutte) {
                                    case Obstacle: g.setColor(new Color(lineCol.getRed(), lineCol.getGreen(), lineCol.getBlue(), alpha));
                                        g.fillRect(gridStartX + ii * sqSize + (sqSize - manSize) / 2,
                                                gridStartY + i * sqSize + (sqSize - manSize) / 2,
                                                manSize, manSize);
                                        break;
                                    case Man:
                                        g.setColor(new Color(manCol.getRed(), manCol.getGreen(), manCol.getBlue(), alpha));
                                        /*g.fillOval(gridStartX + ii * sqSize + (sqSize - manSize) / 2,
                                                gridStartY + i * sqSize + (sqSize - manSize) / 2,
                                                manSize, manSize);*/
                                        drawManExt(g, gridStartX + ii * sqSize + (sqSize - manSize) / 2,
                                                gridStartY + i * sqSize + (sqSize - manSize) / 2, manSize, manSize, alpha);
                                        break;
                                    case Goal: //g.setColor(new Color(0, 0, 255, alpha));
                                        /*g.fillOval(gridStartX + ii * sqSize + (sqSize - manSize) / 2,
                                                gridStartY + i * sqSize + (sqSize - manSize) / 2,
                                                manSize, manSize);*/
                                        drawGoalExt(g, gridStartX + ii * sqSize + (sqSize)/ 2,
                                                gridStartY + i * sqSize + (int)(sqSize*0.1), 1, alpha);
                                        break;
                                }
                            }
                        }
                        break;
                    case Obstacle:
                        if (editingMap && !drawFloor) g.setColor(lightLineCol);
                        else g.setColor(lineCol);
                        g.fillRect(gridStartX + ii * sqSize + (sqSize - manSize) / 2,
                                gridStartY + i * sqSize + (sqSize - manSize) / 2,
                                manSize, manSize);
                        break;
                    case Man:
                        //Man Displacement
                        int xD = 0;
                        int yD = 0;
                        if (!(manX == manXPrev)) xD = manXPrev-manX;
                        if (!(manY == manYPrev)) yD = manYPrev-manY;
                        //Width/height multiplier
                        int mW = (int) (manSize*(manWMult));
                        int mH = (int) (manSize*(2-manWMult));
                        int squishYoff = 0;
                        if (!running && !(manWMult == 1)) {
                            squishYoff = manSize - mH;
                        }
                        if (!veryLarge) {
                            /*g.fillOval(gridStartX + ii * sqSize + (sqSize - mW) / 2 + (xD * (int) manDis),
                                    gridStartY + i * sqSize + (sqSize - mH) / 2 + (yD * (int) manDis) + squishYoff,
                                    mW, mH);*/
                            drawManExt(g, gridStartX + ii * sqSize + (sqSize - mW) / 2 + (xD * (int) manDis),
                                    gridStartY + i * sqSize + (sqSize - mH) / 2 + (yD * (int) manDis) + squishYoff,
                                    mW, mH, 255);
                        } else  {
                            g.setColor(Color.red);
                            g.fillRect(gridStartX + ii * sqSize, gridStartY + i * sqSize,
                                    sqSize, sqSize);
                        }
                        break;
                    case Goal:
                        if (!veryLarge)
                        /*g.fillOval(gridStartX + ii * sqSize + (sqSize - manSize) / 2,
                                gridStartY + i * sqSize + (sqSize - manSize) / 2,
                                manSize, manSize);*/
                        drawGoalExt(g, gridStartX + ii * sqSize + (sqSize) / 2,
                                gridStartY + i * sqSize + (int)(sqSize*0.1),
                                1, 255);
                        else  {
                            g.setColor(new Color(255, 127, 22));
                            g.fillRect(gridStartX + ii * sqSize, gridStartY + i * sqSize,
                                    sqSize, sqSize);
                        }
                        break;
                    }
                }
            }
        // IF holding man / goal
        if (mapEHolding){
            if (mapEHoldingMan) {
                drawManExt(g, mouseIn.getMouseX() - manSize / 2,
                        mouseIn.getMouseY() - manSize / 10, (int)((float)manSize*0.98), (int)((float)manSize*1.12), 255);
            } else {
                g.setColor(Color.blue);
                /*g.fillOval(mouseIn.getMouseX() - manSize / 2,
                        mouseIn.getMouseY() - manSize / 10,
                        manSize, manSize);*/
                drawGoalExt(g, mouseIn.getMouseX(),
                        mouseIn.getMouseY()-(int)(sqSize*0.15),
                        1.1f, 255);
            }
        }

        //INFO BOX
        /*if (editingMap) {
            gui.requestDrawInfo(0, "Place and remove obstacles by\nclicking a spot on the grid.\n\nDrag and drop the ball or the\ngoal to move them");
        } else {
            if (!(togRedund || togMDist || togAStar)) {
                gui.requestDrawInfo(0, "By default, the ball will move\ncompletely randomly");
            }
        }*/
    }

    private void initGrid(int gridW, int gridH) {
        this.gridW = gridW;
        this.gridH = gridH;
        if (gridW <= 29 && gridH <= 29) lnW = 2;
        else {
            if (gridW <= 99 && gridH <= 99) lnW = 1;
            else lnW = 0;
        }

        board = new SquareID[gridW][gridH];
        timesVisited = new int[gridW][gridH];
        squaresVisited = 0;
        squaresRevisited = 0;

        //Grid size draw things
        int sS = gridW;
        if (gridH > gridW) sS = gridH;
        veryLarge = (sS > 79); //if 80 or more
        if (sS < 10) {
            imgBurger = imgBurgerBig;
        } else imgBurger = imgBurgerSmall;
        manHatDist = 100;
        sqSize = (gridSize/sS);
        gridStartX = prefGridX + (gridSize - sqSize*gridW)/2;
        gridStartY = prefGridY + (gridSize - sqSize*gridH)/2;

        //start with empty grid
        for (int i=0; i<gridH; i++) {
            for (int ii=0; ii<gridW; ii++) {
                board[ii][i] = SquareID.Empty;
                timesVisited[ii][i] = 0;
            }
        }
    }

    private void initPresets() {
        presetName = new String[numPresets];
        presetWidth = new int[numPresets];
        presetHeight = new int[numPresets];
        presetData = new String[numPresets];

        presetName[0] = "Test Map";
        presetWidth[0] = 9; presetHeight[0] = 9;
        presetData[0] = "----------------------x--------x-----m--x--g-----x--------x----------------------";

        presetName[1] = "Maze";
        presetWidth[1] = 15; presetHeight[1] = 15;
        presetData[1] = "----x----x-----x-x---x---x-------x--------xxx-x------------------x---x--x-x-----x---g---x------x---x----xx------------x-x------m------x---x-------x-------xx---xx---xx-----xxx------x----------xxx-xx-x-------x----x---x-----x-x-";

        presetName[2] = "Large Grid";
        presetWidth[2] = 30; presetHeight[2] = 20;
        presetData[2] = "------------------------------------------------------------------------------------------------------------------g---------------------------------------------------------------------xx-xx----x-------------------------------------------------------------------------------------------------------------xxxxxxxxxxxxxxxxxxxxxxxx---------------------------------------------------------------------------------x-----------------------------------------------------------x-----------------------------x----------------m------------x-----------------------------x-----------------------------------------";
    }

    public void loadPreset(int preset) {
        if (running) flipRunning();

        presetOn = preset;
        initGrid(presetWidth[preset],presetHeight[preset]);

        String line = presetData[preset];
        for (int i = 0; i < presetHeight[preset]; i++) {
            for (int ii = 0; ii < presetWidth[preset]; ii++) {
                char c = line.charAt(ii+i*presetWidth[preset]);
                switch (c) {
                    case 'x': board[ii][i] = SquareID.Obstacle;
                        break;
                    case 'm': board[ii][i] = SquareID.Man;
                        manX = ii; manXPrev = manX;
                        manY = i; manYPrev = manY;
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
    }

    public void saveFile() {
        if (running) flipRunning();
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
            try {
                JOptionPane.showMessageDialog(null, "Invalid Map, Goal Object Missing");
            } catch (Exception ex) {
            }
        }
    }

    public void openFile(Boolean textFile, String chooserName, String descript, String ext) throws FileNotFoundException {
        JFileChooser fileChooser = new JFileChooser();
        //fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle(chooserName); ///
        FileNameExtensionFilter filter = new FileNameExtensionFilter(descript,ext); ///
        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            //String direct = "" +fileChooser.getCurrentDirectory();
            File file = fileChooser.getSelectedFile();

            Scanner inputTest = new Scanner(file);

            boolean approved;
            if (textFile) approved = checkIfValidFile(inputTest);
            else approved = checkIfValidMovingAI(inputTest);

            if (approved) { //BUILD SELECTED GRID

                if (running) flipRunning();
                buttonLoad.setMapName(file.getName());

                Scanner input = new Scanner(file);

                if (textFile) createMapFromTXT(input);
                else createMapFromMovingAI(input);

                input.close();
            }
        }
    }

    private boolean checkIfValidFile(Scanner input) {
        int gridW = 0, gridH = 0;

        //Test for grid size
        if (input.hasNextInt()) gridW = input.nextInt();
        if (input.hasNextInt()) gridH = input.nextInt();
        if (gridW < 2 || gridH < 2) {invalidMap("Invalid grid size"); input.close();return false;}

        //Test for number of lines
        ArrayList<String> lines = new ArrayList<String>();
        while(input.hasNext()) {
            lines.add(input.next());
        }
        if (!(lines.size() == gridH)) {invalidMap("Invalid line amount"); input.close();return false;}

        //Test for number of chars in each string
        //Test for exactly ONE instance of m, and of g
        int ms = 0, gs = 0;
        for (int i = 0; i < gridH; i++) {
            String line = lines.get(i);
            if (!(line.length() == gridW)) {invalidMap("Invalid line length"); input.close();return false;}
            for (int ii = 0; ii < gridW; ii++) {
                char c = line.charAt(ii);
                if (c == 'm') ms++;
                if (c == 'g') gs++;
            }
        }
        if (!(ms == 1) || !(gs == 1)) {invalidMap("Invalid character(s)"); input.close();return false;}

        //you win!
        {input.close();return true;}
    }

    private boolean checkIfValidMovingAI(Scanner input) {
        for (int i = 0; i < 3; i++) {
            String next = input.next();
            switch (i) {
                case 0: if (!"type".equals(next)) {invalidMap("Invalid File Format type");input.close();return false;
                } break;
                case 1: if (!"octile".equals(next)) {invalidMap("Invalid File Format octile");input.close();return false;} break;
                case 2: if (!"height".equals(next)) {invalidMap("Invalid File Format height");input.close();return false;} break;
            }
        }
        int gridW = 0, gridH = 0;
        if (input.hasNextInt()) gridH = input.nextInt(); if (!"width".equals(input.next())) {invalidMap("Invalid File Format width");input.close();return false;}
        if (input.hasNextInt()) gridW = input.nextInt(); if (!"map".equals(input.next())) {invalidMap("Invalid File Format map");input.close();return false;}
        if (gridW < 2 || gridH < 2 || gridW > (gridSize)/2 || gridH > (gridSize)/2)
            {invalidMap("Invalid Map Size");input.close();return false;}
        //Test for number of lines
        ArrayList<String> lines = new ArrayList<String>();
        while(input.hasNext()) {
            lines.add(input.next());
        }
        if (lines.size() < gridH) {invalidMap("Invalid line amount"); input.close();return false;}

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() < gridW) {invalidMap("Invalid line length"); input.close();return false;}
        }

        //you win!
        {input.close();return true;}
    }

    private void createMapFromTXT(Scanner input) {
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
                        manX = ii; manXPrev = manX;
                        manY = i; manYPrev = manY;
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
    }

    private void createMapFromMovingAI(Scanner input) {
        for (int i = 0; i < 3; i++) input.next();
        gridH = input.nextInt(); input.next();
        gridW = input.nextInt(); input.next();
        initGrid(gridW, gridH);
        for (int i = 0; i < gridH; i++) {
            String line = input.next();
            for (int ii = 0; ii < gridW; ii++) {
                char c = line.charAt(ii);
                if (c == '@' || c == 'O' || c == 'T') {
                    board[ii][i] = SquareID.Obstacle;
                }
            }
        }
        boolean mPlaced = false, gPlaced = false;
        for (int i = 0; i < gridH; i++) {
            if (mPlaced) break;
            for (int ii = 0; ii < gridW; ii++) {
                if (board[ii][i] == SquareID.Empty){
                    manX = ii; manXPrev = manX;
                    manY = i; manYPrev = manY;
                    board[manX][manY] = SquareID.Man;
                    manOrigX = manX;
                    manOrigY = manY;
                    mPlaced = true;
                    break;
                }
            }
        }
        for (int i = gridH-1; i > -1; i--) {
            if (gPlaced) break;
            for (int ii = gridW-1; ii > -1; ii--) {
                if (board[ii][i] == SquareID.Empty){
                    goalX = ii;
                    goalY = i;
                    board[goalX][goalY] = SquareID.Goal;
                    goalOrigX = goalX;
                    goalOrigY = goalY;
                    gPlaced = true;
                    break;
                }
            }
        }
    }

    public void newGridSize(int gridW, int gridH) {
        initGrid(gridW,gridH);
        manX = 0; manXPrev = manX;
        manY = 0; manYPrev = manY;
        manOrigX = manX;
        manOrigY = manY;
        board[manX][manY] = SquareID.Man;
        goalX = gridW-1;
        goalY = gridH-1;
        goalOrigX = goalX;
        goalOrigY = goalY;
        board[goalX][goalY] = SquareID.Goal;
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
        if (board[goalX][goalY] == SquareID.Goal) {
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
                if (dir % 2 == 1) {
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
                    if (!(manY == (gridH - 1) * o) && !(board[manX][manY - Vmove] == SquareID.Obstacle)) i++;
                } else {
                    if (Hmove == -1) o = 0;
                    if (!(manX == (gridW - 1) * o) && !(board[manX + Hmove][manY] == SquareID.Obstacle)) i++;
                }
                ii++;
            }
            if (ii < 100) moveMan(dir);
        }
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
        //ANIMATE
        float diff = 0.85f;
        if (moveTime == 1) diff = 0.95f;
        if (!(Hmove == 0)) manWMult = 2-diff;
        else manWMult = diff;
    }

    private void drawManExt(Graphics g, int x, int y, int w, int h, int alpha) {
        g.setColor(new Color(manCol.getRed(),manCol.getGreen(),manCol.getBlue(),alpha));
        g.fillOval(x,y,w,h);
        float percSize = 0.7f;
        g.setColor(new Color(manColFg.getRed(),manColFg.getGreen(),manColFg.getBlue(),alpha));
        g.fillOval(x+(w-(int)((float)w*percSize))/2+(int)((float)h*0.03),
                y+h-(int)((float)h*percSize)-(int)((float)h*0.05),
                (int)((float)w*percSize),(int)((float)h*percSize));
        //Eyes
            int xD = 0;
            int yD = 0;
            if (!(manX == manXPrev)) xD = manXPrev-manX;
            if (!(manY == manYPrev)) yD = manYPrev-manY;
            int eyesX, eyesY;
            if (!(moveTime == 1)) {
                eyesX = x + w / 2 +
                        (xD * (int) (-manDis * 0.45)) - xD * (int) ((float) h * 0.12);
                eyesY = y + h / 2 - (int) ((float) h * 0.05) +
                        (yD * (int) (-manDis * 0.4)) - yD * (int) ((float) h * 0.06);
            } else {
                eyesX = x + w / 2;
                eyesY = y + h / 2 - (int) ((float) h * 0.05);
            }
            int gap = (int)((float)w*0.4)-(int)((manDis/(float)sqSize)*(int)((float)w*0.14));
            int eyeH = (int) ((float) h * 0.42);
            int eyeW = (int) ((float) w * 0.15);
            boolean close = (manHatDist < 4);
            g.setColor(new Color(22, 25, 28, alpha));
            if (!mapEHoldingMan && (board[goalX][goalY] == SquareID.Goal)) { //NOT HOLDING MAN && NOT FINISHED
                g.fillOval(eyesX - gap / 2 - eyeW / 2, eyesY - eyeH / 2, eyeW, eyeH);
                g.fillOval(eyesX + gap / 2 - eyeW / 2, eyesY - eyeH / 2, eyeW, eyeH);
                if (close) {
                    int eyeWW = (int) (eyeW * 0.8);
                    int eyeHH = (int) (eyeH * 0.8);
                    g.setColor(new Color(239, 233, 246, alpha));
                    g.fillOval(eyesX - gap / 2 - eyeWW / 2, eyesY - eyeHH / 2, eyeWW, eyeHH);
                    g.fillOval(eyesX + gap / 2 - eyeWW / 2, eyesY - eyeHH / 2, eyeWW, eyeHH);
                }
            } else { //IF HOLDING OR FINISHED
                int squintW = (int)((float)h*0.3);
                int squintH = (int)((float)h*0.1);
                int gp = (int)(gap*1.3);
                int aW = 10, aH = 3;
                g.fillRoundRect(x+w/2-gp/2-squintW/2, y+h/2-(int)((float)h*0.1),
                        squintW, squintH, aW,aH);
                g.fillRoundRect(x+w/2+gp/2-squintW/2, y+h/2-(int)((float)h*0.1),
                        squintW, squintH, aW,aH);
                eyesX = x + w / 2;
                eyesY = y + h / 2 - (int) ((float) h * 0.11);
            }
            if (close) {
                eyeW *= 1.7;
                eyeH /= 3.5;
                gap /= 1.1;
                eyesY += eyeH/3;
                g.setColor(new Color(255, 149, 194, alpha));
                g.fillOval(eyesX - gap - eyeW / 2, eyesY + eyeH, eyeW, eyeH);
                g.fillOval(eyesX + gap - eyeW / 2, eyesY + eyeH, eyeW, eyeH);
            }
    }

    private void drawGoalExt(Graphics g, int x, int y, float wMult, int alpha) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        float a = alpha/255; //draw half transparent
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,a);
        g2.setComposite(ac);

        int burgW = imgBurger.getWidth(), burgH = imgBurger.getHeight();
        float ratio = (float) burgW / (float) burgH;
        burgH = (int) (sqSize * 0.8 * (2f-wMult));
        burgW = (int) ((sqSize * 0.8) * ratio * wMult);
        g2.drawImage(imgBurger, x-burgW/2, y, burgW, burgH, null);

        ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1);
        g2.setComposite(ac);
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
        int def = 15;
        int larger = gridW;
        if (gridH > gridW) larger = gridH;
        float mult;
        if (larger < def) mult = approach(100,300,def-larger,1,15);
        else mult = approach(100,30,Math.abs(def-larger),1,85);
        mult /= 100;

        float rand = r.nextInt(2) +4;
        float c = (rand-1)/2;
        float rad = (20+r.nextInt(5))*mult;
        float vsp = (-1.2f-r.nextFloat()*1.2f)*mult;
        for (int i = 0; i < rand; i++) {
            float ihsp = ((int) Math.signum(i-c)+(i-c)*0.3f);
            float range = 0.8f;
            ihsp = (2.2f*ihsp+r.nextFloat()*range-range/2+0.5f)*mult;
            ptManager.addParticle(new Particle(x,y,ihsp,ptManager,rad,vsp));
        }
    }

    public void resetBoard() {
        if (running) flipRunning();
        //buttonBar.resetBar();
        //moveTime = origMoveTime;
        moveID = 0;
        board[manX][manY] = SquareID.Empty;
        board[goalX][goalY] = SquareID.Empty;
        manX = manOrigX; manXPrev = manX;
        manY = manOrigY; manYPrev = manY;
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

        manHatDist = 100;
    }

    public void flipRunning() {
        running = !running;
        manDis = 0;
        manWMult = 1;
        timer = -1;
        buttonStart.swapStartImage();
    }

    public void flipEditingMap() {
        editingMap = !editingMap;
        if (editingMap) {
            bgCol = new Color(44,45,48); //Color.darkGray;
            program.setBgCol(bgCol);
            lineCol = new Color(184,191,202); //Color.lightGray;
            lightLineCol = new Color(136,139,147);
            windowCol = new Color(54,60,66);
            hoverCol = new Color(31,37,41);
            clickCol = new Color(14,21,26);
            sliderCol = new Color(66,156,248);
            sliderClickCol = new Color(25,122,254);
        } else {
            setColorsDefault();
        }
        //Button Image Colors
        bttManager.setAllTint();
    }

    private void setColorsDefault() {
        bgCol = new Color(237,239,241);
        program.setBgCol(bgCol);
        lineCol = new Color(22,23,28);
        lightLineCol = new Color(115,119,124);
        windowCol = Color.white;
        hoverCol = new Color(220,224,232);
        clickCol = new Color(191,198,215);
        sliderCol = new Color(89,242,112);;
        sliderClickCol = new Color(22,233,67);;
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

    public void setButtonStart(ButtonCircle buttonStart) {
        this.buttonStart = buttonStart;
    }

    public void setButtonLoad(ButtonText buttonLoad) {
        this.buttonLoad = buttonLoad;
    }

    public void setmanDis(float manDis) { this.manDis = manDis; }

    public void setManWMult(float manWMult) { this.manWMult = manWMult; }

    public void setMoveTime(int moveTime) {
        this.moveTime = moveTime;
    }

    public GUI getGUI(){
        return this.gui;
    }

    public BttManager getBttManager(){
        return this.bttManager;
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

    public int getGridActualH() { return sqSize*gridH; }

    public boolean getifRunning() { return running; }

    public boolean getifEditingMap() { return editingMap; }

    public Color getLineCol() { return lineCol; }

    public Color getBgCol() { return bgCol; }

    public int getMoveID() { return moveID; }

    public int getNumMoves() { return numMoves; }

    public int getNumPresets() { return numPresets; }

    public String getPresetName(int preset) { return presetName[preset]; }

    public int getPresetOn() { return presetOn; }

    public int approach(int lower, int upper, float progress, float pLower, float pUpper) {
        float percent = (progress-pLower)/(pUpper-pLower);
        int var = (int) (lower+(float)(upper-lower)*percent);
        return var;
    }

    private void invalidMap(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}