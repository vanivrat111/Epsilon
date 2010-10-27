/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package epsilon.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 *
 * @author Marius
 */
public abstract class MenuPage {

    protected String[] items;
    protected int selected;
    protected String title;

    public MenuPage(String[] pageItems, String pageTitle) {

        items = pageItems;
        selected = 0;
        title = pageTitle;
        
    }

    public void drawPage(Graphics2D g, Font f) {

        int y = 0;
        
        g.setFont(f.deriveFont(72f));
        g.setColor(Color.RED);

        FontMetrics fm = g.getFontMetrics();
        y += 50 + fm.getHeight()/2;

        g.drawString(title, 400-(fm.stringWidth(title)/2), y);
        y += fm.getHeight()/2;

        g.setFont(f);
        g.setColor(Color.GREEN);
        y += 30;

        for (int i=0;i<items.length;i++) {

            if (i == selected) {
                g.setFont(f.deriveFont(Font.BOLD,60f));
            }
            
            fm = g.getFontMetrics();

            y += 15 + fm.getHeight()/2;

            g.drawString(items[i], 400-(fm.stringWidth(items[i])/2), y);
            
            y += fm.getHeight()/2;
            
            g.setFont(f);
        }
        
    }

    public void selectNext() {
        if (selected < items.length - 1) {
            selected++;
        }
    }

    public void selectPrevious() {
        if (selected > 0) {
            selected--;
        }
    }

    public abstract void useSelected();

    public void reset() {
        selected = 0;
    }

}
