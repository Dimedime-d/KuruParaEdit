/*
    Attempt to view all 4 Background layers of a KP level given the csv
    9/28: Decompression and recompression works!!111!!!!111!!!!1!111
    10/1: On hold for now, need to look at level loading asm to fix oddities...
*/
package kplevelviewer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import kplevelviewer.OffsetGroup.AdventureMap;
import kplevelviewer.OffsetGroup.CutsceneBackground;
import kplevelviewer.OffsetGroup.MagicLearnBG0;
import kplevelviewer.OffsetGroup.MagicPreview;
import kplevelviewer.OffsetGroup.MinigameSplash;
import kplevelviewer.altdecomp.GenericDecomp;
import kplevelviewer.util.LempelZiv;
import kplevelviewer.util.ROM;

public class KPCompressionWiz{
    public AdventureMap[] levels = AdventureMap.values();
            
    //Arrays with every level's data as bytes, uncompressed
    private int[][] tilesets = new int[4][];
    private int[] tilesetSizes = {0x4000, 0x4000, 0x4000, 0x2000};
    
    private ImageSet tilesetImages;
    
    private int[] tileMap1;
    private int[] tileMap2;
    private int[] tileMap3;
    private int[] tileMap4;
    
    private BufferedImage[][] mapImages1;
    private BufferedImage fullMap1;
    private BufferedImage[][] mapImages2;
    private BufferedImage fullMap2;
    private BufferedImage[][] mapImages3;
    private BufferedImage fullMap3;
    private BufferedImage[][] mapImages4;
    private BufferedImage fullMap4;
    
    private Palette testPalette;
    
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
        //Restructure this...
        //Create romAccess
        ROM.createRomAccess("res/KP - Use in Java.gba"); //Japanese default
        String[] options = {"Decomp Map Level", "Decompress other offset", "Compress a binary", 
            "Decomp Minigame Splash", "Decomp Magic Preview", "Decomp Magic Learn BG0", "Decomp Cutscene Background"};
        /*int response = JOptionPane.showOptionDialog(null, "What to do?", "Pick option",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);
        switch (response)
        {
            case 0:
                new KPCompressionWiz(); break;
            case 1:
                decompressAtOffset(); break;
            case 2:
                compressABinary(); break;
            case 3:
                new GenericDecomp(MinigameSplash.values()); break;
            case 4:
                new GenericDecomp(MagicPreview.values()); break;
            case 5:
                new GenericDecomp(MagicLearnBG0.values()); break;
        }
        //System.exit(0);*/
        
        JPanel startPanel = new JPanel();
        
