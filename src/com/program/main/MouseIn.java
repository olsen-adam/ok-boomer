package com.program.main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseIn implements MouseListener, MouseMotionListener {
        int mX = 0;
        int mY = 0;
        boolean mClicked, mPressed, mReleased, mDragged;

    public MouseIn() {

    }

    public void tick() {
        //if (mClicked) System.out.println("Clicked");
        mClicked = false;
        mPressed = false;
        mReleased = false;
        mDragged = false;
    }
    public void render(Graphics g) {
        /*g.setColor(Color.black);
        g.drawString("MX: " + mX,5,17);
        g.drawString("MY: " + mY,5,35);*/
        //g.drawString(String.valueOf(mClicked),5,48);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mClicked = true;
    }
    @Override
    public void mousePressed(MouseEvent e) {
        mPressed = true;
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        mReleased = true;
    }
    @Override
    public void mouseEntered(MouseEvent arg0) {
    }
    @Override
    public void mouseExited(MouseEvent arg0) {
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        mX = e.getX();
        mY = e.getY();
        mDragged = true;
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        mX = e.getX();
        mY = e.getY();
    }

    public boolean getClicked() {
        return this.mClicked;
    }
    public boolean getPressed() {
        return this.mPressed;
    }
    public boolean getReleased() {
        return this.mReleased;
    }
    public boolean getDragged() {
        return this.mDragged;
    }
    public int getMouseX() {
        return this.mX;
    }
    public int getMouseY() {
        return this.mY;
    }
}
