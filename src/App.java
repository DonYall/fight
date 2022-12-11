import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.awt.image.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class App extends JFrame {
    private Character p1;
    private Character p2;
    private String p1Name;
    private String p2Name;
    private Map<Integer, Boolean> p1Keys = new HashMap<>();
    private int p1Movement = 0;
    private Map<Integer, Boolean> p2Keys = new HashMap<>();
    private int p2Movement = 0;
    private final int floor;
    private final JPanel gamePanel;
    private Timer repaintTimer;
    public App(String p1Name, String p2Name, double multiplier) throws IOException {
        p1Keys.put(1, false);
        p1Keys.put(-1, false);
        p2Keys.put(1, false);
        p2Keys.put(-1, false);

        floor = (int) ((7*multiplier) / 8);
        p1 = new Character(p1Name, 100, 200);
        p2 = new Character(p2Name, 600, 200);

        // Background image
        BufferedImage bufferedBG = ImageIO.read(getClass().getResource("bg.jpg"));
        Image bgImage = bufferedBG.getScaledInstance((int) (800*multiplier), (int) (400*multiplier), BufferedImage.SCALE_SMOOTH);
        
        // Character images
        BufferedImage bufferedRyanPog = ImageIO.read(getClass().getResource("ryanpog.png"));
        Image ryanpogIMG = bufferedRyanPog.getScaledInstance((int) (70*multiplier), (int) (70*multiplier), BufferedImage.SCALE_SMOOTH);

        BufferedImage bufferedMichelle = ImageIO.read(getClass().getResource("mk.png"));
        Image michelleIMG = bufferedMichelle.getScaledInstance((int) (70*multiplier), (int) (70*multiplier), BufferedImage.SCALE_SMOOTH);

        // Game Panel
        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(bgImage, 0, 0, this);
                g2d.drawImage(ryanpogIMG, (int) (p1.x*multiplier), (int) (p1.y*multiplier), null);
                g2d.drawImage(michelleIMG, (int) (p2.x*multiplier), (int) (p2.y*multiplier), null);
                g2d.dispose();
            }
        };
        InputMap im = gamePanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = gamePanel.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "pressed.a");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "pressed.d");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "released.a");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "released.d");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "pressed.w");

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "pressed.left");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "pressed.right");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "released.left");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "released.right");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "pressed.up");

        am.put("pressed.a", new MoveAction(1, -1, true));
        am.put("pressed.d", new MoveAction(1, +1, true));
        am.put("released.a", new MoveAction(1, -1, false));
        am.put("released.d", new MoveAction(1, +1, false));
        am.put("pressed.w", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p1.jump();
            }
        });

        am.put("pressed.left", new MoveAction(2, -1, true));
        am.put("pressed.right", new MoveAction(2, +1, true));
        am.put("released.left", new MoveAction(2, -1, false));
        am.put("released.right", new MoveAction(2, +1, false));
        am.put("pressed.up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p2.sinusoidalJump();
            }
        });

        add(gamePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize((int) (800*multiplier), (int) (400*multiplier));
        setUndecorated(true);
        setVisible(true);
        repaintTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p1.move(p1Movement);
                p2.move(p2Movement);

                repaint();
                revalidate();
            }
        });
        repaintTimer.setInitialDelay(0);
        repaintTimer.setRepeats(true);
        repaintTimer.setCoalesce(true);
        repaintTimer.start();
    }

    class MoveAction extends AbstractAction {
        int playerID;
        int direction;
        boolean pressed;

        public MoveAction(int playerID, int direction, boolean pressed) {
            this.playerID = playerID;
            this.direction = direction;
            this.pressed = pressed;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (pressed) {
                if (playerID == 1) {
                    p1Movement = direction;
                    p1Keys.put(direction, true);
                } else {
                    p2Movement = direction;
                    p2Keys.put(direction, true);
                }
            } else {
                if (playerID == 1) {
                    p1Keys.put(direction, false);
                    if (!p1Keys.get(-direction)) {
                        p1Movement = 0;
                    } else {
                        p1Movement = -direction;
                    }
                } else {
                    p2Keys.put(direction, false);
                    if (!p2Keys.get(-direction)) {
                        p2Movement = 0;
                    } else {
                        p2Movement = -direction;
                    }
                }
            }
        }
        
    }


    public static void main(String[] args) throws Exception {
        new App("ryan", "michael", 1.5);
    }
}