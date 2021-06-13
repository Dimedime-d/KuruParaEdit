package kpdatamanipulator.ops.tileget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    Spits out a .bin file that can be inserted via ARMIPS!!
*/
public class TableWriter extends RandomAccessFile {
    
    private int fileSize;
    private static int valueCount = 0;
    
    public TableWriter(File file, String mode, int size) throws FileNotFoundException {
        super(file, mode);
        fileSize = size;
    }

    public void writeOffsetsToFile(ArrayList<Integer> values, ArrayList<Integer> offsets) {
        try {
            this.setLength(fileSize);
            //Check if values & offsets have different sizes
            if (values.size() != offsets.size()) {
                System.out.println("Value/Offset Lists have different sizes");
            } else {
                for (int i = 0; i < values.size(); i++) //Time to write each value!!
                {
                    seek(offsets.get(i));
                    if (values.get(i) != 0) {
                        writeByte(values.get(i));
                        System.out.println("Wrote "+(values.get(i)) + " at offset " + Integer.toHexString(offsets.get(i)));
                        valueCount++;
                    }
                }
            }
            
            System.out.println(valueCount + " values written");
        } catch (IOException ex) {
            Logger.getLogger(TableWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
