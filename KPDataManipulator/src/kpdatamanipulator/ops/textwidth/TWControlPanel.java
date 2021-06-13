//This is the class that "knows about" all the maps + image
package kpdatamanipulator.ops.textwidth;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import kpdatamanipulator.ops.textwidth.TWPanel.ImagePanel;

public class TWControlPanel extends JPanel
{
    /**
     *
     */
    private static final long serialVersionUID = 8578226420253165282L;

    public enum TextType
    {
        ALTFONT,
        ASCII;
        
        public HashMap<Character, BufferedImage> imageMap;
        public HashMap<Character, Integer> yOffsetMap;
    }
    
    private TextType currentTextType = TextType.ALTFONT;

    private BufferedImage txtImage;
    
    private int textX = 0, textY = 0;
    private int xDefault = 0x0F, yDefault = 0x0F, centerXDefault = TWPanel.WIDTH1/2; //STARTING x and y for text
    private boolean centerAlign = false;
    private boolean showDialogueOverlay = false;
    
    private final int MAX_ROWS = 10;
    private final int DEFAULT_SPACING = 0x0E;
    
    private JTextField xField = new JTextField(2);
    private JTextField yField = new JTextField(2);
    private JTextField centerXField = new JTextField(2);
    private JTextField lineSpacingField = new JTextField(2);
    
    private final JTextArea txtEditArea = new JTextArea(6, 35);
    
    private ImagePanel imgPanel;
    
    //Constructor
    //Needs to know it has a text image (alias) + image panel to update
    public TWControlPanel(BufferedImage img, ImagePanel imgPanel) throws IOException
    {
        super();
        
        txtImage = img; //aliases
        this.imgPanel = imgPanel;
        clearImage();
        resetCoords();
        //Fill maps
        createCharMaps();
        
        //  Add text edit panel containing label + txtField
        JPanel txtEditPanel = createTxtEditPanel();
        //  Add moar Components for starting x, y, width, center, etc.
        JPanel txtDataPanel = createTxtDataPanel();
        
        add(txtEditPanel);
        add(txtDataPanel);
        
        //setBackground(Color.GRAY); //Black rn
    }
    
    private void createCharMaps() throws IOException
    {   
        //AltFont
        {
            HashMap<Character, BufferedImage> imageMap = new HashMap<>();
            HashMap<Character, Integer> yOffsetMap = new HashMap<>();
            //First, all the normal characters
            //Note, all text is aligned to top left
            //Local fields first
            
            File[] regular = new File("res/altfontimg").listFiles();
            //TextUtil.fillRegularCharMaps(regular, imageMap, yOffsetMap);
            TextUtil.parseFiles(regular, imageMap, yOffsetMap, false, TextType.ALTFONT);
            //Then all the uppercase chars
            File[] upCase = new File("res/altfontimg/caps").listFiles();
            TextUtil.parseFiles(upCase, imageMap, yOffsetMap, false, TextType.ALTFONT);
            //Then all the special chars
            File[] special = new File("res/altfontimg/spec").listFiles();
            TextUtil.parseFiles(special, imageMap, yOffsetMap, true, TextType.ALTFONT);
            TextType.ALTFONT.imageMap = imageMap;
            TextType.ALTFONT.yOffsetMap = yOffsetMap;
        }
        //ascii
        {
            HashMap<Character, BufferedImage> imageMap = new HashMap<>();
            HashMap<Character, Integer> yOffsetMap = new HashMap<>();
            
            File[] regular = new File("res/asciiimg").listFiles();
            TextUtil.parseFiles(regular, imageMap, yOffsetMap, false, TextType.ASCII);
            File[] upCase = new File("res/asciiimg/caps").listFiles();
            TextUtil.parseFiles(upCase, imageMap, yOffsetMap, false, TextType.ASCII);
            File[] special = new File("res/asciiimg/spec").listFiles();
            TextUtil.parseFiles(special, imageMap, yOffsetMap, true, TextType.ASCII);
            
            TextType.ASCII.imageMap = imageMap;
            TextType.ASCII.yOffsetMap = yOffsetMap;
        }
    }
    
