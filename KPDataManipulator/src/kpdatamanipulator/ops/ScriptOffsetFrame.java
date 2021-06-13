//Finds a particular hex string indicating a "scrolling script" like in the cutscenes
//Other part finds the endpoints of the scripts and formats them such that they
//can be ripped with Cartographer

package kpdatamanipulator.ops;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import kpdatamanipulator.select.KPRomAccess;

public class ScriptOffsetFrame extends JFrame{

    /**
     *
     */
    private static final long serialVersionUID = -6611462089404925949L;
    private RandomAccessFile romAccess;
    private final byte[] PRE_SCRIPT_BYTES = {0x0A,0,0x14,0,0x18,(byte)0xFF,(byte)0xFF,0x7F};
    private ArrayList<String> offsetList = new ArrayList<>();
    private int option;
    
    //Just pointers to text, not actual text here
    private final int MAGIC_SCRIPT_START;
    private final int MAGIC_SCRIPT_END;
    
    public ScriptOffsetFrame (KPRomAccess roms) {
        super();
        option = KPRomAccess.pickRomLanguage();
        if (option == KPRomAccess.JPN) 
        {   //Prep Japanese offsets
            romAccess = roms.getRomAccess();
            MAGIC_SCRIPT_START = 0x3BD7C;
            MAGIC_SCRIPT_END = 0x3C07C;
        }
        else 
        {   //Prep Chinese offsets
            MAGIC_SCRIPT_START = 0x3D8A8;
            MAGIC_SCRIPT_END = 0x3DBA8;
            romAccess = roms.getRomAccessChinese();
        }
        /*
        int response = JOptionPane.showConfirmDialog(null,"Would you like to"
                + " seek \"scrolling\" script offsets in the ROM?\nThis could take a while",
                "Skip?",
                JOptionPane.YES_NO_OPTION);
        
        if (response == JOptionPane.YES_OPTION) seekOffsets(PRE_SCRIPT_BYTES);
        else 
        {  int response2 = JOptionPane.showConfirmDialog(null,"Would you like to"
            + " seek magic description script offsets in the ROM?");
            
            if (response2 == JOptionPane.YES_OPTION) seekOffsetsMagic();
            else 
            {  //Ask to cartographer from a script offset txt file
                String input = JOptionPane.showInputDialog(null, 
                    "Assuming you've already made a txt file in the"
                  + " \"scriptOffsets\" folder labeled with a number,\n which one would you"
                  + " like to Cartographer format?");
                Integer in = Integer.parseInt(input);
                
                cartographerFormat(new File(String.format("res/scriptOffsets/%d%s", in, ".txt")));
            }
        }*/
        
        JPanel startPanel = new JPanel();
        String[] options = {"Seek scrolling scripts (slow)", "Seek magic description script offsets", "Cartographer format", "Seek tileset loading offsets"};
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
                                seekOffsets(PRE_SCRIPT_BYTES); break;
                            case 1:
                                seekOffsetsMagic(); break;
                            case 2:
                                String input = JOptionPane.showInputDialog(null, 
                                "Assuming you've already made a txt file in the"
                              + " \"scriptOffsets\" folder labeled with a number,\n which one would you"
                              + " like to Cartographer format?");
                                Integer in = Integer.parseInt(input);

                                cartographerFormat(new File(String.format("res/scriptOffsets/%d%s", in, ".txt")));
                                break;
                            case 3:
                                seekOffsetsMapLoad(); break;
                        }
                    } catch (IOException e) {
                        System.out.println("Error instantiating button");
                    }
                }
            });
            button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            button.setActionCommand(Integer.toString(buttonID));
            startPanel.add(button);
            buttonID++;
        }
        
        JFrame frame = new JFrame("Script offset frame - pick an option!");
        
        frame.getContentPane().add(startPanel); //Implicit drawing order here
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        frame.setVisible(true);
    }
    
    public void seekOffsets(byte[] prefixToFind) {
        try {
            //Iterate through every single byte excluding the very last ones
            for (int i = 0; i < (romAccess.length() - prefixToFind.length); i++) {
                romAccess.seek(i); //Set reading point @ byte
                for (int j = 0; j < prefixToFind.length; j++) {
                    if (romAccess.readByte() != prefixToFind[j]) break;
                    //Break out of INNER loop if offset is no good
                    
                    if (j == prefixToFind.length - 1) { //If ALL bytes match
                        int scriptOffset = i + prefixToFind.length;
                        System.out.println(Integer.toHexString(scriptOffset));
                        offsetList.add(Integer.toString(scriptOffset));
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ScriptOffsetFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void seekOffsetsMagic() {
        try {
            //Iterate through every byte from start to finish of magic
            for (int i = MAGIC_SCRIPT_START; i < MAGIC_SCRIPT_END; i+= 4)
            {
                romAccess.seek(i);
                byte[] temp = new byte[4]; //read bytes in increments of 4
                romAccess.readFully(temp);
                
                if (temp[3] == 0x08) //If bytearray contains a pointer
                {
                    int scriptOffset = toRelOffset(temp);
                    System.out.println(Integer.toHexString(scriptOffset & 0xFFFFFF)); //AND removes leading 08
                    offsetList.add(Integer.toString(scriptOffset));
                }

            }
        } catch (IOException ex) {
                Logger.getLogger(ScriptOffsetFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void seekOffsetsMapLoad() throws IOException
    {
        try {
            //Iterate through every single byte excluding the very last ones
            for (int i = 0; i < 0x100000; i+= 4) {
                romAccess.seek(i); //Set reading point @ byte
                //Checking code is here
                int arg1 = romAccess.readInt();
                if (arg1 == 0x09000000 || arg1 == 0x09000100 || arg1 == 0x09000200)
                {
                    String loc = Long.toHexString(romAccess.getFilePointer() - 4);
                    int arg1a = (arg1 & 0x00000F00) >> 8; //i think 0 = tileset, 1 = map, 2 = palette
                    int layer = romAccess.readByte();
                    if (layer == 0 || layer == 1 || layer == 2 || layer == 3)
                    {
                        romAccess.skipBytes(3);
                    
                        byte[] offsetBytes = new byte[4];
                        romAccess.readFully(offsetBytes);
                        int offset = toRelOffset(offsetBytes);

                        if (option == KPRomAccess.JPN) {
                            offset += 0x138C54;
                        } else if (option == KPRomAccess.CHN) {
                            offset += 0x138E1C;
                        }
                        String offsetStr = Integer.toHexString(offset);

                        byte[] sizeBytes = new byte[4];
                        romAccess.readFully(sizeBytes);
                        int size = toRelOffset(sizeBytes);
                        String sizeStr = Integer.toHexString(size);

                        System.out.println(loc + "\t" + arg1a + "\t" + layer + "\t" + offsetStr + "\t" + sizeStr);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ScriptOffsetFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Done!");
    }
    
    public int toRelOffset(byte[] bytes) {
        //flips bytes and concatenates them in that reversed order
        return ( bytes[0] & 0xFF |
                 ((bytes[1] << 8) & 0xFF00) |
                 ((bytes[2] << 16) & 0xFF0000) |
                 ((bytes[3] << 24) & 0xFF000000)
                 );
    }

    private void cartographerFormat(File file) {
        try (Scanner txtScan = new Scanner(file);){
            //Need a unique name, script start and script end offsets for each script
            int scriptCount = 1;
            String scriptStartOffset;
            String scriptEndOffset = null;
            
            //Iterate for however many lines txt file has
            while (txtScan.hasNextLine())
            {
                scriptStartOffset = (txtScan.nextLine().toUpperCase());
                //Determining the end offset!!
                int startOffsetDec = Integer.parseInt(scriptStartOffset, 16);
                
                romAccess.seek(startOffsetDec); //seek outside of loop to save
                //computing time
                
                //Iterate through every single byte until 2 consecutive 00's are reached
                for (int i = startOffsetDec; i < romAccess.length(); i++)
                {
                    if (romAccess.readByte() == 0) {
                        if (romAccess.readByte() == 0) {
                            //Breaking out of the loop
                            int endOffsetDec = (int) romAccess.getFilePointer(); // - 2; //omit 2 00's
                            scriptEndOffset = Integer.toHexString(endOffsetDec).toUpperCase();
                            //Putting the offset into hex.
                            break;
                        }
                    }
                }
                
                //if (scriptCount == 1) {
                    //Spits out the text that can be inserted into cartographer
                    printCartographerBlock(scriptCount, scriptStartOffset, scriptEndOffset);
                //}
                
                scriptCount++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScriptOffsetFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ScriptOffsetFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void printCartographerBlock(int count, String hexStart, String hexEnd) {
        System.out.println(
          "#BLOCK NAME:\t\t\tMagic Script "+(count-1)+
          "\n#TYPE:\t\t\t\tNORMAL"+
          "\n#METHOD:\t\t\tRAW"+
          "\n#SCRIPT START:\t\t\t$"+hexStart+
          "\n#SCRIPT STOP:\t\t\t$"+hexEnd+
          "\n#TABLE:\t\t\t\tdefault.tbl"+    //may need to change table depending on japanese/chinese
          "\n#COMMENTS:\t\t\tYes"+
          "\n#END BLOCK\n");
    }
}
