package kpdatamanipulator.ops.tileget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import kpdatamanipulator.select.KPRomAccess;

public class TileGetterFrame extends JComponent{

    private RandomAccessFile romAccess;
    private ObjImageHandler oih;
    
    private JFrame frame;

    private int width = 800, height = 800;
    
    private final int PAL_OFFSET = 0x1C65D0;
    private final int[] PAL_COLORS_RGB = {
        0xff2098a0,
        0xff000000,
        0xff2098a0,
        0xfff09898,
        0xff004090,
        0xff0060a0,
        0xff0080b8,
        0xffb84800,
        0xffc88010,
        0xffe0b828,
        0xfff8f840,
        0xff000000,
        0xff383838,
        0xff787878,
        0xffb8b8b8,
        0xfff8f8f8,};
    
    private final int BASE_TILE_OFFSET; //obj data
    
    private final int TEXT1_PTR_OFFSET; //Relative offsets to add to obj data
        private final int MAX_ENG_CHAR_BOUND1 = 0x039414; //Everything in between text ptr 1 offset
        private final int ENG_CHAR_COUNT1 = 107; //including dupes
    private final int SET1_CHAR_COUNT = 319;
        //contains data pertaining to the english/special characters (the ones I care about)
    private final int TEXT2_PTR_OFFSET;
        private final int MAX_ENG_CHAR_BOUND2 = 0x039C84;
        private final int ENG_CHAR_COUNT2 = 287; //208 without blanks
    

    protected enum tileSize{EIGHT_SQUARE, SIXTEEN_TALL, SIXTEEN_WIDE,
            SIXTEEN_SQUARE, NONE};
    
    private ArrayList<BufferedImage> textImages1 = new ArrayList<>();
    private ArrayList<BufferedImage> textImages2 = new ArrayList<>();

    private ArrayList<BufferedImage> charImages = new ArrayList<>();
    //Container of every compleat character image
    