    private JPanel createTxtEditPanel()
    {
        JPanel temp = new JPanel();
        temp.add(new JLabel("Text to show: "));
        
        JScrollPane txtEditPane = new JScrollPane(txtEditArea);
        txtEditArea.setLineWrap(false);
        addChangeListener(txtEditArea, e -> updateImgPanel());  

        temp.add(txtEditPane);
        return temp;
    }
    
    private JPanel createTxtDataPanel()
    {
        JPanel temp = new JPanel();
        temp.setLayout(new BoxLayout(temp, BoxLayout.Y_AXIS));
            JPanel coordPanel = new JPanel(); //x/y of top-left coord
                coordPanel.setLayout(new BoxLayout(coordPanel, BoxLayout.X_AXIS));
                coordPanel.add(new JLabel("X: "));
                addChangeListener(xField, e -> updateXDefault());
                coordPanel.add(xField);
                coordPanel.add(new JLabel("Y: "));
                addChangeListener(yField, e -> updateYDefault());
                coordPanel.add(yField);
                updateCoordFields();
        temp.add(coordPanel);

        //Font buttons
            JRadioButton altFontBtn = new JRadioButton("Altfont");
            altFontBtn.addActionListener(e -> {
                currentTextType = TextType.ALTFONT;
                updateImgPanel();
                });
            altFontBtn.setSelected(true);
			altFontBtn.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            JRadioButton asciiBtn = new JRadioButton("ASCII");
            asciiBtn.addActionListener(e -> {
                currentTextType = TextType.ASCII;
                updateImgPanel();
                });
			asciiBtn.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            ButtonGroup fontBtns = new ButtonGroup();
            fontBtns.add(altFontBtn);
            fontBtns.add(asciiBtn);
        JPanel btnPanel = new JPanel();
        btnPanel.add(altFontBtn);
        btnPanel.add(asciiBtn);
        temp.add(btnPanel);
            
            JPanel coordCenterPanel = new JPanel();
            coordCenterPanel.setLayout(new BoxLayout(coordCenterPanel, BoxLayout.X_AXIS));
            coordCenterPanel.add(new JLabel("Center X:"));
            centerXField.setEditable(centerAlign);
            centerXField.setText(Integer.toHexString(centerXDefault));
            addChangeListener(centerXField, e -> {
                updateCenterX();
                    });
            coordCenterPanel.add(centerXField);

        temp.add(coordCenterPanel);
        
            JPanel lineSpacingPanel = new JPanel();
            lineSpacingPanel.setLayout(new BoxLayout(lineSpacingPanel, BoxLayout.X_AXIS));
            lineSpacingPanel.add(new JLabel("Line Spacing"));
            lineSpacingField.setText(Integer.toHexString(DEFAULT_SPACING));
            addChangeListener(lineSpacingField, e -> {
                updateLineSpacing();
                    });
            lineSpacingPanel.add(lineSpacingField);
        temp.add(lineSpacingPanel);
        
            JCheckBox centerCheckBox = new JCheckBox("Center align");
                centerCheckBox.setSelected(centerAlign);
                centerCheckBox.addItemListener(e -> {
                    centerAlign = !centerAlign;
                    centerXField.setEditable(centerAlign);
                    xField.setEditable(!centerAlign);
                    updateImgPanel();});
                centerCheckBox.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        temp.add(centerCheckBox);
        
            JCheckBox dialogueCheckBox = new JCheckBox("Dialogue Overlay");
                dialogueCheckBox.setSelected(showDialogueOverlay);
                dialogueCheckBox.addItemListener(e -> {
                    showDialogueOverlay = !showDialogueOverlay;
                    //Some presets to draw text in dialogue box correctly
                    centerCheckBox.setSelected(true);
                    asciiBtn.setSelected(true);
                    currentTextType = TextType.ASCII;
                    centerXField.setText("90"); //144 decimal
                    yField.setText("85"); //133 decimal
                    updateImgPanel();
                });
                dialogueCheckBox.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            temp.add(dialogueCheckBox);

        return temp;
    }
    

