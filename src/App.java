import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.nio.Buffer;
import java.awt.image.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class App extends JFrame {
    private Character p1;
    private Character p2;
    private String p1Name;
    private String p2Name;
    private Map<Integer, Boolean> p1Keys = new HashMap<>();
    private Map<Integer, Boolean> p2Keys = new HashMap<>();
    private int p1Movement = 0;
    private int p2Movement = 0;
    private int p1Direction = +1;
    private int p2Direction = -1;
    private int gun1Index = 0;
    private int gun2Index = 0;
    private int finger1Index = 0;
    private int finger2Index = 0;
    private final int floor;
    private final JPanel gamePanel;
    private Timer repaintTimer;
    public App(String p1Name, String p2Name, double multiplier) throws IOException {
        this.p1Name = p1Name;
        this.p2Name = p2Name;
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

        BufferedImage bufferedRyanPogF = ImageIO.read(getClass().getResource("ryanpogF.png"));
        Image ryanpogIMGF = bufferedRyanPogF.getScaledInstance((int) (70*multiplier), (int) (70*multiplier), BufferedImage.SCALE_SMOOTH);

        BufferedImage bufferedMichelleF = ImageIO.read(getClass().getResource("mkF.png"));
        Image michelleIMGF = bufferedMichelleF.getScaledInstance((int) (70*multiplier), (int) (70*multiplier), BufferedImage.SCALE_SMOOTH);

        BufferedImage bufferedFinger = ImageIO.read(getClass().getResource("finger.png"));
        Image fingerIMG = bufferedFinger.getScaledInstance((int) (20*multiplier), (int) (20*multiplier), BufferedImage.SCALE_SMOOTH);

        BufferedImage bufferedFingerF = ImageIO.read(getClass().getResource("fingerF.png"));
        Image fingerIMGF = bufferedFingerF.getScaledInstance((int) (20*multiplier), (int) (20*multiplier), BufferedImage.SCALE_SMOOTH);

        // Gun (322 x 263) x 13
        // Gun origin: (160, 130)
        BufferedImage bufferedGun = ImageIO.read(getClass().getResource("gun.png"));
        BufferedImage bufferedGunF = ImageIO.read(getClass().getResource("gunF.png"));
        Image[] gunIMG = new Image[13];
        Image[] gunIMGF = new Image[13];

        for (int i = 0; i < 13; i++) {
            gunIMG[i] = bufferedGun.getSubimage(i*322, 0, 322, 263).getScaledInstance((int) (56*multiplier), (int) (46*multiplier), BufferedImage.SCALE_SMOOTH);
        }
        for (int i = 12; i >= 0; i--) {
            gunIMGF[12-i] = bufferedGunF.getSubimage(i*322, 0, 322, 263).getScaledInstance((int) (56*multiplier), (int) (46*multiplier), BufferedImage.SCALE_SMOOTH);
        }

        // Game Panel
        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(bgImage, 0, 0, this);
                if (p1Direction == -1) {
                    g2d.drawImage(ryanpogIMG, (int) (p1.x*multiplier), (int) (p1.y*multiplier), null);
                    if (p1.isShooting) {
                        g2d.drawImage(gunIMGF[gun1Index/3], (int) ((p1.x-40)*multiplier), (int) ((p1.y+20)*multiplier), null);
                    }
                    if (p1.isFingering) {
                        g2d.drawImage(fingerIMGF, (int) ((p1.x-10)*multiplier), (int) ((p1.y+40)*multiplier), null);
                    }
                } else {
                    g2d.drawImage(ryanpogIMGF, (int) (p1.x*multiplier), (int) (p1.y*multiplier), null);
                    if (p1.isShooting) {
                        g2d.drawImage(gunIMG[gun1Index/3], (int) ((p1.x+50)*multiplier), (int) ((p1.y+20)*multiplier), null);
                    }
                    if (p1.isFingering) {
                        g2d.drawImage(fingerIMG, (int) ((p1.x+60)*multiplier), (int) ((p1.y+40)*multiplier), null);
                    }
                }
                if (p2Direction == +1) {
                    g2d.drawImage(michelleIMGF, (int) (p2.x*multiplier), (int) (p2.y*multiplier), null);
                    if (p2.isShooting) {
                        g2d.drawImage(gunIMG[gun2Index/3], (int) ((p2.x+50)*multiplier), (int) ((p2.y+20)*multiplier), null);
                    }
                    if (p2.isFingering) {
                        g2d.drawImage(fingerIMG, (int) ((p2.x+60)*multiplier), (int) ((p2.y+40)*multiplier), null);
                    }
                } else {
                    g2d.drawImage(michelleIMG, (int) (p2.x*multiplier), (int) (p2.y*multiplier), null);
                    if (p2.isShooting) {
                        g2d.drawImage(gunIMGF[gun2Index/3], (int) ((p2.x-40)*multiplier), (int) ((p2.y+20)*multiplier), null);
                    }
                    if (p2.isFingering) {
                        g2d.drawImage(fingerIMGF, (int) ((p2.x-10)*multiplier), (int) ((p2.y+40)*multiplier), null);
                    }
                }

                // Show gun hitboxes
                //g2d.fillRect((int)((p1.x+33)*multiplier), (int)((p1.y)*multiplier), (int)(4*multiplier), (int)(70*multiplier));
                //g2d.fillRect((int)((p2.x+33)*multiplier), (int)((p2.y)*multiplier), (int)(4*multiplier), (int)(70*multiplier)); 
                // Show player hitboxes
                //g2d.drawOval((int) ((p1.x)*multiplier), (int) ((p1.y)*multiplier), (int) (2*p1.hitboxRadius*multiplier), (int) (2*p1.hitboxRadius*multiplier));
                //g2d.drawOval((int) ((p2.x)*multiplier), (int) ((p2.y)*multiplier), (int) (2*p2.hitboxRadius*multiplier), (int) (2*p2.hitboxRadius*multiplier));

                // Health bars
                g2d.fillRect((int)(20*multiplier), (int)(20*multiplier), (int)(p1.currentHP*(300/p1.maxHP)*multiplier), (int)(25*multiplier));
                g2d.fillRect((int)((800-300+20)*multiplier), (int)(20*multiplier), (int)(p2.currentHP*(300/p2.maxHP)*multiplier), (int)(25*multiplier));

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
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0, false), "pressed.g");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0, false), "pressed.t");

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "pressed.left");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "pressed.right");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "released.left");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "released.right");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "pressed.up");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0, false), "pressed.l");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0, false), "pressed.o");

        am.put("pressed.a", new MoveAction(1, -1, true));
        am.put("pressed.d", new MoveAction(1, +1, true));
        am.put("released.a", new MoveAction(1, -1, false));
        am.put("released.d", new MoveAction(1, +1, false));
        am.put("pressed.w", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!p1.isShooting && !p1.isDisabled) {
                    p1.jump();
                }
            }
        });
        am.put("pressed.g", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!p1.isShooting && !p1.isDisabled) {
                    p1.isShooting = true;
                    p1Keys.put(+1, false);
                    p1Keys.put(-1, false);
                }
            }
        });
        am.put("pressed.t", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!p1.isShooting && !p1.isDisabled && !p1.isFingering) {
                    p1.isFingering = true;
                }
            }
        });

        am.put("pressed.left", new MoveAction(2, -1, true));
        am.put("pressed.right", new MoveAction(2, +1, true));
        am.put("released.left", new MoveAction(2, -1, false));
        am.put("released.right", new MoveAction(2, +1, false));
        am.put("pressed.up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!p2.isShooting && !p2.isDisabled) {
                    p2.jump();
                }
            }
        });
        am.put("pressed.l", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!p2.isShooting && !p2.isDisabled) {
                    p2.isShooting = true;
                    p2Keys.put(+1, false);
                    p2Keys.put(-1, false);
                }
            }
        });
        am.put("pressed.o", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!p1.isShooting && !p1.isDisabled && !p1.isFingering) {
                    p2.isFingering = true;
                }
            }
        });

        add(gamePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize((int) (800*multiplier), (int) (400*multiplier));
        setUndecorated(true);
        setVisible(true);
        repaintTimer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long start = System.nanoTime();
                p1.move(p1Movement);
                p2.move(p2Movement);

                // Shoot gun
                if (p1.isShooting) {
                    gun1Index++;
                    if (gun1Index == 39) {
                        gun1Index = 0;
                        p1.isShooting = false;
                        p1Movement = 0;
                    } else if (gun1Index == 24) {
                        if (p1Direction == -1) {
                            if ((p1Direction*((p2.x+35)-(p1.x-40+27)) > 0) && p1.y+20+20 > p2.y && p1.y+20+20 < p2.y+70) {
                                p2.hit(-10);
                                p2Movement = 0;
                            }
                        } else {
                            if ((p1Direction*((p2.x+35)-(p1.x+50+27)) > 0) && p1.y+20+20 > p2.y && p1.y+20+20 < p2.y+70) {
                                p2.hit(-10);
                                p2Movement = 0;
                            }
                        }        
                    }
                }
                if (p2.isShooting) {
                    gun2Index++;
                    if (gun2Index == 39) {
                        gun2Index = 0;
                        p2.isShooting = false;
                        p2Movement = 0;
                    } else if (gun2Index == 24) {
                        if (p2Direction == +1) {
                            if ((p2Direction*((p1.x+35)-(p2.x+50+27)) > 0) && p2.y+20+20 > p1.y && p2.y+20+20 < p1.y+70) {
                                p1.hit(-10);
                                p1Movement = 0;
                            }
                        } else {
                            if ((p2Direction*((p1.x+35)-(p2.x-40+27)) > 0) && p2.y+20+20 > p1.y && p2.y+20+20 < p1.y+70) {
                                p1.hit(-10);
                                p1Movement = 0;
                            } 
                        }
                    }
                }

                // Finger
                if (finger1Index == 10) {
                    finger1Index = 0;
                    p1.isFingering = false;
                }
                if (finger2Index == 10) {
                    finger2Index = 0;
                    p2.isFingering = false;
                }
                // Player-Player collisions
                if (p1.isFingering) {
                    finger1Index++;
                    if (finger1Index == 1) {
                        if (Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2)) <= p1.hitboxRadius + p2.hitboxRadius) {
                            p2.hit(-10);
                            p2Movement = 0;
                        }
                    }
                }
                if (p2.isFingering) {
                    finger2Index++;
                    if (finger2Index == 1) {
                        if (Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2)) <= p1.hitboxRadius + p2.hitboxRadius) {
                            p1.hit(-10);
                            p1Movement = 0;
                        }
                    }
                }

                repaint();
                revalidate();
                
                long elapsed = System.nanoTime() - start;
                long wait = 16 - elapsed/1000000;

                if (wait <= 0) {
                    wait = 5;
                }

                try {
                    Thread.sleep(wait);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
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
                    if (p1.isShooting || p1.isDisabled) return;
                    p1Movement = direction;
                    p1Direction = direction;
                    p1Keys.put(direction, true);
                } else {
                    if (p2.isShooting || p2.isDisabled) return;
                    p2Movement = direction;
                    p2Direction = direction;
                    p2Keys.put(direction, true);
                }
            } else {
                if (playerID == 1) {
                    if (p1.isShooting || p1.isDisabled) return;
                    p1Keys.put(direction, false);
                    if (!p1Keys.get(-direction)) {
                        p1Movement = 0;
                    } else {
                        p1Movement = -direction;
                    }
                } else {
                    if (p2.isShooting || p2.isDisabled) return;
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