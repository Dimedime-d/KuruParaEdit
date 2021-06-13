//Next-level container holding control/image panels
package kpdatamanipulator.ops.imgobjhex;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class IOHPanel extends JPanel
{
    /**
     *
     */
    private static final long serialVersionUID = 8858670279768266749L;
    public static final int WIDTH1 = 240, HEIGHT1 = 160; // GBA screen size
    public static final Color PANEL_BG = Color.BLACK;
    
    private ImagePanel imgPanel;
    private IOHControlPanel ctrlPanel;
    
    private BufferedImage image = new BufferedImage(WIDTH1, HEIGHT1, BufferedImage.TYPE_INT_RGB); 
    //imgPanel will draw image
    
    public IOHPanel() //Constructor
    {
        //Add components
        setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        
        //Panel to display text
        imgPanel = new ImagePanel();
        
        //Panel for text field + others
        ctrlPanel = new IOHControlPanel(image, imgPanel);
        
        add(imgPanel);
        add(ctrlPanel);
    }
    
    public class ImagePanel extends JPanel //Show image with text
    {
        /**
         *
         */
        private static final long serialVersionUID = 2313687251403833110L;

        public ImagePanel() // Constructor
        {
            super();
            setPreferredSize(new Dimension(WIDTH1+1, HEIGHT1));
            setBorder(BorderFactory.createMatteBorder(0,0,0,1, Color.RED));
        }
        
        @Override
        public void paintComponent(Graphics g)
        {
            g.setColor(PANEL_BG);
            g.fillRect(0,0,WIDTH1,HEIGHT1);

            g.drawImage(image, 0, 0, null);
        }
    }
}