    /**
     * Installs a listener to receive notification when the text of any
     * {@code JTextComponent} is changed. Internally, it installs a
     * {@link DocumentListener} on the text component's {@link Document},
     * and a {@link PropertyChangeListener} on the text component to detect
     * if the {@code Document} itself is replaced.
     * 
     * @param text any text component, such as a {@link JTextField}
     *        or {@link JTextArea}
     * @param changeListener a listener to receive {@link ChangeEvent}s
     *        when the text is changed; the source object for the events
     *        will be the text component
     * @throws NullPointerException if either parameter is null
     */
    public static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(changeListener);
        DocumentListener dl = new DocumentListener() {
            private int lastChange = 0, lastNotifiedChange = 0;

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lastChange++;
                SwingUtilities.invokeLater(() -> {
                    if (lastNotifiedChange != lastChange) {
                        lastNotifiedChange = lastChange;
                        changeListener.stateChanged(new ChangeEvent(text));
                    }
                });
            }
        };
        text.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
            Document d1 = (Document)e.getOldValue();
            Document d2 = (Document)e.getNewValue();
            if (d1 != null) d1.removeDocumentListener(dl);
            if (d2 != null) d2.addDocumentListener(dl);
            dl.changedUpdate(null);
        });
        Document d = text.getDocument();
        if (d != null) d.addDocumentListener(dl);
    }
    
    private void clearImage()
    {
        Graphics g = txtImage.createGraphics();
        g.setColor(new Color(TextUtil.BG_COLOR));
        g.fillRect(0,0,TWPanel.WIDTH1,TWPanel.HEIGHT1);
        g.dispose();
    }
    
    private void resetCoords()
    {
        textX = xDefault;
        textY = yDefault;
    }

    private void updateCoordFields()
    {
        SwingUtilities.invokeLater(() -> 
        {
        xField.setText(Integer.toHexString(xDefault).toUpperCase());
        yField.setText(Integer.toHexString(yDefault).toUpperCase());    
        });
    }
    
    private void updateXDefault() //Called upon editing x field
    {
        if (xField.getText().length() > 2) xField.setText("FF"); //no need for large #'s
        try
        {
            int newX = Integer.parseInt(xField.getText(), 16);
            //Check bounds of newX
                if (newX > TWPanel.WIDTH1) newX = TWPanel.WIDTH1;
            xDefault = newX;
            updateImgPanel();
        } catch (NumberFormatException e)
        {   //Invalid input resets xField
            if (xField.getText().length() > 0)
            xField.setText(Integer.toHexString(xDefault).toUpperCase());
        }
    }
    
    private void updateYDefault() //Called upon editing y field
    {
        if (yField.getText().length() > 2) yField.setText("FF");
        try
        {
            int newY = Integer.parseInt(yField.getText(), 16);
                if (newY > TWPanel.HEIGHT1) newY = TWPanel.HEIGHT1;
            yDefault = newY;
            updateImgPanel();
        } catch (NumberFormatException e)
        {
            if (yField.getText().length() > 0)
            yField.setText(Integer.toHexString(yDefault).toUpperCase());
        }
    }
	
    private void updateCenterX()
    {
        if (centerXField.getText().length() > 2) centerXField.setText("FF");
        try
        {
            int newCenterX = Integer.parseInt(xField.getText(), 16);
            updateImgPanel();
        } catch (NumberFormatException e)
        {
            centerXField.setText(Integer.toHexString(centerXDefault).toUpperCase());
        }
    }
    
    private void updateLineSpacing()
    {
        if (lineSpacingField.getText().length() > 2) lineSpacingField.setText("E");
        try
        {
            int newLineSpacing = Integer.parseInt(lineSpacingField.getText(), 16);
            updateImgPanel();
        } catch (NumberFormatException e)
        {
            lineSpacingField.setText("E");
        }
    }
    
    //go here whenever text is typed in
    public void updateImgPanel() //Update text display on left
    {
        clearImage(); //Wipes previous text image to draw new text
        resetCoords(); //Restore textX and textY to default values
        limitRows();  //Truncate text to n rows before iterating through characters
        if (showDialogueOverlay) drawDialogueOverlay();
        //iterate through each character in string and show image!
        drawCharsToImage(txtEditArea.getText(), centerAlign);

        imgPanel.repaint(); //Actual method that redraws image
    }
    
    private void drawDialogueOverlay()
    {
        try {
            Graphics g = txtImage.createGraphics();
            g.drawImage(ImageIO.read(new File("res/dialogueoverlay.png")),0,0,null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //rewrite this method to work with multiple lines!!
    private void drawCharsToImage(String text, boolean alignCenter)
    {
        String[] lines = text.split("\n");
        for (String line : lines)
        {   //Need to iterate each line twice: 1 to calc x-position if centered
            //1 to draw each character
            if (alignCenter)
            {
                changeCenteredX(line);
            }
            
            for (char c : line.toCharArray())
            {
                BufferedImage currentImg = currentTextType.imageMap.get(c);
                //currently: use space if can't find image for character
                int yOffset = (currentImg != null) ? currentTextType.yOffsetMap.get(c) : 0;
                if (currentImg == null) currentImg = currentTextType.imageMap.get(' ');
                int width = currentImg.getWidth();
                
                //Only draw characters in-bounds
                if (textX >= 0 && textX <= TWPanel.WIDTH1
                    && textY >= 0 && textY <= TWPanel.HEIGHT1)
                {
                    Graphics g = txtImage.createGraphics();
                    g.drawImage(currentImg, textX, textY + yOffset, null);
                    g.dispose();

                    textX += width + 1;
                }  
            }
            //After iterating every character in line, move on to next one
            textY += Integer.parseInt(lineSpacingField.getText(), 16);
            textX = xDefault;
        }
    }
    
    private void changeCenteredX(String line)
    {
        int totalLineWidth = 0;
            for (char c : line.toCharArray())
            {
                //read each character
                BufferedImage charImg = currentTextType.imageMap.get(c);
                if (charImg == null) charImg = currentTextType.imageMap.get(' ');

                //Add width of each character
                totalLineWidth += charImg.getWidth() + 1;   //includes space @ end
            }
            totalLineWidth--;   //no space at end

            //Mutate starting x coordinate
            xDefault = (Integer.parseInt(centerXField.getText(), 16)*2 - totalLineWidth)/2 - 1;
            //Account that coords start at 0
            if (xDefault < 0) xDefault = 0;
            textX = xDefault;

            SwingUtilities.invokeLater(() -> {
            xField.setText(Integer.toHexString(xDefault).toUpperCase());
            });
    }
    
    private void limitRows()
    {
        String s = txtEditArea.getText();
            //Keep rows to 5 max
        if (txtEditArea.getLineCount() > MAX_ROWS)
        {
            //Truncate text area to 5 lines
            int newLineCount = 0, index = 0;
            while (newLineCount < MAX_ROWS) //Count (MAX_ROWS) newline characters
            {
                //Ensures that, if "\n" is 1st character, that it gets counted
                int newIndex = s.indexOf("\n", (index == 0 && newLineCount == 0) ? index : index + 1);
                index = newIndex;
                newLineCount++;
            }

            final int lastIndex = index;

            Runnable truncate = new Runnable()
            {
                String text = s;
                int fifthIndex = lastIndex;

                @Override
                public void run()
                {
                    txtEditArea.setText(s.substring(0,lastIndex));
                }
            };
            SwingUtilities.invokeLater(truncate);
        }

    }

}
