package kplevelviewer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class EightBitDepthImageSet extends ImageSet {

    public EightBitDepthImageSet(Palette palette, int[][] tilesetArgs) 
    {
        super();
        currentPalette = palette;
        
        for (int[] arg : tilesetArgs)
        {
            for (int i = 0; i < arg.length; i++)
            {
                //Fix: lower 4 bits define left dot, upper 4 bits define right dot
                fullTileDataSet.add(arg[i]);
            }
        }
    }

    @Override
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
        width = mapWidth;
        height = mapHeight;
        
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
    
    @Override
    protected BufferedImage constructTile(int tileData, int extraOffset)
    {
        //Get tile number and palette data
        int tileNum = (tileData & 0b0000_0011_1111_1111);
        //System.out.println(tileNum);
        int tileDataPosition = tileNum * 0x40;

        //Each tile image 16x16
        BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        //Graphics2D g = image.createGraphics(); //Create graphics context

        int index = 0;
        for (int y = 0; y < 8; y++) //Each row
        {
            for (int x = 0; x < 8; x++) //Every other column for each byte
            {   //DRAW tile in here, pixel by pixel
                int tileByte = (fullTileDataSet.get(tileDataPosition + index) & 0xFF);
                Color pixColor = currentPalette.getColor(tileByte);
                image.setRGB(x, y, pixColor.getRGB());

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
