package kplevelviewer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Chris
 */
public class DrawPanel extends JPanel
    {
        private BufferedImage[] layers;
        private int imWidth, imHeight;
    
        //Inner panel to draw stuff
        public DrawPanel(BufferedImage ... layers)
        {
            this.layers = layers;
            imWidth = 0x1000;
            imHeight = 0x1000;
        }
        
        public DrawPanel(int frameWidth, int frameHeight, BufferedImage ... layers)
        {
            this.layers = layers;
            this.imWidth = frameWidth;
            this.imHeight = frameHeight;
        }
        
        public int getImWidth() {
            return imWidth;
        }

        public int getImHeight() {
            return imHeight;
        }

        @Override
        public void paintComponent(Graphics g)
        {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, imWidth, imHeight);

            for (BufferedImage layer : layers)
            {
                if (layer != null)
                {
                    g.drawImage(layer, 0, 0, null);
                }
            }
        }
    }