    public TileGetterFrame(KPRomAccess roms) 
      throws FileNotFoundException {
        int option = KPRomAccess.pickRomLanguage();
        if (option == KPRomAccess.JPN)
        {   //Japanese
            romAccess = roms.getRomAccess();
            oih = new ObjImageHandler(roms.getClonedRomAccess());
            TEXT1_PTR_OFFSET = 0x03912C;
            TEXT2_PTR_OFFSET = 0x039808;
            BASE_TILE_OFFSET = 0x284F70;
        } else { 
            //Chinese
            romAccess = roms.getRomAccessChinese();
            oih = new ObjImageHandler(roms.getClonedRomAccessChinese());
            TEXT1_PTR_OFFSET = 0x03AF30;
            TEXT2_PTR_OFFSET = 0x03DDA8;
            BASE_TILE_OFFSET = 0x280EEC;
        }
        
        //If chinese, dump all images into designated folder
        if (option == KPRomAccess.CHN)
        {
            if (JOptionPane.showConfirmDialog(null, "Write Tiles?",
                    "Write tiles to folder?", JOptionPane.YES_NO_OPTION) ==
                    JOptionPane.YES_OPTION)
            {
                try 
                {
                    writeTiles();
                } catch (IOException e) {
                    System.out.println("Error making images");
                }
            }
        }
        

        /*try {
            printPalettes();
        } catch (IOException ex) {
            Logger.getLogger(TileGetterFrame.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        //Goal: Read offset to get pointer, add to base tile offset to get obj data,
        //then draw obj and return array of images.
        if (TEXT1_PTR_OFFSET != 0) getTiles(TEXT1_PTR_OFFSET, textImages1);
        if (TEXT2_PTR_OFFSET != 0) getTiles(TEXT2_PTR_OFFSET, textImages2);

        //Put all the image lists together
        charImages.addAll(textImages1);
        charImages.addAll(textImages2);

        setUpFrame();

        //
        // Multiple different methods from here on out
        // GOAL #2: Write a file of size 0x1000? to make a width table of each character!
        //
        //Block pertaining to 1st English Characters
        /*
            ArrayList<BufferedImage> engChars = new ArrayList<>(charImages.subList(0,ENG_CHAR_COUNT1));

            EnglishTileHandler egh = new EnglishTileHandler(PAL_COLORS_RGB, engChars, romAccess);

            ArrayList<Integer> widths = new ArrayList<>();
            ArrayList<Integer> halfOffsets = new ArrayList<>();

            //Calc the width of each individual char image!!
            widths = egh.calcWidths(engChars, width);

            halfOffsets = egh.listEngRelOffsets(TEXT1_PTR_OFFSET, MAX_ENG_CHAR_BOUND1, true);

            ArrayList<Integer> offsets = egh.listEngRelOffsets(TEXT1_PTR_OFFSET, MAX_ENG_CHAR_BOUND1, false);
        
            
        //Block pertaining to 2nd set of English chars
            
            ArrayList<BufferedImage> engChars2 = new ArrayList<>(charImages.subList(SET1_CHAR_COUNT,SET1_CHAR_COUNT+ENG_CHAR_COUNT2));

            EnglishTileHandler egh2 = new EnglishTileHandler(PAL_COLORS_RGB, engChars2, romAccess);

            ArrayList<Integer> widths2 = egh2.calcWidths(engChars2, width);
            ArrayList<Integer> halfOffsets2 = egh2.listEngRelOffsets(TEXT2_PTR_OFFSET, MAX_ENG_CHAR_BOUND2, true);

            try {
                //Lastly, our table writer to write the widths at the given offsets!
                TableWriter tw = new TableWriter(new File("table3b.bin"), "rw", 0x1000);
                tw.writeOffsetsToFile(widths, halfOffsets);
                tw.writeOffsetsToFile(widths2, halfOffsets2);
                

            } catch (FileNotFoundException ex) {
                Logger.getLogger(TileGetterFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        */
        //System.out.println("Table created");
        
        //dumpObjData(BASE_TILE_OFFSET, offsets);
        //Must delete each time
        
        //GOAL #3: Dynamically read key inputs in a separate text field to calculate width
        //Already done!!

    }
    
