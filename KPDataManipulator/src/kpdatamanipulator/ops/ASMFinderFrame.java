//Uses a complete dump of the ARM7 THUMB of the rom, searches every
//single line for a particular ASM command

package kpdatamanipulator.ops;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ASMFinderFrame {

    private File rawASMFile;
    private String instructionToFind;
    
    private ArrayList<String> offsetHits = new ArrayList<>(); //Stores all offsets containing the instruction to find
    
    public ASMFinderFrame() {
        rawASMFile = new File("res/[Thumb] Everything Dump.txt");
        
        //uses a USER input to determine what to look for
        String response = JOptionPane.showInputDialog(null, 
                "Enter an ASM instruction to search for.");
        
        instructionToFind = response;
        
        findOffsets(instructionToFind);
    }
    
    public final void findOffsets(String instruc){
        try (Scanner asmScan = new Scanner(rawASMFile)){
            while (asmScan.hasNextLine()) { //Read through entire file line by line
                String currentLine = asmScan.nextLine();
                
                if (currentLine.contains(instruc)) {
                    //Record the offset where the instruction was found!
                    String currentOffsetHit = currentLine.substring(0,8); //1st 8 chars = offset
                    
                    System.out.println(currentOffsetHit);
                    offsetHits.add(currentOffsetHit);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ASMFinderFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
