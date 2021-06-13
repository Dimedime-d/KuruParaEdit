//Take an image of specified dimensions, break into 32x16 chunks, & write obj data
//[[NOT NECESSARY since GBAGE already does
//IN ORDER of 4-bit groups: 21 43 65 87 corr. to pixels left to right (1/12/19: WIP)

//Outermost class dealing with the JFrame
package kpdatamanipulator.ops.imgobjhex;

import javax.swing.JFrame;

public class ImgObjHexGenFrame 
{
    private JFrame frame;
    
    public ImgObjHexGenFrame() //Constructor
    {
        frame = new JFrame("Image Object Hex");
        frame.add(new IOHPanel());
        setUp();
    }
    
    private void setUp()
    {
        frame.setSize(900,189);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
