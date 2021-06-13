package kplevelviewer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class ImageSet //Uses GBA way of storing 4-bit depth images
{
    //This has the raw byte info with palette of each pixel
    protected ArrayList<Integer> fullTileDataSet = new ArrayList<>();
    public final int BYTES_PER_TILE = 0x20;
    
    protected Palette currentPalette;
    
    public ImageSet() {
        
    }
    
    //Put ALL the tileset data into one tileMap
    public ImageSet(Palette palette, int[][] tilesets)
    {
        currentPalette = palette;
        
        for (int[] arg : tilesets)
        {
            for (int i = 0; i < arg.length; i++)
            {
                //Fix: lower 4 bits define left dot, upper 4 bits define right dot
                int leftColorNib = (arg[i] & 0x0F);
                int rightColorNib = (arg[i] & 0xF0);
                int colorByte = ((leftColorNib << 0x04) & 0xF0) | ((rightColorNib >> 0x04) & 0x0F);
                fullTileDataSet.add(colorByte);
            }
        }
    }
    
    public BufferedImage[][] createTileMap(int[] mapData, int extraOffset)
    {
        return createTileMap(mapData, extraOffset, -1, -1);
    }
    
    public BufferedImage[][] createTileMap(int[] mapData, int extraOffset, int mapWidth, int mapHeight)
    {
        BufferedImage[][] tileMapImages;
        //Array to ArrayList
        ArrayList<Integer> mapDataList = new ArrayList<>();
        if (mapData == null) return null;
        for (int i : mapData)
        {
            mapDataList.add(i & 0xFF);
        }
        
        
        Iterator mapDataReader = mapDataList.iterator();
        int width, height;
        if (mapWidth == -1 && mapHeight == -1)
        {
        //First, read each tilemap's size (width at byte 0/1, height at byte 2/3)
        int widthByte2 = (Integer) mapDataReader.next();
        int widthByte1 = (Integer) mapDataReader.next();
        width = (widthByte2 & 0xFF) + ((widthByte1 << 0x08) & 0xFF00);
        //Reverses order of bytes
        int heightByte2 = (Integer) mapDataReader.next();
        int heightByte1 = (Integer) mapDataReader.next();
        height = (heightByte2 & 0xFF) + ((heightByte1 << 0x08) & 0xFF00);
        
        mapDataReader = mapDataList.iterator();
        mapDataReader.next();
        mapDataReader.next();
        mapDataReader.next();
        mapDataReader.next();
        } else {
            width = mapWidth;
            height = mapHeight;
        }
        //System.out.println(width + " " + height);
        tileMapImages = new BufferedImage[height+1][width+1];
        int row = 0, column = 0;
        //Read each map tile's data (2 bytes per entry), incl. tile number, hflip/vflip, and palette
        //And add each image
        while (mapDataReader.hasNext())
        {
        int tileByte2 = (Integer) mapDataReader.next();
        int tileByte1 = (Integer) mapDataReader.next();
        int tileBytes = (tileByte2 & 0xFF) + ((tileByte1 << 0x08) & 0xFF00);
        BufferedImage tile = constructTile(tileBytes, extraOffset);
        //Add tile to map!
        tileMapImages[row][column] = tile;
            column++;
            if (column >= width)
            {
                row++;
                column = 0;
            }
            
        }
        
        return tileMapImages;
    }
        
    protected BufferedImage constructTile(int tileData, int extraOffset)
    {
        //Get tile number and palette data
        int tileNum = (tileData & 0b0000_0011_1111_1111);
        tileNum += extraOffset;
        int tileDataPosition = tileNum * BYTES_PER_TILE;
        int paletteNum = ((tileData & 0b1111_0000_0000_0000) >> 12);
        int paletteDataPosition = paletteNum * Palette.COLORS_PER_ROW;

        //Each tile image 16x16
        BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        //Graphics2D g = image.createGraphics(); //Create graphics context

        int index = 0;
        for (int y = 0; y < 8; y++) //Each row
        {
            for (int x = 0; x < 8; x += 2) //Every other column for each byte
            {   //DRAW tile in here
                int tileByte = fullTileDataSet.get(tileDataPosition + index);
                int leftDotPalNum = ((tileByte >> 4) & 0x0F); //top 4 bits
                //NEW: Color 0 is transparent
                Color leftDotColor;
                if (leftDotPalNum == 0)
                    leftDotColor = new Color(0, 0, 0, 0);
                else
                    leftDotColor = currentPalette.getColor(
                    paletteDataPosition + leftDotPalNum);
                image.setRGB(x, y, leftDotColor.getRGB());

                int rightDotPalNum = (tileByte & 0x0F); //bottom 4 bits
                Color rightDotColor;
                if (rightDotPalNum == 0)
                    rightDotColor = new Color(0, 0, 0, 0);
                else
                    rightDotColor = currentPalette.getColor(
                    paletteDataPosition + rightDotPalNum);
                image.setRGB((x+1), y, rightDotColor.getRGB());

                index++;
            }
        }
        //TODO: Handle Image HFlipping/VFlipping
        int hFlipNum = (tileData & 0b0000_0100_0000_0000);
        boolean hFlip = (hFlipNum != 0); //TRUE if there's a bit set
        int vFlipNum = (tileData & 0b0000_1000_0000_0000);
        boolean vFlip = (vFlipNum != 0);

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        BufferedImage newImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        if (hFlip && !vFlip) //Horiz but no vert flip
        {
            newImage.createGraphics().drawImage(image, imgWidth, 0, imgWidth*-1, imgHeight, null);
            return newImage;
        }
        if (!hFlip && vFlip) //Vert but no horiz flip
        {
            newImage.createGraphics().drawImage(image, 0, imgHeight, imgWidth, imgHeight*-1, null);
            return newImage;
        }
        if (hFlip && vFlip) //Both flips
        {
            newImage.createGraphics().drawImage(image, imgWidth, imgHeight, imgWidth*-1, imgHeight*-1, null);
            return newImage;
        }

        return image;
    }
}
