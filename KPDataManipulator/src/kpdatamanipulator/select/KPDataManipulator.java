//Christopher Ung
//3/24/18
//JDK Version 1.8.0
//Goal: Given an 8-bit integer value, read corr. values in Kururin Paradise ROM

package kpdatamanipulator.select;

import kpdatamanipulator.ops.imgobjhex.ImgObjHexGenFrame;
import kpdatamanipulator.ops.textwidth.TextWidthFrame;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import kpdatamanipulator.ops.*;
import javax.swing.JOptionPane;
import kpdatamanipulator.ops.tileget.TileGetterFrame;

public class KPDataManipulator{

    private KPRomAccess roms;
    
    public KPDataManipulator() throws FileNotFoundException, IOException {
        createRomAccess();
        String input = JOptionPane.showInputDialog(null, "Please select a num from the following:"
                + "\n1: Angle Parser"
                + "\n2: Map Decompressor"
                + "\n3: Table Generator"
                + "\n4: Script Offset Finder (+Tileset transfer finder)"
                + "\n5: Complete Tile Getter (Width calculator and table included) [Complex beyond repair]"
                + "\n6: Overall Text Width Calculator"
                + "\n\t7: Obj Data Overwriter"
                + "\n8: ASM routine finder"
                + "\n9: Byte Frequency Counter"
                + "\n10: Image to OBJ hex Generator (not needed)");
        int num = 0;
        try {
          num = Integer.parseInt(input);
        } catch (NumberFormatException e) {
          e.printStackTrace();
        }
        
        //default = japanese
        RandomAccessFile romAccess = roms.getRomAccess();
        
        switch(num) {
            case 1: 
            new AngleFrame(romAccess);
            break;
            case 2:
            new MapFrame(romAccess);
            break;
            case 3:
            new TableFrame(roms);
            break;
            case 4:
            new ScriptOffsetFrame(roms);
            break;
            case 5:
            new TileGetterFrame(roms);
            break;
            case 6:
            new TextWidthFrame();
            break;
            case 7:
            new BinaryOverwriterFrame();
            break;
            case 8:
            new ASMFinderFrame();
            break;
            case 9:
            new ByteFrequencyFrame();
            break;
            case 10:
            new ImgObjHexGenFrame();
            break;
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        new KPDataManipulator();
        //frame.decompress();
    }
    
    public void createRomAccess() {
        try {
            roms = new KPRomAccess(); //Allows seeking file at offset
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Error loading file");
        }
    }
}
