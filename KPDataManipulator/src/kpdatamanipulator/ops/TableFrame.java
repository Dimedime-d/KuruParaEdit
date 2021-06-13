package kpdatamanipulator.ops;

//import java.awt.Color;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
//import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import kpdatamanipulator.select.KPRomAccess;

public class TableFrame extends JFrame {
    
    /**
     *
     */
    private static final long serialVersionUID = -597651445480527876L;
    private RandomAccessFile romAccess;
    private int startAdr, numEntries, numSubentries, subentryLength;
    private String[] finalSubentryStrings; //final array to read data to
    private int subentryIndex = 0; //tracks current index in final subentries array
    
    public TableFrame(KPRomAccess roms) {
        super();
        int option = KPRomAccess.pickRomLanguage();
        if (option == KPRomAccess.JPN) {
            romAccess = roms.getRomAccess();
        } else {
            romAccess = roms.getRomAccessChinese();
        }
        
        startAdr = askHex("start address");
        numEntries = askDec("# of entries");
        numSubentries = askDec("# of subentries");
        subentryLength = askHex("subentry length in bytes");
        
        readRom();
        generateTable();
    }
    
    private int askHex(String msg) {
        String uInput = JOptionPane.showInputDialog("Enter a value for "+msg
            +" in hex. (No prefix)");
        return Integer.parseInt(uInput, 16);
    }
    
    private int askDec(String msg) {
        String uInput = JOptionPane.showInputDialog("Enter a value for "+msg
            +" in base 10 decimal.");
        return Integer.parseInt(uInput);
    }
    
    private void readRom() {
        try {
            romAccess.seek(startAdr); //start reading @ startAdress
            
            finalSubentryStrings = new String[numSubentries*numEntries];
            //Iterate through however many entries specified
            for (int i = 0; i < numEntries; i++)
            {
                //Iterate through one entry's worth of subentries
                for (int j = 0; j < numSubentries; j++) 
                {
                    //Read ONE sub-entry
                    byte[] temp = new byte[subentryLength];
                    romAccess.readFully(temp); //reading bytes into temp array

                    //Process unmodified bytearray, flipping it
                    int subentry = (temp[0] & 0xFF) 
                                 + ((temp[1] << 8) & 0xFF00) 
                                 + ((temp[2] << 16) & 0xFF0000) 
                                 + ((temp[3] << 24) & 0xFF000000);
                    //Converting subentry into a string that can fcn as a label
                    String subentryString = String.format("%08x",subentry).toUpperCase();

                    finalSubentryStrings[subentryIndex++] = subentryString;

                    //output test
                    System.out.println(subentryString);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(TableFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void generateTable() { //Makes a frame, puts all the entries in it via a JFrame!
        //Overall layout is 2x2: UR are increments (0x00, 0x04, etc.), BL are offsets, BR are table entries
        
        //temp
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        
        //JPanel topRPanel = createTopRPanel();
        JPanel tPanel = createTablePanel();
        JScrollPane sp = new JScrollPane(tPanel);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        //add(topRPanel);
        //add(tPanel);
        
        getContentPane().add(sp);
        
        pack();
        setResizable(true);
        //Set up frame
        setLocationRelativeTo(null); //center window on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //allow x out
        setVisible(true); //make frame visible
    }
    
    private JPanel createTablePanel() {
        GridLayout tableGridLayout = new GridLayout(numEntries,numSubentries);
        tableGridLayout.setHgap(5);
        tableGridLayout.setVgap(3);
        JPanel temp = new JPanel (tableGridLayout); //rows, then cols
        
        String allSubentries = "";
        int currentEntryOffset = 0;
        int currentSubOffset = 0;
        
        //Components are added left to right, top to bottom.
        for (int y = 0; y < numEntries; y++)
        {
            //Adds increments on top
            if (y == 0)
            {   //tab @ beginning
                allSubentries = allSubentries + "\t";
                
                //0x04, 0x08, etc.
                for (int x = 0; x < numSubentries; x++)
                {
                    String increment = (("0x" + 
                    (Integer.toHexString(currentSubOffset)).toUpperCase()));
                    
                    allSubentries = allSubentries + increment;  //append increment
                    
                    if (x != numSubentries - 1) //Tab all but last
                    {
                        allSubentries = allSubentries + "\t";
                    }
                    
                    currentSubOffset += 4;
                }
                allSubentries = allSubentries + "\n";
            }
            for (int x = 0; x < numSubentries; x++)
            {
                if (x == 0) {
                //Row headers are offsets (Start + entry increment
                String rowHeader = (Integer.toHexString(startAdr)).toUpperCase() +
                        ("+0x"+(Integer.toHexString(currentEntryOffset).toUpperCase()));
                currentEntryOffset += (numSubentries*subentryLength);
                allSubentries = allSubentries + rowHeader +"\t";
                }
                
                //Actual subentry adding below
                String subentryString = finalSubentryStrings[y*numSubentries+x];
                allSubentries = allSubentries + subentryString; //concatenates each subentry string
                if (x != (numSubentries - 1))
                    allSubentries = allSubentries + "\t"; //tabs after each string except last one in row
                
                //Below: Makes 1 text field for each subentry
                /*JTextField tempField = new JTextField(finalSubentryStrings[y*numSubentries + x]);
                tempField.setBackground(Color.WHITE);
                temp.add(tempField);*/
            }
                allSubentries = allSubentries + "\n";
        }
        JTextArea bigTableArea = new JTextArea(allSubentries);
        temp.add(bigTableArea);
        
        return temp;
    }
    /*
    private JPanel createTopRPanel() {
        GridLayout incLayout = new GridLayout (1, numSubentries);
        incLayout.setHgap(5);
        
        JPanel temp = new JPanel(incLayout);
        int currentSubOffset = 0;
        for (int i = 0; i < numSubentries; i++)
        {
            JLabel tempLabel = new JLabel(("0x" + 
                    Integer.toHexString(currentSubOffset)).toUpperCase());
            tempLabel.setForeground(Color.BLUE);
            temp.add(tempLabel); //above table entry data
            currentSubOffset += 4;
        }
        return temp;
    }
    */
}
