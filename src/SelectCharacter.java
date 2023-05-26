import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class SelectCharacter extends JFrame {
    private final List<String> availableCharacters = Arrays.asList("ryanpog", "andrew", "mk", "don", "deev", "steph", "joseph", "ethan", "kairo", "katie");
    private ArrayList<String> players = new ArrayList<>();
    private JPanel mainPanel;

    public SelectCharacter() throws IOException {
        ArrayList<Image> charImages = new ArrayList<>();
        for (String character : availableCharacters) {
            charImages.add(ImageIO.read(getClass().getResource(character + ".png")).getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH));
        }
        mainPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                for (int i = 0; i < charImages.size(); i++) {
                    g.drawImage(charImages.get(i), i * 100, 0, this);
                }
            }
        };
        mainPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
            }
            @Override
            public void mouseEntered(MouseEvent arg0) {
            }
            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getX() > 0 && e.getX() < availableCharacters.size() * 100 && e.getY() > 0 && e.getY() < 100) {
                    players.add(availableCharacters.get((int) (e.getX() / 100)));
                    if (players.size() == 2) {
                        try {
                            dispose();
                            App a = new App(players.get(0), players.get(1));
                            a.connectToServer();
                            a.startReceivingMoves();
                        } catch (IOException | LineUnavailableException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        try {
                            charImages.set((int) (e.getX() / 100), ImageIO.read(getClass().getResource(availableCharacters.get((int) (e.getX() / 100)) + ".png")).getScaledInstance(110, 110, BufferedImage.SCALE_SMOOTH));
                            repaint();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }
            
        });
        add(mainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(availableCharacters.size() * 100, 100);
        setUndecorated(true);
        setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        new SelectCharacter();
    }
}