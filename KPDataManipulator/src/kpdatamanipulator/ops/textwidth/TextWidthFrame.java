/*
    Program that puts text onto a GBA display showing width/location
*/
package kpdatamanipulator.ops.textwidth;

import java.io.IOException;
import javax.swing.JFrame;

public class TextWidthFrame {
    private JFrame frame;
    
    public TextWidthFrame() throws IOException //constructor
    {
        frame = new JFrame("Text Width and Location");
        
        //Add top-level component, then set up
        frame.add(new TWPanel());

        setUp();
    }
    
    private void setUp()
    {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
