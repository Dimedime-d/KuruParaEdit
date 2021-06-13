package kpdatamanipulator.ops.imgobjhex;

import java.awt.Color;
import java.util.HashMap;

public class PaletteMap {

    private HashMap<Color,Byte> colorMap = new HashMap<>();
    public PaletteMap() 
    {
        //Hardcode color values
        colorMap.put(new Color(0x007028), (byte) 0);
        colorMap.put(new Color(0x980040), (byte) 0xB);
        colorMap.put(new Color(0xB03858), (byte) 0xC);
        colorMap.put(new Color(0xC87078), (byte) 0xD);
        colorMap.put(new Color(0xE0A898), (byte) 0xE);
        colorMap.put(new Color(0xF8E8B8), (byte) 0xF);
    }
    
    public Byte getByte(Color c) //Get color, give byte
    {
        byte b = colorMap.get(c);
        b = (b < 0) ? 0 : b;
        return b;
    }
}
