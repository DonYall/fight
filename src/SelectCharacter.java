import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class SelectCharacter extends JFrame {
    private JPanel mainPanel = new JPanel(new FlowLayout());
    private JLabel characterList = new JLabel("Available characters: ryanpog, andrew, mk");
    private JTextField mainTextField = new JTextField("eg: ryanpog/andrew");
    private JButton okButton = new JButton("OK");

    public SelectCharacter() {
        mainPanel.add(characterList);
        mainPanel.add(mainTextField);
        mainPanel.add(okButton);
        okButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) { 
                try {
                    new App(mainTextField.getText().split("/")[0], mainTextField.getText().split("/")[1], 1.5);
                    dispose();
                } catch (IOException e) {
                    e.printStackTrace();
                }               
            }
        });
        add(mainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 100);
        setVisible(true);
    }

    public static void main(String[] args) {
        new SelectCharacter();
    }
}