package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class FileSelector {

    private final JButton BUTTON;

    public FileSelector(String buttonName) {
        BUTTON = new JButton(buttonName);
        BUTTON.addActionListener(this::actionPerformed);
    }

    private void actionPerformed(ActionEvent event) {
        if (event.getSource() != BUTTON) {
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        int response = fileChooser.showOpenDialog(null);
        if (response != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fileChooser.getSelectedFile();
        System.out.println(file.getAbsolutePath());
    }

    public Component getComponent() {
        return BUTTON;
    }

}
