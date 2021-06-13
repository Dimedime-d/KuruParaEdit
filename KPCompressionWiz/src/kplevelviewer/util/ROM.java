package kplevelviewer.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ROM 
{
    public static RandomAccessFile reader;
    public static final int JPN = 0, CHN = 1;
    public static int lang = 0;
    
    public static void createRomAccess(String path)
    {
        File romFile = new File(path);
        try {
            reader = new RandomAccessFile(romFile, "r");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ROM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void changeRomAccessToChinese()
    {
        File romFile = new File("res/KURUPARA_A9QC00.gba");
        try {
            reader = new RandomAccessFile(romFile, "r");
            lang = CHN;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ROM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void changeRomAccessToJapanese()
    {
        File romFile = new File("res/KP - Use in Java.gba");
        try {
            reader = new RandomAccessFile(romFile, "r");
            lang = JPN;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ROM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
