package kpdatamanipulator.ops;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class ByteFrequencyFrame 
{
    private int[] tallies = new int[256];
    
    public ByteFrequencyFrame() 
    {
        JOptionPane.showMessageDialog(null, 
                "This program will count the frequency of bytes in a given file.");
        File bin = pickFile(); //User picks file
        operateTally(bin); //Count!
    }
    
    public File pickFile() 
    {
        JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Pick a binary file!?");
            fc.setFileFilter(new FileFilter() 
                    { //Save Dialog shows ONLY excel files
                        @Override
                        public String getDescription()
                        {   return "Binary files (*.bin)";} //drop-down text

                        @Override
                        public boolean accept(File f) //choose whether to show file
                        {
                            if (f.isDirectory())
                                return true;
                            else
                            {
                                String filename = f.getName().toLowerCase();
                                return filename.endsWith(".bin");
                            }
                        }
                    }   );
            int returnVal = fc.showOpenDialog(null);
        
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                return fc.getSelectedFile();
            } else {
                System.exit(0);
            }
        return null;
    }
    
    public void operateTally(File file)
    {
        try (FileInputStream input = new FileInputStream(file)){
            int count = 0;
            while (count <= file.length())
            {
                int currentByte = (int) (input.read() & 0xFF);
                tallies[currentByte]++;
                //Array keeps tallies of each byte!
                
                count++;
            }
        } catch (IOException ex) {
            Logger.getLogger(ByteFrequencyFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Output
        for (int i = 0; i < tallies.length; i++)
        {
            String hexByte = Integer.toHexString(i);
            System.out.println(hexByte + " frequency: " + tallies[i]);
        }
    }
}
