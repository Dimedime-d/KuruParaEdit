package kpdatamanipulator.ops;

//User picks a file, data gets overwritten

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;


public class BinaryOverwriterFrame {
    
    public BinaryOverwriterFrame() 
    {
        JOptionPane.showMessageDialog(null, 
                "This program will zero out the x-displacement of a given binary file.");
        File bin = pickFile();
        filterObjdX(bin);
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
    
    public void filterObjdX(File file) //Overwrite the necessary data
    {
        try (RandomAccessFile binAccess = new RandomAccessFile(file, "rw")){
            //Continue until end of file
            binAccess.seek(0); //Read beginning of file
            while (binAccess.getFilePointer() < binAccess.length())
            {
                // 01 60 08 08 --> Next byte gets zeroed out
                if (binAccess.readByte() == 0x01)
                    if (binAccess.readByte() == 0x60)
                        if (binAccess.readByte() == 0x08)
                            if (binAccess.readByte() == 0x08)
                                binAccess.writeByte(0x00);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BinaryOverwriterFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BinaryOverwriterFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
