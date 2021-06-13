//Panel with a buncha fields to manipulate image
package kpdatamanipulator.ops.imgobjhex;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import kpdatamanipulator.ops.imgobjhex.IOHPanel.ImagePanel;

public class IOHControlPanel extends JPanel
{
    /**
     *
     */
    private static final long serialVersionUID = 368541171956366255L;
    private BufferedImage panelImg, fullObjImg;
    private ImagePanel imgPanel;
    //private PaletteMap palette; 
    
    private JTextField xField = new JTextField(2);
    private JTextField yField = new JTextField(2);
    
    private int imgX = 0, imgY = 0;
    private int xParam1 = 0x94, yParam1 = 0x8C,
                xParam2 = 0x2c, yParam2 = 0x08;
    
    private final String[] PRESETS = {"Level", "World"};
    
    public IOHControlPanel(BufferedImage img, ImagePanel imgPanel)
    {
        super();
        
        panelImg = img; //aliases
        this.imgPanel = imgPanel;
        
        //Construct paletteMap //Maps each color to hex
        //palette = new PaletteMap();
        
        //Ask for file to read into image
         //Pick an image! (Read file into img)
        File file = pickFile();
        try 
        {
            fullObjImg = ImageIO.read(file);
        } catch (IOException ex) 
        {
            System.out.println("error reading image");
            Logger.getLogger(ImgObjHexGenFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Add components
        JPanel imgDataPanel = createImgDataPanel();
        
        add(imgDataPanel);
        
        updateImgPanel();
    }
    
    private File pickFile()
    {
        JFileChooser fc = new JFileChooser(); //Choose file
            fc.setDialogTitle("Pick image file");
            fc.setFileFilter(new FileFilter() 
                    { //Save Dialog shows ONLY excel files
                        @Override
                        public String getDescription()
                        {   return "Supported files (.png)";} //drop-down text

                        @Override
                        public boolean accept(File f) //choose whether to show file
                        {
                            if (f.isDirectory())
                                return true;
                            else
                            {
                                String filename = f.getName().toLowerCase();
                                return filename.endsWith(".png");
                            }
                        }
                    }   );
            int returnVal = fc.showOpenDialog(null);
        
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                return fc.getSelectedFile();
            } else {
                System.exit(0);
            }
        return null; //Should be unreachable
    }
    
    private JPanel createImgDataPanel()
    {
        JPanel temp = new JPanel();
        temp.setLayout(new BoxLayout(temp, BoxLayout.Y_AXIS));
            JPanel coordPanel = new JPanel(); //x/y of top-left coord
            coordPanel.setLayout(new BoxLayout(coordPanel, BoxLayout.X_AXIS));
            coordPanel.add(new JLabel("X: "));
            addChangeListener(xField, e -> updateImgX());
            coordPanel.add(xField);
            coordPanel.add(new JLabel("Y: "));
            addChangeListener(yField, e -> updateImgY());
            coordPanel.add(yField);
            temp.add(coordPanel);
            
            //JPanel for radio buttons
            JPanel presetPanel = new JPanel(new GridLayout(0,1));
            JRadioButton lvlTitleBtn = new JRadioButton(PRESETS[0]);
            lvlTitleBtn.setActionCommand(PRESETS[0]);
            lvlTitleBtn.setSelected(true);
            
            JRadioButton worldTitleBtn = new JRadioButton(PRESETS[1]);
            worldTitleBtn.setActionCommand(PRESETS[1]);
            worldTitleBtn.setSelected(true);
            
            //Add to button group
            ButtonGroup group = new ButtonGroup();
            group.add(lvlTitleBtn);
            group.add(worldTitleBtn);
            
            //Add action Listeners
            lvlTitleBtn.addActionListener(new RadioListener());
            worldTitleBtn.addActionListener(new RadioListener());
            
            //Add buttons to panel
            presetPanel.add(lvlTitleBtn);
            presetPanel.add(worldTitleBtn);
            temp.add(presetPanel);
            
        return temp;
    }
    
    private void clearImage()
    {
        Graphics g = panelImg.createGraphics();
        g.setColor(IOHPanel.PANEL_BG);
        g.fillRect(0,0,IOHPanel.WIDTH1,IOHPanel.HEIGHT1);
        g.dispose();
    }
    
    private void updateImgPanel()
    {
        clearImage();
        
        //Draw ObjImg to panelImg
        Graphics g = panelImg.createGraphics();
        g.drawImage(fullObjImg, imgX + xParam1 + xParam2, imgY + yParam1 + yParam2, null);
        g.dispose();
        
        imgPanel.repaint(); //Redraw
    }

    
    private void updateImgX()
    {
        if (xField.getText().length() > 2) xField.setText("FF"); //no need for large #'s
        try
        {
            int newX = Integer.parseInt(xField.getText(), 16);
            //Check bounds of newX
                //if (newX > IOHPanel.WIDTH1) newX = IOHPanel.WIDTH1;
            //Update imgX
            imgX = (newX > 0x7F) ? newX | 0xFFFFFF00 : newX;
            System.out.println(Integer.toHexString(imgX));
            updateImgPanel();
        } catch (NumberFormatException e)
        {   //Invalid input resets xField
            if (xField.getText().length() > 0)
            xField.setText(Integer.toHexString(0).toUpperCase());
        }
    }
    
    private void updateImgY() //Called upon editing y field
    {
        if (yField.getText().length() > 2) yField.setText("FF");
        try
        {
            int newY = Integer.parseInt(yField.getText(), 16);
                //if (newY > IOHPanel.HEIGHT1) newY = IOHPanel.HEIGHT1;
            imgY = (newY > 0x7F) ? newY | 0xFFFFFF00 : newY;
            System.out.println(Integer.toHexString(imgY));
            updateImgPanel();
        } catch (NumberFormatException e)
        {
            if (yField.getText().length() > 0)
            yField.setText(Integer.toHexString(0).toUpperCase());
        }
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
    
    private class RadioListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent ae) 
        {   //Listen radio inputs
            //Swap presets appropriately
            if (ae.getActionCommand().equals(PRESETS[0])) //Levels
            {
                xParam1 = 0x94; 
                yParam1 = 0x8C;
                xParam2 = 0x2C; 
                yParam2 = 0x08;
                updateImgPanel();
            } else {
                xParam1 = 0x7C;
                yParam1 = 0x08;
                xParam2 = 0x20;
                yParam2 = 0x08;
                updateImgPanel();
            }
        }
        
    }
}