        //Add shit here
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        int buttonID = 0;
        for (String s : options)
        {
            JButton button = new JButton(s);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) 
                {   
                    try {
                        switch (Integer.parseInt(ae.getActionCommand())) 
                        {
                            case 0:
                            new KPCompressionWiz(); break;
                            case 1:
                                decompressAtOffset(); break;
                            case 2:
                                compressABinary(); break;
                            case 3:
                                new GenericDecomp(MinigameSplash.values()); break;
                            case 4:
                                new GenericDecomp(MagicPreview.values()); break;
                            case 5:
                                new GenericDecomp(MagicLearnBG0.values()); break;
                            case 6:
                                new GenericDecomp(CutsceneBackground.values()); break;
                        }
                    } catch (IOException | NumberFormatException e) {
                        System.out.println("Error instantiating Buttons");
                    }
                }
            });
            button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            button.setActionCommand(Integer.toString(buttonID));
            startPanel.add(button);
            buttonID++;
        }
        
        JFrame frame = new JFrame("Compression Wiz - Pick an option!");
        
        frame.getContentPane().add(startPanel); //Implicit drawing order here
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        frame.setVisible(true);
    }
    
    private static void decompressAtOffset() throws FileNotFoundException
    {
        String input = JOptionPane.showInputDialog(null, "What offset? No header/prefix");
           int offset = Integer.parseInt(input, 16);
           input = JOptionPane.showInputDialog(null, "Bytes to read? (In hex)");
           int bytesToRead = Integer.parseInt(input,16);

           //and now, to attempt decompression
           int[] decompressedData = LempelZiv.decompressGivenBytesToRead(offset, bytesToRead);
           outputBinary(decompressedData);
    }
    
    private static void compressABinary() throws IOException
    {
        JFileChooser fc = new JFileChooser("");
        fc.setDialogTitle("Pick a binary!?");
        fc.setFileFilter(new FileFilter() { //Save Dialog shows ONLY bin files
            
            @Override
            public String getDescription()
            {   return "Binary files (.bin)";}
            
            @Override
            public boolean accept(File f)
            {
                if (f.isDirectory())
                    return true;
                else
                {
                    String filename = f.getName().toLowerCase();
                    return filename.endsWith(".bin");
                }
            }
        });
        int userSelect = fc.showOpenDialog(null); //Prompts file chooser to open
        if (userSelect == JFileChooser.APPROVE_OPTION)
        {   //upon hitting ok...
            byte[] array = Files.readAllBytes(Paths.get(fc.getSelectedFile().getPath()));
            int[] bytes = new int[array.length];
            for (int i = 0; i < array.length; i++)
            {
                bytes[i] = (array[i] & 0xFF); //signed shenanigans
            }
            boolean old = false;
            if (old) {
                int[] compressedData = LempelZiv.compress_old(bytes);
                outputBinaryFile(compressedData);
            } else {
                byte[] data = LempelZiv.compress(bytes);
                outputBinaryFile(data);
            }
        }
    }
    
    protected static void outputBinary(int[] bytes) throws FileNotFoundException
    {
        System.out.println("Outputting to output.txt...");
        try (PrintStream out = new PrintStream(new FileOutputStream("res/output.txt"))) {
            System.setOut(out);
            //Test the print monkaS
            int count = 1;
            for (int i : bytes)
            {
                System.out.print(String.format("%02X", i & 0xFF) + " ");
                if (count % 16 == 0)
                    System.out.println("");
                count++;
            }
        }
    }
    
    protected static void outputBinaryFile(int[] ints) throws IOException
    {
        byte[] stuff = new byte[ints.length];
        for (int i = 0; i < stuff.length; i++)
        {
            stuff[i] = (byte) ints[i];
        }
        outputBinaryFile(stuff);
    }
    
    protected static void outputBinaryFile(byte[] bytes) throws FileNotFoundException, IOException
    {

        System.out.println("Outputting to output.bin...");
        File output = new File("res/output.bin");
        try (FileOutputStream out = new FileOutputStream(output))
        {
            out.write(bytes, 0, bytes.length);
        }
        System.out.println("Done!");
    }
    
    public KPCompressionWiz() throws FileNotFoundException
    {
        //Prompt for level id
        String input = JOptionPane.showInputDialog(null, "Level ID? (-1 = skip)");
        int num = Integer.parseInt(input);
        if (num > -1 && num < levels.length)
        {
        //Extract level tilesets and tile maps from csv
        AdventureMap selectedLevel = levels[num];
        createOneLevelData(selectedLevel, num); //Sets up all the data for one level
        }
        //Decompressing the palette = most important step!!!
        
        /*///Now, attempt to print a level's mapdata:
        //Neo land 4 = level 41
        int[] mapData = LempelZiv.decompress(levelList.get(41).getTileMapOffset1() + 5);
        int count = 1;
        for (int i : mapData)
        {
            System.out.print(Integer.toHexString(i) + " ");
            if (count % 16 == 0)
                System.out.println("");
            count++;
        }*/
        //attemptCompress();
        
        // DRAW IMAGES HERE
        if (mapImages1 != null)
        {
            fullMap1 = new BufferedImage(mapImages1[0].length*8, mapImages1.length*8, BufferedImage.TYPE_INT_ARGB);
            Graphics gMap1 = fullMap1.createGraphics();
            for (int y = 0; y < mapImages1.length; y++)
            {
                for (int x = 0; x < mapImages1[0].length; x++)
                {
                    gMap1.drawImage(mapImages1[y][x], x*8 , y*8, null);
                }
            }
        }
        if (mapImages2 != null)
        {
            fullMap2 = new BufferedImage(mapImages2[0].length*8, mapImages2.length*8, BufferedImage.TYPE_INT_ARGB);
            Graphics gMap2 = fullMap2.createGraphics();
            for (int y = 0; y < mapImages2.length; y++)
            {
                for (int x = 0; x < mapImages2[0].length; x++)
                {
                    gMap2.drawImage(mapImages2[y][x], x*8 , y*8, null);
                }
            }
        }
        if (mapImages3 != null)
        {
            fullMap3 = new BufferedImage(mapImages3[0].length*8, mapImages3.length*8, BufferedImage.TYPE_INT_ARGB);
            Graphics gMap3 = fullMap3.createGraphics();
            for (int y = 0; y < mapImages3.length; y++)
            {
                for (int x = 0; x < mapImages3[0].length; x++)
                {
                    gMap3.drawImage(mapImages3[y][x], x*8 , y*8, null);
                }
            }
        }
        if (mapImages4 != null)
        {
            fullMap4 = new BufferedImage(mapImages4[0].length*8, mapImages4.length*8, BufferedImage.TYPE_INT_ARGB);
            Graphics gMap4 = fullMap4.createGraphics();
            for (int y = 0; y < mapImages4.length; y++)
            {
                for (int x = 0; x < mapImages4[0].length; x++)
                {
                    gMap4.drawImage(mapImages4[y][x], x*8 , y*8, null);
                }
            }
        }
        //
        
        //Test display palette
        JFrame frame = new JFrame("Test");
        
        //Add stuff here
        frame.getContentPane().add(new DrawPanel(fullMap4, fullMap3, fullMap2, fullMap1)); //Implicit drawing order here
        frame.setSize(new Dimension(1000, 1000));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        frame.setVisible(true);
        
    }
    
    private void createOneLevelData(AdventureMap level, int levelID) throws FileNotFoundException
    {
        //Attempt to Decompress palette
        int[] paletteData = LempelZiv.decompress(level.getCompressedPaletteOffset(),
                Palette.SIZE);
        testPalette = new Palette(paletteData);
        //Write in tile data (it's uncompressed)
        testPalette.writeWallPalette(level.getWallPaletteOffset());
        int[] tilesetOffsets = level.getTilesetOffsets();
        for (int i = 0; i < 4; i++)
        {
            int offset = tilesetOffsets[i];
            tilesets[i] = LempelZiv.decompress(offset, tilesetSizes[i]);
        }

        tilesetImages = new ImageSet (testPalette, tilesets);
        
        tileMap1 = LempelZiv.decompress(level.getTileMapOffsets()[0]);
        tileMap2 = LempelZiv.decompress(level.getTileMapOffsets()[1]);
        //outputBinary(tileMap2);
        tileMap3 = LempelZiv.decompress(level.getTileMapOffsets()[2]);
        tileMap4 = LempelZiv.decompress(level.getTileMapOffsets()[3]);
        
        //Fiddling around with extra offsets!
        int offset1 = 0x600, offset2 = 0x400, offset3 = 0x400, offset4 = 0;
        switch (levelID)
        {
            case 0: case 1: case 2: case 3: case 4:
                offset1 = 0x600; offset2 = 0; offset3 = 0x200; offset4 = 0;
                break;
            case 5: case 6: case 7:
                offset1 = 0x600; offset2 = 0x200; offset3 = 0; offset4 = 0x200;
                break;
            case 8: case 9:
                offset1 = 0x600; offset2 = 0; offset3 = 0x200; offset4 = 0x200; //Fix LAyers
                break;
            case 10: case 11: case 12:
                offset1 = 0x600; offset2 = 0x200; offset3 = 0x200; offset4 = 0; //Fix transparency
                break;
            
            case 16: case 17: case 18:
                offset1 = 0x600; offset2 = 0x200; offset3 = 0; offset4 = 0x200;
                break;
            case 19: case 20: //Mining
                offset1 = 0x600; offset2 = 0x000; offset3 = 0x000; offset4 = 0x000;
                break;
            case 21: case 22: case 23: //Train
                offset1 = 0x600; offset2 = 0x000; offset3 = 0x200; offset4 = 0x200;
                break;
            case 24: case 25: case 26: //Clock
                offset1 = 0x600; offset2 = 0x000; offset3 = 0x200; offset4 = 0x200;
                break;
            case 27: case 28: //Sand
                offset1 = 0x600; offset2 = 0x600; offset3 = 0x000; offset4 = 0x200; //Fix Transparency
                break;
            case 29: case 30: case 31: //Circus
                offset1 = 0x600; offset2 = 0x200; offset3 = 0x200; offset4 = 0; //Fix Offset3....
                break;
            case 32: case 35: case 36: //Spooky
                offset1 = 0x600; offset2 = 0x200; offset3 = 0x200; offset4 = 0;
                break;
            case 33: //Castle
                offset1 = 0x600; offset2 = 0; offset3 = 0; offset4 = 0;
                break;
            case 34: case 37: //Castle2
                offset1 = 0x600; offset2 = 0; offset3 = 0x200; offset4 = 0; //Fix spotlight
                break;
            case 38: case 39: case 40: case 41: //Neo
                offset1 = 0x600; offset2 = 0; offset3 = 0x200; offset4 = 0;
                break;
        }
        //Write tile images to map
        mapImages1 = tilesetImages.createTileMap(tileMap1, offset1);
        mapImages2 = tilesetImages.createTileMap(tileMap2, offset2);
        if (levelID <= 41) {
            mapImages3 = tilesetImages.createTileMap(tileMap3, offset3);
        } else {
            mapImages3 = tilesetImages.createTileMap(tileMap3, offset3, 16, 16);
        }
        mapImages4 = tilesetImages.createTileMap(tileMap4, offset4);
    }
    
    /*public void createLevelListFromcsv()
    {
        //Extract Relevant data from csv
        File dataFile = new File("res/MapData.csv");
        OffsetGroup.AdventureMap.
        try 
        {
            Scanner reader = new Scanner(dataFile);
            //Iterate through each line of the csv
            while (reader.hasNextLine())
            {
                String[] elements = reader.nextLine().split(",");
                //Initialize important elements to pass to OffsetGroup
                if (elements[2].length() > 1) //Check if there is a level to pass
                {
                    //INITIALIZE KPLEVELS IN HERE
                    int tilesetOffset1 = Integer.parseInt(elements[4], 16) & 0x00FFFFFF;
                    int tilesetOffset2 = Integer.parseInt(elements[5], 16) & 0x00FFFFFF;
                    int tilesetOffset3 = Integer.parseInt(elements[6], 16) & 0x00FFFFFF;
                    int tilesetOffset4 = Integer.parseInt(elements[7], 16) & 0x00FFFFFF;
                    
                    int tileMapOffset1 = Integer.parseInt(elements[8], 16) & 0x00FFFFFF;
                    int tileMapOffset2 = Integer.parseInt(elements[9], 16) & 0x00FFFFFF;
                    int tileMapOffset3 = Integer.parseInt(elements[10], 16) & 0x00FFFFFF;
                    int tileMapOffset4 = (int) Long.parseLong(elements[11], 16) & 0x00FFFFFF;
                    //2-3 has strange BG3

                    int compressedPaletteOffset = Integer.parseInt(elements[12], 16) & 0x00FFFFFF;
                    int wallPaletteOffset = Integer.parseInt(elements[14], 16) & 0x00FFFFFF;
                    
                    OffsetGroup level = new OffsetGroup(
                        tilesetOffset1, tilesetOffset2, tilesetOffset3, tilesetOffset4,
                        tileMapOffset1, tileMapOffset2, tileMapOffset3, tileMapOffset4,
                        compressedPaletteOffset, wallPaletteOffset);
                    
                    levelList.add(level);
                }
            }
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(KPCompressionWiz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    */
    /*
    public void attemptCompress()
    {
        //Attempt to Decompress palette
        int[] paletteData = LempelZiv.decompress(levels[41].getCompressedPaletteOffset(),
                Palette.SIZE);
        //testPalette = new Palette(paletteData);
        
        //Print palette data
        int count = 1;
        for (int i : paletteData)
        {
            System.out.print(String.format("%02X", i & 0xFF) + " ");
            if (count % 16 == 0)
                System.out.println("");
            count++;
        }
        
        System.out.println("");
        
        //and now, to attempt recompression
        int[] paletteDataRecompressed = LempelZiv.compress(paletteData);
        
        //Test the print monkaS
        count = 1;
        for (int i : paletteDataRecompressed)
        {
            System.out.print(String.format("%02X", i & 0xFF) + " ");
            if (count % 16 == 0)
                System.out.println("");
            count++;
        }
        
        System.out.println("\n");
        
        //Testing re-decompression
        paletteData = LempelZiv.decompress(paletteDataRecompressed);
        
        //Print palette data
        count = 1;
        for (int i : paletteData)
        {
            System.out.print(String.format("%02X", i & 0xFF) + " ");
            if (count % 16 == 0)
                System.out.println("");
            count++;
        }
    }*/

}
        