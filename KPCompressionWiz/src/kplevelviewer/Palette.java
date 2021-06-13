package kplevelviewer;

import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kplevelviewer.util.ROM;

public class Palette 
{   
    public static final int SIZE = 0x200;
    public static final int COLORS_PER_ROW = 0x10;
    //Create a 4BPP Palette from an int[]
    //Colors are stored in an array to be retrieved
    
    private final int RED_MASK   = 0b0000_0000_0001_1111;
    private final int GREEN_MASK = 0b0000_0011_1110_0000;
    private final int BLUE_MASK  = 0b0111_1100_0000_0000;
    
    private Color[] colors = new Color[SIZE/2];
    
    public Palette(int[] ints)
    {
        //Each color occupies 2 bytes (2 slots in array)
        //Bit 0-4 red intensity, 5-9 green intensity, 10-14 blue intensity
        
        for (int i = 0; i < colors.length; i++)
        {
            //Make and add color to array
            Color color = makeColorFromBytes(ints, i);
            colors[i] = color;
        }
    }
    
    public void writeWallPalette(int offset)
    {
        byte[] paletteBytes = new byte[COLORS_PER_ROW*2];
        try {
            ROM.reader.seek(offset);
            ROM.reader.readFully(paletteBytes);
            //Same color logic as above
            for (int i = 0; i < COLORS_PER_ROW; i++)
            {
                Color color = makeColorFromBytes(paletteBytes, i);
                //Replace FIRST row of colors
                colors[i] = color;
            }
        } catch (IOException ex) {
            Logger.getLogger(Palette.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Color makeColorFromBytes(byte[] data, int index)
    {
         int colorShort = 
                ((data[2*index+1] << 0x08) & 0xFF00) + (data[2*index] & 0xFF);
            //Colors typically range from 0-256, so scale 'em up by 8.
            int red = (colorShort & RED_MASK) * 8;
            int green = ((colorShort & GREEN_MASK) >> 0x05) * 8;
            int blue = ((colorShort & BLUE_MASK) >> 0x0A) * 8;
            
            //System.out.println(red+", "+green+", "+blue);
            
            //Fix colors == 256 to be 255
            red = (red > 255) ? 255 : red;
            green = (green > 255) ? 255 : green;
            blue = (blue > 255) ? 255 : blue;
            
            //Return color
            Color color = new Color(red, green, blue);
            return color;
    }
    
    public Color makeColorFromBytes(int[] data, int index)
    {
         int colorShort = 
                ((data[2*index+1] << 0x08) & 0xFF00) + (data[2*index] & 0xFF);
         //System.out.println(Integer.toHexString(colorShort));
            //Colors typically range from 0-256, so scale 'em up by 8.
            int red = (colorShort & RED_MASK) * 8;
            int green = ((colorShort & GREEN_MASK) >> 0x05) * 8;
            int blue = ((colorShort & BLUE_MASK) >> 0x0A) * 8;
            
            //System.out.println(red+", "+green+", "+blue);
            
            //Fix colors == 256 to be 255
            red = (red > 255) ? 255 : red;
            green = (green > 255) ? 255 : green;
            blue = (blue > 255) ? 255 : blue;
            
            //Return color
            Color color = new Color(red, green, blue);
            return color;
    }
    
    public Color getColor(int index)
    {
        return colors[index];
    }
    
}