    public final void setUpFrame() {
        frame = new JFrame("Character Showcase"); //Title
        frame.getContentPane().add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width,height);
        frame.setLocationRelativeTo(null); //centered
        frame.setResizable(false);
        frame.setUndecorated(true);
        frame.addKeyListener(new Keys());
        frame.setVisible(true);
    }
    
    private class Keys extends KeyAdapter
    {
        @Override
        public void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                frame.dispose();
            }
        }
    }
    
    //Drawing time!
    @Override
    public void paintComponent(Graphics g)
    {
        g.setColor(new Color(PAL_COLORS_RGB[0]));
        g.fillRect(0,0,width,height);
        /*//Draw grid
        g.setColor(Color.BLACK);
        for (int y = 0; y < height; y += TileImg.TILE_HEIGHT*5) {
            g.drawLine(0,y,width,y);
        }
        for (int x = 0; x < width; x += TileImg.TILE_WIDTH*5) {
            g.drawLine(x,0,x,height);
        }*/
        
        if (charImages != null)
        {
            int charImageIndex = 0;
            for (int y = 0; y < height; y += TileImg.TILE_HEIGHT)
            {
                for (int x = 0; x < width; x += TileImg.TILE_WIDTH)
                {
                    if (charImageIndex < charImages.size()) //If there's another image to draw
                    {
                        if (charImageIndex == textImages1.size())
                        {
                            y+= TileImg.TILE_HEIGHT*2; //skip a line between diff lists
                            x = 0;
                        }
                        g.drawImage(charImages.get(charImageIndex),x,y,null);
                        charImageIndex++;
                    } else break;
                }
            }
        }
        
        
        
    }
    
    public final void printPalettes() throws IOException {
        JOptionPane.showMessageDialog(null, "Dev method: Correctly convert the"
                + " 15-bit colors in the ROM to standard 32-bit RGB colors.");
        //The bytes are reversed...
        romAccess.seek(PAL_OFFSET);
        
        for (int i = 0; i < PAL_COLORS_RGB.length; i++) {
            byte[] temp = new byte[2];
            romAccess.readFully(temp);
            
            short colorWord = (short) (temp[0] & 0xFF | ((temp[1] << 8) & 0xFF00));
            
            int red = (colorWord & 0b11111) * 8;
            int green = ((colorWord >>> 5) & 0b11111) * 8;
            int blue = ((colorWord >>> 10) & 0b11111) * 8;
            
            //System.out.println(
            //    "new Color("+red+","+green+","+blue+")"+",");
        
            Color color = new Color(red,green,blue);
            System.out.println("0x"+Integer.toHexString(color.getRGB())+",");
        }
        
    }
    
    public final void getTiles(int offset, ArrayList<BufferedImage> imageList) {
        try {
            romAccess.seek(offset); //go to starting offset (one time)

            while (true) 
            {
                byte[] temp = new byte[4]; //stores individual bytes of rel offset
                romAccess.readFully(temp);
                int relOffset = toRelOffset(temp);
                    //System.out.println(Integer.toHexString(relOffset));

                if (relOffset == 0xFFFFFFFF) { //Adds a placeholder where there isn't a tile referenced
                    imageList.add(new BufferedImage(TileImg.TILE_WIDTH,
                        TileImg.TILE_HEIGHT,BufferedImage.TYPE_INT_ARGB));
                    continue;
                }
                
                if (relOffset < 0 || relOffset > 0xFFFFFF) break; //invalid ptr

                int currentObjOffset = (BASE_TILE_OFFSET + relOffset);
                //Points to 8 bytes of data before graphics
                BufferedImage obj = oih.drawObjAt(currentObjOffset);

                //Finally, adding the image to the list (If it exists)!
                //if (obj.getWidth() > 1)
                imageList.add(obj);

            }
        } catch (IOException ex) {
            Logger.getLogger(TileGetterFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public final void writeTiles() throws IOException //(Chinese only)
    {
        for (int i = 0; i <= 0xFFFF; i++)
        {
            int num = characterParse(i);
            if (num > 0 && num < 0x7FFFFF && num > 0x2DD1F4 && num < 0x2FA27C)
            {   //Valid data
                BufferedImage obj = oih.drawObjAt(num);
                //Temporary variable flipping the bytes of i
                int j = ((i & 0xFF) << 8) | ((i & 0xFF00) >> 8);
                ImageIO.write(obj,"png",new File("out/chinese2/0x"+hex(j,4)+".png"));
            }
        }
    }
    
    public int characterParse(int dat)
    {
        try {
            int base = ((dat-0xA1) & 0xFF)*0x5E + 0xFF5F + ((dat & 0xFF00) >> 0x08);
            base = base << 0x10;
            base = base >> 0x0E;
            base += 0x3AF30;
            romAccess.seek(base);
            byte[] temp = new byte[4]; //stores individual bytes of rel offset
            romAccess.readFully(temp);
            int relOffset = toRelOffset(temp);
            
            relOffset += 0x280EEC; //gfx in rom //do NOT skip 8 bytes
            return relOffset;
        } catch (IOException ex) {
            System.out.println(ex);
            System.exit(1);
            return 0;
        }
    }
    
    private String hex(int num, int nibbles) {
        return String.format("%0"+nibbles+"X",num);
    }
    
    public int toRelOffset(byte[] bytes) {
        //flips bytes and concatenates them in that reversed order
        return ( bytes[0] & 0xFF |
                 ((bytes[1] << 8) & 0xFF00) |
                 ((bytes[2] << 16) & 0xFF0000) |
                 ((bytes[3] << 24) & 0xFF000000)
                 );
    }
    
    private class ObjImageHandler { //Handles drawing objects
        
        private tileSize currentTileSize;
        private final int BLOCK_SIZE = 8;
        
        private RandomAccessFile objDataAccess;
        
        public ObjImageHandler(RandomAccessFile ra) 
        {
            objDataAccess = ra;
        };
        
        public BufferedImage drawObjAt(int offset) {
            try {
                objDataAccess.seek(offset); //1st 8 bytes are strictly obj data
                
                byte[] objData = new byte[8];
                objDataAccess.readFully(objData);
                
                byte tileSizeHex = objData[6]; //Seventh byte
                
                int numTileBlocks; //number of 8x8 blocks to draw
                
                //Retrieving the correct tile size
                switch (tileSizeHex) {
                    case 0x11: //8x8
                        currentTileSize = tileSize.EIGHT_SQUARE;
                        numTileBlocks = 1; break;
                    case 0x12: //16x8 wide
                        currentTileSize = tileSize.SIXTEEN_WIDE;
                        numTileBlocks = 2; break;
                    case 0x21:
                        currentTileSize = tileSize.SIXTEEN_TALL;
                        numTileBlocks = 2; break;
                    case 0x22:
                        currentTileSize = tileSize.SIXTEEN_SQUARE;
                        numTileBlocks = 4; break;
                    default:
                        currentTileSize = tileSize.NONE;
                        //return new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
                        numTileBlocks = 1;
                }
                
                BufferedImage[] tileBlocks = new BufferedImage[numTileBlocks];
                //Draw each individual 8x8 block according to 4bpp palette
                for (int i = 0; i < tileBlocks.length; i++)
                {
                    tileBlocks[i] = drawBlock4bpp();
                }
                
                BufferedImage wholeTile = pieceBlocksTogether(tileBlocks);
                //Finally, putting the images together
                
                return wholeTile;
            } catch (IOException ex) {
                Logger.getLogger(TileGetterFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null; //unreachable
        }
        
        public BufferedImage drawBlock4bpp() { //Draw individual 8x8 block from ROM!
            //Offset already set...
            byte[] blockBytes = new byte[32]; //32 bytes per 8x8 block
            try {
                objDataAccess.readFully(blockBytes);
            } catch (IOException ex) {
                Logger.getLogger(TileGetterFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            int blockByteIndex = 0;
            
            BufferedImage tempBlock = new BufferedImage(
                    BLOCK_SIZE,BLOCK_SIZE,BufferedImage.TYPE_INT_ARGB);
            Graphics g = tempBlock.createGraphics();
            //Use g to draw, return tempBlock...
            
            for (int y = 0; y < tempBlock.getHeight(); y++)
            {
                for (int x = 0; x < tempBlock.getWidth(); x++)
                {
                    if (x % 2 == 0)
                    { //1st nibble (1/2 byte being read
                        int paletteIndex = blockBytes[blockByteIndex] & 0xF;
                        //Lower 4 bits
                        
                        //draw individual pixel
                        tempBlock.setRGB(x,y,PAL_COLORS_RGB[paletteIndex]);
                    } else { //2nd nibble being read
                        int paletteIndex = 
                            (blockBytes[blockByteIndex] >>> 4) & 0xF ;
                        //upper 4 bits
                        
                        //draw individual pixel
                        tempBlock.setRGB(x,y,PAL_COLORS_RGB[paletteIndex]);
                        
                        blockByteIndex++; //read next byte
                    }
                }
            }
            return tempBlock;
        }
        
        public BufferedImage pieceBlocksTogether(BufferedImage[] blocks )
        {
            BufferedImage fullTile;
            
            switch (currentTileSize) { //Decide how to combine 8x8 blocks!
                case EIGHT_SQUARE:
                    fullTile = blocks[0];
                    break;
                case SIXTEEN_TALL:
                    fullTile = combineImgY(blocks[0],blocks[1]);
                    break;
                case SIXTEEN_WIDE:
                    fullTile = combineImgX(blocks[0],blocks[1]);
                    break;
                case SIXTEEN_SQUARE:
                    fullTile = combineImgY(
                            combineImgX(blocks[0],blocks[1]),
                            combineImgX(blocks[2],blocks[3])
                        );
                    break;
                default:
                    fullTile = new BufferedImage(
                            BLOCK_SIZE,BLOCK_SIZE,BufferedImage.TYPE_INT_ARGB);
            }
            return fullTile;
        }
        
        public BufferedImage combineImgY(BufferedImage img1, BufferedImage img2)
        {   //vertically combine the 2 images
            BufferedImage temp = new BufferedImage(
                Math.max(img1.getWidth(), img2.getWidth()),
                img1.getHeight()+img2.getHeight(), 
                BufferedImage.TYPE_INT_ARGB);
            
            Graphics g = temp.createGraphics();
            g.drawImage(img1,0,0,null);
            g.drawImage(img2,0,img1.getHeight(),null);
            g.dispose(); //Avoid those memory leaks!
            
            return temp;
        }
        
        public BufferedImage combineImgX(BufferedImage img1, BufferedImage img2)
        {   //hotizontally combine the 2 images (8x8 into wide) (same 
            BufferedImage temp = new BufferedImage(
                img1.getWidth()+img2.getWidth(),
                Math.max(img1.getHeight(),img2.getHeight()),
                BufferedImage.TYPE_INT_ARGB);
            
            Graphics g = temp.createGraphics();
            g.drawImage(img1,0,0,null);
            g.drawImage(img2,img1.getWidth(),0,null);
            g.dispose();
            
            return temp;
        }
    }
    
    public void dumpObjData(int basePtr, ArrayList<Integer> origOffsets) {
        
        //Spit out a .bin file for the data of each object!
        ArrayList<Integer> relOffsets = new ArrayList<>(origOffsets);
        while (relOffsets.size() > 0) {
            try {
                int objOffset = basePtr+relOffsets.get(0);
                String objOffsetHexString = Integer.toHexString(objOffset);
                RandomAccessFile objDataWriter = new RandomAccessFile(
                        "dumps/modded/"+objOffsetHexString+".bin", "rw");
                
                do {
                    romAccess.seek(objOffset + 6); //Goes to Object Data (Properties + GFX) and tilesize
                    int tileSizeByte = romAccess.readByte();

                    romAccess.seek(objOffset);



                    int bytesToDump = 0;
                    switch (tileSizeByte) {
                        case 0x11: bytesToDump = 0x28; break;
                        case 0x12: bytesToDump = 0x48; break;
                        case 0x21: bytesToDump = 0x48; break;
                        case 0x22: bytesToDump = 0x88; break;
                    }

                    byte[] raw = new byte[bytesToDump]; //Array to be read, then written

                    romAccess.readFully(raw);
                    
                    //Change x-displacement on every character to 0
                    raw[4] = 0;
                    //Only extend the file if writer is at the END of the file.
                    if (objDataWriter.getFilePointer() == 0 || objDataWriter.getFilePointer() == (objDataWriter.length() - 1))
                        objDataWriter.setLength(objDataWriter.length() + bytesToDump);
                    //extending current file
                    objDataWriter.write(raw);
                    //and writing appropriate bytes

                    relOffsets.remove(relOffsets.indexOf(objOffset - basePtr));
                    //objOffset points to beginning of obj data (so they can be id'd and be removed)
                    objOffset = (int) romAccess.getFilePointer();
                    
                } while (relOffsets.contains((int) romAccess.getFilePointer() - basePtr));
                
                //If the offsetlist contains romAccess' file pointer (i.e. the data is continuous),
                //Then... continue writing to the same file (see above)
                
                //Below is lazy way to copy-paste into asm file
                System.out.println("\n.org 0x08"+objOffsetHexString);
                System.out.println(".incbin "+objOffsetHexString+".bin");
                
            } catch (IOException ex) {
                Logger.getLogger(TileGetterFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
