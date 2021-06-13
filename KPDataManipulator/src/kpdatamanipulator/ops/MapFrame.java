package kpdatamanipulator.ops;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MapFrame extends JFrame {
    
    /**
     *
     */
    private static final long serialVersionUID = -2458573555706809777L;

    private RandomAccessFile romAccess;
    
    private int neo_land_4 = 0x0054D1A5;
    
    public MapFrame(RandomAccessFile ra) {
        romAccess = ra;
        
        decompress();
        
        JOptionPane.showMessageDialog(null, "No GUI Yet. \nEnjoy the decompressed"
                + " map data of Neo Land 4");
    }
    
    //Practice Decompression Routine
    public void decompress() {
        try {
            //1st 4 bytes = size
            romAccess.seek(neo_land_4);
            byte[] tempSizeBytes = new byte[4];
            int[] sizeBytes = new int[4];
            romAccess.readFully(tempSizeBytes);
            for (int i = 0; i < sizeBytes.length; i++) {
                sizeBytes[i] = makeByteUnsignedInt(tempSizeBytes[i]);
            }
            int bytesLeft = sizeBytes[0]*sizeBytes[2]*2; //each tile takes up 2 bytes
            //Finalize the final size of map
            
            int[] mapRAMBytes = new int[bytesLeft+4];
            //1st 4 bytes are size
            for (int i = 0; i < sizeBytes.length; i++) {
                mapRAMBytes[i] = sizeBytes[i];
            }
            int currentPointer = 4, copyLimit = 0, copyPos = 0;
            
            while (bytesLeft > 0) {
                byte nextByte = romAccess.readByte(); 
                //Scrolls to next byte automatically
                if (nextByte < 0) { //Copying back
                    int length = ((nextByte & 0x7F) + 3);
                    bytesLeft -= length;

                    copyLimit = currentPointer + length;

                    int bytesBack = makeByteUnsignedInt(romAccess.readByte());
                    copyPos = currentPointer - bytesBack;

                    while (currentPointer < copyLimit) {
                        mapRAMBytes[currentPointer++] = mapRAMBytes[copyPos++];
                    }
                } else { //New Data
                    int length = ++nextByte;
                    bytesLeft -= length;

                    copyLimit = currentPointer + length;

                    while (currentPointer < copyLimit) {
                    int newTileID = romAccess.readByte();
                    mapRAMBytes[currentPointer++] = newTileID;
                    }
                }
                //Poss. print during the loop?
            }
            
            //Print Test
            for (int i = 0; i < mapRAMBytes.length; i++) {
                System.out.print(twoDigitHex((mapRAMBytes[i]&0xFF)) + " ");
                if ((i+1) % 0x10 == 0 && i != 0) {
                    System.out.print("\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AngleFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int makeByteUnsignedInt(byte b) {
        return ((int) b) & 0xFF; //keeps only 8 least significant bits from int (32-bit)
    }
    
    public String twoDigitHex(int i) {
        if (i < 0x10) {
            return ("0" + Integer.toHexString(i));
        } else {
            return Integer.toHexString(i);
        }
    }
}
