package com.program.main.buttons;

import java.awt.*;
import java.util.LinkedList;

public class BttManager {
    LinkedList<ObjButton> buttons = new LinkedList<ObjButton>();

    public void tick() {
        for (int i = 0; i < buttons.size(); i++) {
            ObjButton ii = buttons.get(i);
            ii.tick();
        }
    }

    public void render(Graphics g) {
        for (int i = 0; i < buttons.size(); i++) {
            ObjButton ii = buttons.get(i);
            ii.render(g);
        }
    }

    public void addButton(ObjButton button) {
        buttons.add(button);
    }

    public void removeButton(ObjButton button) {
        buttons.remove(button);
    }
}
