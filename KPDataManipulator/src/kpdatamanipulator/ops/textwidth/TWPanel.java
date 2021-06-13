//Top-level panel containing image and control panels

package kpdatamanipulator.ops.textwidth;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class TWPanel extends JPanel
{
    public static final int WIDTH1 = 240, HEIGHT1 = 160;
    
    private ImagePanel imgPanel;
    private TWControlPanel ctrlPanel;
    
    private BufferedImage txtImage = new BufferedImage(WIDTH1, HEIGHT1, BufferedImage.TYPE_INT_RGB); 
    //Overlay text on background
    
    public TWPanel() throws IOException
    {
        //Add components
        setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        
        //Panel to display text
        imgPanel = new ImagePanel();
        
        //Panel for text field + others
        ctrlPanel = new TWControlPanel(txtImage, imgPanel);
        
        add(imgPanel);
        add(ctrlPanel);
    }
    
    public class ImagePanel extends JPanel //Show image with text
    {
        public ImagePanel() //Constructor
        {
            super();
            setPreferredSize(new Dimension(WIDTH1+10, HEIGHT1));
            setBorder(BorderFactory.createMatteBorder(0,0,0,10, Color.RED));
        }
        
        @Override
        public void paintComponent(Graphics g)
        {
            g.setColor(new Color(TextUtil.BG_COLOR));
            g.fillRect(0,0,WIDTH1,HEIGHT1);

            g.drawImage(txtImage, 0, 0, null);
        }
    }
}
