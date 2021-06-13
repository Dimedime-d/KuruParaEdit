package kpdatamanipulator.ops.tileget;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnglishTileHandler { //Moving all english stuff to over here, plus the file writer
    
    private int[] PAL_COLORS = new int[0x10];
    private ArrayList<BufferedImage> engChars = new ArrayList<>();
    private RandomAccessFile romAccess;
    
    public EnglishTileHandler(int[] colors, ArrayList<BufferedImage> engChars, RandomAccessFile ra) {
        PAL_COLORS = colors;
        this.engChars = engChars;
        romAccess = ra;
    }
    
    public ArrayList<Integer> calcWidths(ArrayList<BufferedImage> chars, int width) {
        
        int charCount = 0;
        ArrayList<Integer> widths = new ArrayList<>();
        
        for (BufferedImage image : chars) 
        {
            //Check how many columns are white!
            int filledColor = PAL_COLORS[0xF]; //true = has alpha
            int startX = -1, endX = 0;
            for (int x = 0; x < image.getWidth(); x++) {
                //Each column
                for (int y = 0; y < image.getHeight(); y++) {
                    //Each row
                    if (image.getRGB(x,y) == filledColor)
                        endX = x + 1; //The latest filled column gets stored into endX (plus one empty col at the end)
                    if (startX == -1)
                        startX = x;
                    //the First filled column's x is marked
                }
            }
            
            if (startX == -1) startX = 0;
            
            int totalCharWidth;
            if ((endX - startX) > 0)
                totalCharWidth = (endX - startX) + 1; //Only valid for chars with
            //width to begin with!
            else
                totalCharWidth = 0;
            
            //Print the charWidth, with custom formatting
            DecimalFormat twoPlaces = new DecimalFormat("00");
            //System.out.print(twoPlaces.format(totalCharWidth) + " ");
            
            //if (((charCount+1) % (width/TileImg.TILE_WIDTH)) == 0) //New line corr. to lines in jframe
                //System.out.println("");
            
            charCount++;
            
            //Add width to collection
            widths.add(totalCharWidth);
            
        }
        
        //System.out.println("");
        
        return widths;
    }
    
    public ArrayList<Integer> listEngRelOffsets(int start, int end, boolean truncate) {   try {
        //Lists all the necessary offsets
        //for the English characters I want
        romAccess.seek(start); //The start
        int offsetCount = 0;
        
        ArrayList<Integer> checkMeForDupes = new ArrayList<>();
        
                while (romAccess.getFilePointer() < end) {

                    //Create relative offset
                    byte[] temp = new byte[4]; //stores individual bytes of rel offset
                    romAccess.readFully(temp);
                    int relOffset = toRelOffset(temp);

                    if (relOffset < 0 || relOffset > 0xFFFFFF) continue; //invalid ptr

                    int lastHalfWord = ((relOffset << 0x10)) >>> 0x10;

                    int manipulateMe = (lastHalfWord >>> 0x05) << 0x01;
                    if (truncate)
                        checkMeForDupes.add(manipulateMe);
                    else
                        checkMeForDupes.add(relOffset); //Extra condition to truncating the offset

                    //System.out.print(Integer.toHexString(manipulateMe) + " ");
                    //if ((offsetCount+1) % 4 == 0)
                        //System.out.println("");

                    offsetCount++;
                }

                //checkDuplicates(checkMeForDupes);
                
                //System.out.println("");
                return checkMeForDupes;
            
            } catch (IOException ex) {
                Logger.getLogger(TileGetterFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
    
        return null; //Should be unreachable
    }
    
    public int toRelOffset(byte[] bytes) {
        //flips bytes and concatenates them in that reversed order
        return ( bytes[0] & 0xFF |
                 ((bytes[1] << 8) & 0xFF00) |
                 ((bytes[2] << 16) & 0xFF0000) |
                 ((bytes[3] << 24) & 0xFF000000)
                 );
    }
    
    public void checkDuplicates(ArrayList<Integer> list) {
        Set<Integer> seenValues = new HashSet<>();
        ArrayList<Integer> dupeValues = new ArrayList<>();
        //Iterate through each int in list
        
        for (int a : list) {
            if (seenValues.contains(a))
                dupeValues.add(a);
            else
                seenValues.add(a);
        }
        
        System.out.println("Duplicate values:");
        for (int a : dupeValues) {
            System.out.println(Integer.toHexString(a));
        }
    }

}
