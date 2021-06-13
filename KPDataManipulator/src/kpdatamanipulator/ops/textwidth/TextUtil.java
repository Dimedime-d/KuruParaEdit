/*
    Helper with creating text width map
*/
package kpdatamanipulator.ops.textwidth;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class TextUtil 
{
    //1st 2 hex digits are alpha
    public static final int FILLED_COLOR =   0xFFF8_F8F8;
    public static final int FILLED_COLOR_2 = 0x0078_7878;
    public static final int BG_COLOR =       0x0;
    public static final int TEXT_BG_COLOR =  0xFF20_98A0;
    
    public static void parseFiles(File[] files, HashMap<Character, BufferedImage> IMap,
            HashMap<Character, Integer> OMap, boolean special, TWControlPanel.TextType type)
    {
        for (File file : files)
        {   //Exclude other not png files
            if (file.getName().endsWith(".png"))
            {   
                try 
                {
                //Operate on file within these brackets
                BufferedImage currImage = ImageIO.read(file);
                int width = currImage.getWidth();
                int height = currImage.getHeight();
                
                //Get character from filename
                char c;
                if (special)
                {
                    String fileName = file.getName().substring(0, file.getName().length() - 4);
                    c = getSpecialChar(fileName);
                } else {
                    c = file.getName().charAt(0);
                }
                
                int yOffset = determineYOffset(height, c, type);
                OMap.put(c, yOffset);
                
                //Assume each glyph's height to be 9 (fix zero-width) FALSE - parantheses are height 11.
                BufferedImage finalTxtImg;
                if (width > 0 && height > 0)
                    finalTxtImg = currImage.getSubimage(0,0,width,height);
                else {
                    finalTxtImg = currImage.getSubimage(0,0,1,1);
                }
                
                //mutate images to get rid of blue background
                //finalTxtImg = setTransparentColor(finalTxtImg, TEXT_BG_COLOR);
                
                //Finally, insert data into maps
                IMap.put(c, finalTxtImg);
                } catch (IOException ex) {
                    Logger.getLogger(TextUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static int determineYOffset(int height, char c, TWControlPanel.TextType type)
    {
        switch (type)
        {
            case ALTFONT :
                if (height >= 9)
                return -5;
                else
                {
                    switch (c) 
                    {
                        case ',' : return 2;
                        case '.' : return 2;
                        case ':': case ';': return -3;
                        case 't' : return -4;
                        case '\'': case '\"' : return -6;
                        case '-': return -1;
                        default : return -2;
                    }
                }
            case ASCII:
                if (height >= 8)
                return -4;
                    switch (c) 
                    {
                        case ',' : return 1;
                        case '.' : return 2;
                        case ':': case ';': return -3;
                        case 't' : return -4;
                        case '\'': case '\"' : return -6;
                        case '-': return -1;
                        case '/': return -3;
                        default : return -2;
                    }
            default: return -2;
        }
        
    }
    
    private static char getSpecialChar(String fileName)
    {
        switch (fileName) //Strings in switch statements :D
        {
            case "asterisk" : return '*';
            case "colon" : return ':';
            case "degreesign" : return '°';
            case "doublequote" : return '\"';
            case "greaterthan" : return '>';
            case "lessthan" : return '<';
            case "period" : return '.';
            case "questionmark" : return '?';
            case "space" : return ' ';
            case "forwardslash" : return '/';
            case "verticalline" : return '|';
            default : return 'ƍ'; //should be unreachable
        }
    }
}
