package kpdatamanipulator.ops;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class AngleFrame extends JFrame implements ActionListener{

    /**
     *
     */
    private static final long serialVersionUID = 3601564743312117633L;

    private RandomAccessFile romAccess;
    
    private static String title = "Angle Parser";
    
    private final int MOV_OFFSET = 0xADCF0;
    
    private JLabel angleLabel = new JLabel("Angle (hex):");
    private final JLabel xLabel = new JLabel("X: ");
    private final JLabel yLabel = new JLabel("Y: ");
    private final JButton calcButton = new JButton("Calculate!");
    private JTextField angleField = new JTextField(6);
    private JTextField xField = new JTextField(6);
    private JTextField yField = new JTextField(6);
    
    //tech
    private final JLabel degLabel = new JLabel("Degree equiv.: ");
    private final JTextField degField = new JTextField(3);
    private final JLabel offsetLabel = new JLabel("Offset (hex): ");
    private final JTextField offsetField = new JTextField(3);
    
    private JPanel angleDisplayPanel = new JPanel();
    private JPanel coordPanel = new JPanel();
    private JPanel techPanel = new JPanel();
    
    public AngleFrame(RandomAccessFile ra) {
        super(title);
        this.romAccess = ra;
        
        createFrame();
    }

    public void createFrame() {
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        
        addTitleText();
        addAngleDispComponent();
        addCalcBtn();
        addCoordPanel();
        addTechPanel();
        
        pack();
        setResizable(false);
        //Set up frame
        setLocationRelativeTo(null); //center window on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //allow x out
        setVisible(true); //make frame visible
    }
    
    public void addTitleText() {
        JLabel title1 = new JLabel("Angle Parser");
        JLabel title2 = new JLabel("for Kururin Paradise");
        title1.setAlignmentX(Component.CENTER_ALIGNMENT);
        title2.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        this.add(title1);
        this.add(title2);
    }
    
    public void addAngleDispComponent() {
        angleDisplayPanel.add(angleLabel);
        angleDisplayPanel.add(angleField);
        //Document filter
        PlainDocument doc = (PlainDocument)angleField.getDocument();
        doc.setDocumentFilter(new IntFilter());
        
        this.add(angleDisplayPanel);
    }
    
    public void addCalcBtn() {
        //insert stuff for calcButton
        calcButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calcButton.addActionListener(this);
        this.add(calcButton);
    }
    
    public void addCoordPanel() {
        //manipulating so that x/y labels appear on top of text Fields
        JPanel xPanel = new JPanel();
        xPanel.setLayout(new BoxLayout(xPanel, BoxLayout.Y_AXIS));
        xPanel.add(xLabel);
        xField.setEditable(false);
        xPanel.add(xField);
        
        JPanel yPanel = new JPanel();
        yPanel.setLayout(new BoxLayout(yPanel, BoxLayout.Y_AXIS));
        yPanel.add(yLabel);
        yField.setEditable(false);
        yPanel.add(yField);

        coordPanel.add(xPanel);
        coordPanel.add(yPanel);
        
        this.add(coordPanel);
    }
    
    public void addTechPanel() {
        //Tech panel
        JPanel degPanel = new JPanel();
        degPanel.setLayout(new BoxLayout(degPanel, BoxLayout.Y_AXIS));
        degPanel.add(degLabel);
        degField.setEditable(false);
        degPanel.add(degField);

        JPanel offsetPanel = new JPanel();
        offsetPanel.setLayout(new BoxLayout(offsetPanel, BoxLayout.Y_AXIS));
        offsetPanel.add(offsetLabel);
        offsetField.setEditable(false);
        offsetPanel.add(offsetField);
        
        techPanel.add(degPanel);
        techPanel.add(offsetPanel);

        this.add(techPanel);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        //Trims int input
        int angle = 0;
        try {
            angle = Integer.parseInt(angleField.getText(), 16); //Parse hex string as int
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Error parsing hex string as int");
        }
        
        angle = (angle << 0x18) >>> 0x18; //leaves only 8 least sig bits
        angleField.setText(Integer.toHexString(angle));
        
        //Returns approx. degree equivalent
        double degree = (angle*360)/256.0;
        DecimalFormat onePlace = new DecimalFormat("#.#");
        degField.setText(onePlace.format(degree));
        //set Offset field
        int offset = angle << 0x01;
        offsetField.setText(Integer.toHexString(offset));
        
        //Lastly, read the rom for x and y!!
        //x first
        int finalXOffset = MOV_OFFSET + offset;
        byte[] bytes = new byte[2];
        try {
            romAccess.seek(finalXOffset);
            romAccess.readFully(bytes);
            //Reads LENGTH of bytes array starting at offset above
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error reading rom");
        }
        short xShort = (short) ((bytes[1] << 0x08) + bytes[0]); //Concatenating bytes, Little endian
        int xInt;
        if (angle <= 0x80) {
            xInt = (int) (xShort & 0xFF); //correction for positive angles
        } else {
            if ((xShort & 0x100) == 0) {
                xInt = (xShort | 0x100); //correction for negative angles
            } else {
                xInt = xShort;
            }
        }
        String xHex = Integer.toHexString(xInt);
        //Custom Zero fill
        while (xHex.length() < 8) {
            String zero = "0";
            xHex = (zero + xHex);
        }
        xField.setText(xHex.toUpperCase());
    }
    
    public class IntFilter extends DocumentFilter //allows only int inputs!!
    {
        @Override
        public void insertString(FilterBypass fb, int offset, String string,
            AttributeSet attr) throws BadLocationException { //method to place String in textfield
            string = string.toUpperCase();
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            //method to place text in a dummy doc?
            sb.append(doc.getText(0, doc.getLength()));
            sb.insert(offset, string);
            
            if (intTest(sb.toString()))
            {
                super.insertString(fb, offset, string, attr);
            } else 
                {
                   warn();
                }
        }
        
        private boolean intTest(String str)
        {
            try {
                Integer.parseInt(str, 16);
                return true;
            } catch (NumberFormatException e) {
               if (str.equals (""))
               return true;
               else
               return false;
            }
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
            AttributeSet attrs) throws BadLocationException {
            text = text.toUpperCase();
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.replace(offset, offset + length, text);

            if (intTest(sb.toString())) {
               super.replace(fb, offset, length, text, attrs);
            } else {
               warn();
            }

        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.delete(offset, offset + length);

            if (intTest(sb.toString())) {
               super.remove(fb, offset, length);
            } else {
               warn();
            }
        }
        
        public void warn() {
            JOptionPane.showMessageDialog(null, "Input angles in numbers only.");
        }
    }
    
    
}
