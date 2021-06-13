/*
 * Handles the 2 different kp ROMs
*/

package kpdatamanipulator.select;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import javax.swing.JOptionPane;

public class KPRomAccess 
{
    public static final int JPN = 0;
    public static final int CHN = 1;
    
    private String japRomString = "res/KP - Use in Java.gba";
    private String chnRomString = "res/KURUPARA_A9QC00.gba";
    
    private RandomAccessFile romAccessJapanese;
    private RandomAccessFile romAccessChinese;
            
    public KPRomAccess() throws FileNotFoundException
    {
        //RandomAccessFile constructor reads filenames as strings to work in compiled .jars... (doesn't fucking work)
        romAccessJapanese = new RandomAccessFile(japRomString, "r");
        romAccessChinese = new RandomAccessFile(chnRomString, "r");
    }
    
    public RandomAccessFile getRomAccess()
    {
        return romAccessJapanese;
    }
    
    //Clone instance of romAccessJap
    public RandomAccessFile getClonedRomAccess() throws FileNotFoundException
    {
        return new RandomAccessFile(japRomString, "r");
    }
    
    public RandomAccessFile getRomAccessChinese()
    {
        return romAccessChinese;
    }
    
    public RandomAccessFile getClonedRomAccessChinese() throws FileNotFoundException
    {
        return new RandomAccessFile(chnRomString, "r");
    }
    
    //Contain JOptionPane here to pick japanese/chinese
    public static int pickRomLanguage()
    {
        return 
            JOptionPane.showOptionDialog(null,
            "Japanese or Chinese?",
            "Choose an option",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"Japanese","Chinese"},
            "Japanese");
    }
}
