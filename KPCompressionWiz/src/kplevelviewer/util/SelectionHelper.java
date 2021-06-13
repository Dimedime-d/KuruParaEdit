package kplevelviewer.util;

import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import kplevelviewer.OffsetGroup.DecompActor;

public class SelectionHelper {
    
    public static JComboBox<String> enumsToComboBox(DecompActor[] actors)
    {
        String[] choices = new String[actors.length];
        int index = 0;
        for (DecompActor a : actors)
        {
            choices[index++] = a.toString();
        }
        return new JComboBox<>(choices);
    }
    
    public static void askForSelection(JComboBox<String> cb, JButton btnOK)
    {
        JFrame askFrame = new JFrame("Pick one!");
        JPanel panel = new JPanel();
        askFrame.add(panel);

        JLabel lbl = new JLabel("Select one of the possible choices and click OK");
        lbl.setVisible(true);

        panel.add(lbl);
        
        cb.setMaximumRowCount(20);
        cb.setVisible(true);
        panel.add(cb);

        panel.add(btnOK);
        
        askFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        askFrame.pack();
        askFrame.setLocationRelativeTo(null);
        askFrame.setLayout(null);
        askFrame.setVisible(true);
    }
}
