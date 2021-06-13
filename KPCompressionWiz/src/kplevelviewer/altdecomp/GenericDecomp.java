package kplevelviewer.altdecomp;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import kplevelviewer.DrawPanel;
import kplevelviewer.OffsetGroup;
import kplevelviewer.util.SelectionHelper;
import kplevelviewer.OffsetGroup.DecompActor;

public class GenericDecomp implements ActionListener{
    
    private static JButton btnOK;
    private final JComboBox<String> cb;
    private final DecompActor[] actors;
    private JFrame frame;
    
    public GenericDecomp(DecompActor[] actors)
    {
        this.actors = actors;
        cb = SelectionHelper.enumsToComboBox(actors);
        ask();
    }
    
    private void ask()
    {
        btnOK = new JButton("OK");
        btnOK.addActionListener(this);
        SelectionHelper.askForSelection(cb, btnOK);
    }
    
    private void decompScreen(int index) throws IOException
    {
        DecompActor da = actors[index];
        OffsetGroup.swapROM(index, da);
        //Note: only 1 layer supported so far
        BufferedImage[][] mapImages = da.createMapImages();
        BufferedImage fullMap = packImages(mapImages);
        DrawPanel dp = da.createDrawPanel(fullMap);
        displayImage(dp);
        takeScreenshot(da);
    }
    
    private BufferedImage packImages(BufferedImage[][] images)
    {
        BufferedImage im = null;
        if (images != null)
        {
            im = new BufferedImage(images[0].length*8, images.length*8, BufferedImage.TYPE_INT_ARGB);
            Graphics gMap1 = im.createGraphics();
            for (int y = 0; y < images.length; y++)
            {
                for (int x = 0; x < images[0].length; x++)
                {
                    gMap1.drawImage(images[y][x], x*8, y*8, null);
                }
            }
        }
        return im;
    }
    
    private void displayImage(DrawPanel dp) throws IOException
    {
        frame = new JFrame("Test");
        
        //Add stuff here
        int frameWidth = dp.getImWidth();
        int frameHeight = dp.getImHeight();
        frame.getContentPane().add(dp); //Implicit drawing order here
        
        frame.setSize(new Dimension(frameWidth+8, frameHeight-49));
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        frame.setVisible(true);
    }
    
    private void takeScreenshot(DecompActor da) throws IOException
    {
        BufferedImage sshot = getScreenShot(frame.getContentPane());

        String filePath = da.getFilePath();
        ImageIO.write(sshot, "png", new File(filePath));
        JOptionPane.showMessageDialog(null, "Wrote screenshot!");
    }
    
    private BufferedImage getScreenShot(Component component) {
        BufferedImage image = new BufferedImage(
          component.getWidth(),
          component.getHeight(),
          BufferedImage.TYPE_INT_RGB
          );
        // call the Component's paint method, using
        // the Graphics object of the image.
        component.paint( image.getGraphics() ); // alternately use .printAll(..)
        return image;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        if (ae.getSource().equals(btnOK))
        {
            try {
                decompScreen(cb.getSelectedIndex());
            } catch (IOException ex) {
                Logger.getLogger(GenericDecomp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
