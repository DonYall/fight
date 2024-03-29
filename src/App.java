import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.awt.image.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class App extends JFrame {
    private double multiplier;
    private Character p1;
    private Character p2;
    private Map<Integer, Boolean> p1Keys = new HashMap<>();
    private Map<Integer, Boolean> p2Keys = new HashMap<>();
    private boolean p1Cutscene = false;
    private boolean p2Cutscene = false;
    private double p1CutsceneVelocity = 0.2;
    private double p2CutsceneVelocity = 0.2;
    private double p1CutsceneScale = 1;
    private double p2CutsceneScale = 1;
    private int p1Movement = 0;
    private int p2Movement = 0;
    private int p1Direction = +1;
    private int p2Direction = -1;
    private int gun1Index = 0;
    private int gun2Index = 0;
    private int finger1Index = 0;
    private int finger2Index = 0;
    private int explosion1Index = -1;
    private int explosion2Index = -1;
    private final JPanel gamePanel;
    private double accelerator = 0.1;
    private double p1Velocitator = 0;
    private double p1Rotator = 0;
    private double p2Velocitator = 0;
    private double p2Rotator = 0;
    private ArrayList<Character> p1Decoys = new ArrayList<>();
    private ArrayList<Character> p2Decoys = new ArrayList<>();
    private Timer repaintTimer;
    private Image bgImage;
    private Image ryanpogIMG;
    private Image ryanpogIMGF;
    private Image michelleIMG;
    private Image michelleIMGF;
    private Image fingerIMG;
    private Image fingerIMGF;
    private Image xIMG;
    private Image[] gunIMG;
    private Image[] gunIMGF;
    private Image hammerIMG;
    private Image hammerIMGF;
    private Image[] explosionIMG;
    private Image yoshiIMG;
    private BufferedImage bufferedBG;
    private BufferedImage bufferedRyanPog;
    private BufferedImage bufferedRyanPogF;
    private BufferedImage bufferedMichelle;
    private BufferedImage bufferedMichelleF;
    private BufferedImage bufferedFinger;
    private BufferedImage bufferedFingerF;
    private BufferedImage bufferedGun;
    private BufferedImage bufferedGunF;
    private BufferedImage bufferedX;
    private BufferedImage bufferedHammer;
    private BufferedImage bufferedHammerF;
    private BufferedImage bufferedExplosion;
    private BufferedImage bufferedYoshi;

    public App(String p1Name, String p2Name) throws IOException, LineUnavailableException {
        multiplier = Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 800.0;

        p1Keys.put(1, false);
        p1Keys.put(-1, false);
        p2Keys.put(1, false);
        p2Keys.put(-1, false);

        p1 = new Character(p1Name, 100, 250);
        p2 = new Character(p2Name, 600, 250);
        p1.opponent = p2;
        p2.opponent = p1;

        // Background image
        bufferedBG = ImageIO.read(getClass().getResource("bg1.jpg"));

        // Character images
        bufferedRyanPog = ImageIO.read(getClass().getResource(p1Name + ".png"));
        bufferedMichelle = ImageIO.read(getClass().getResource(p2Name + ".png"));
        bufferedRyanPogF = ImageIO.read(getClass().getResource(p1Name + "F.png"));
        bufferedMichelleF = ImageIO.read(getClass().getResource(p2Name + "F.png"));
        bufferedFinger = ImageIO.read(getClass().getResource("finger.png"));
        bufferedFingerF = ImageIO.read(getClass().getResource("fingerF.png"));
        bufferedX = ImageIO.read(getClass().getResource("x.png"));
        bufferedHammer = ImageIO.read(getClass().getResource("hammer.png"));
        bufferedHammerF = ImageIO.read(getClass().getResource("hammerF.png"));
        bufferedYoshi = ImageIO.read(getClass().getResource("yoshi.png"));

        // Gun (322 x 263) x 13
        // Gun origin: (160, 130)
        gunIMG = new Image[13];
        gunIMGF = new Image[13];
        bufferedGun = ImageIO.read(getClass().getResource("gun.png"));
        bufferedGunF = ImageIO.read(getClass().getResource("gunF.png"));

        // Explosion (256 x 251) * 15
        explosionIMG = new Image[15];
        bufferedExplosion = ImageIO.read(getClass().getResource("explosion.png"));

        loadImages();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].setFullScreenWindow(this);
        setFocusable(true);
        requestFocusInWindow();

        // Game Panel
        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                g2d.setColor(Color.BLACK.darker());
                if (p1Cutscene) {
                    p1CutsceneScale += p1CutsceneVelocity;
                    g2d.translate(68*multiplier, 280*multiplier);
                    g2d.scale(p1CutsceneScale, p1CutsceneScale);
                    g2d.translate(-68*multiplier, -280*multiplier);
                } else if (p2Cutscene) {
                    p2CutsceneScale += p2CutsceneVelocity;
                    g2d.translate(660*multiplier, 280*multiplier);
                    g2d.scale(p2CutsceneScale, p2CutsceneScale);
                    g2d.translate(-660*multiplier, -280*multiplier);
                }
                g2d.drawImage(bgImage, 0, 0, this);
                g2d.drawImage(xIMG, (int) (780*multiplier), 0, null);

                // Draw gun tracer
                if (gun1Index == 24) {
                    if (p1.isSupering == Supers.ANDREW_SUPER) {
                        g2d.drawLine((int) ((p1.x+35)*multiplier), (int) ((p1.y+20+20)*multiplier), (int) ((p2.x+35)*multiplier), (int) ((p2.y+20+20)*multiplier));
                    } else {
                        g2d.drawLine((int) ((p1.x+35)*multiplier), (int) ((p1.y+20+20)*multiplier), (int) ((400 + 400*p1Direction)*multiplier), (int) ((p1.y+20+20)*multiplier));
                    }
                }
                if (gun2Index == 24) {
                    if (p2.isSupering == Supers.ANDREW_SUPER) {
                        g2d.drawLine((int) ((p2.x+35)*multiplier), (int) ((p2.y+20+20)*multiplier), (int) ((p1.x+35)*multiplier), (int) ((p1.y+20+20)*multiplier));
                    } else {
                        g2d.drawLine((int) ((p2.x+35)*multiplier), (int) ((p2.y+20+20)*multiplier), (int) ((400 + 400*p2Direction)*multiplier), (int) ((p2.y+20+20)*multiplier));
                    }
                }

                // Draw characters, guns, fingers
                if (p1.isSupering == Supers.DEEV_SUPER) {
                    if (p2Direction == +1) {
                        drawCharacter(g2d, p2, michelleIMGF);
                        if (p2.isShooting && p2.isSupering != Supers.DEEV_SUPER) {
                            g2d.drawImage(gunIMG[gun2Index/3], (int) ((p2.x+50)*multiplier), (int) ((p2.y+20)*multiplier), null);
                        }
                        if (p2.isFingering) {
                            g2d.drawImage(fingerIMG, (int) ((p2.x+60)*multiplier), (int) ((p2.y+40)*multiplier), null);
                        }
                    } else {
                        drawCharacter(g2d, p2, michelleIMG);
                        if (p2.isShooting && p2.isSupering != Supers.DEEV_SUPER) {
                            g2d.drawImage(gunIMGF[gun2Index/3], (int) ((p2.x-40)*multiplier), (int) ((p2.y+20)*multiplier), null);
                        }
                        if (p2.isFingering) {
                            g2d.drawImage(fingerIMGF, (int) ((p2.x-10)*multiplier), (int) ((p2.y+40)*multiplier), null);
                        }
                    }
                    if (p1Direction == -1) {
                        drawCharacter(g2d, p1, ryanpogIMG);
                        if (p1.isFingering) {
                            g2d.drawImage(fingerIMGF, (int) ((p1.x-10)*multiplier), (int) ((p1.y+40)*multiplier), null);
                        }
                    } else {
                        drawCharacter(g2d, p1, ryanpogIMGF);
                        if (p1.isFingering) {
                            g2d.drawImage(fingerIMG, (int) ((p1.x+60)*multiplier), (int) ((p1.y+40)*multiplier), null);
                        }
                    }
                } else {
                    if (p1Direction == -1) {
                        drawCharacter(g2d, p1, ryanpogIMG);
                        if (p1.isShooting) {
                            g2d.drawImage(gunIMGF[gun1Index/3], (int) ((p1.x-40)*multiplier), (int) ((p1.y+20)*multiplier), null);
                        }
                        if (p1.isFingering) {
                            g2d.drawImage(fingerIMGF, (int) ((p1.x-10)*multiplier), (int) ((p1.y+40)*multiplier), null);
                        }
                    } else {
                        drawCharacter(g2d, p1, ryanpogIMGF);
                        if (p1.isShooting) {
                            g2d.drawImage(gunIMG[gun1Index/3], (int) ((p1.x+50)*multiplier), (int) ((p1.y+20)*multiplier), null);
                        }
                        if (p1.isFingering) {
                            g2d.drawImage(fingerIMG, (int) ((p1.x+60)*multiplier), (int) ((p1.y+40)*multiplier), null);
                        }
                    }
                    if (p2Direction == +1) {
                        drawCharacter(g2d, p2, michelleIMGF);
                        if (p2.isShooting && p2.isSupering != Supers.DEEV_SUPER) {
                            g2d.drawImage(gunIMG[gun2Index/3], (int) ((p2.x+50)*multiplier), (int) ((p2.y+20)*multiplier), null);
                        }
                        if (p2.isFingering) {
                            g2d.drawImage(fingerIMG, (int) ((p2.x+60)*multiplier), (int) ((p2.y+40)*multiplier), null);
                        }
                    } else {
                        drawCharacter(g2d, p2, michelleIMG);
                        if (p2.isShooting && p2.isSupering != Supers.DEEV_SUPER) {
                            g2d.drawImage(gunIMGF[gun2Index/3], (int) ((p2.x-40)*multiplier), (int) ((p2.y+20)*multiplier), null);
                        }
                        if (p2.isFingering) {
                            g2d.drawImage(fingerIMGF, (int) ((p2.x-10)*multiplier), (int) ((p2.y+40)*multiplier), null);
                        }
                    }    
                }

                // Draw explosions
                if (explosion1Index > -1) {
                    g2d.drawImage(explosionIMG[explosion1Index], (int) ((p1.x-15)*multiplier), (int) ((p1.y+70)*multiplier), null);
                }
                if (explosion2Index > -1) {
                    g2d.drawImage(explosionIMG[explosion2Index], (int) ((p2.x-15)*multiplier), (int) ((p2.y+70)*multiplier), null);
                }

                // Show player hitboxes
                // g2d.drawOval((int) ((p1.x)*multiplier), (int) ((p1.y)*multiplier), (int) (2*p1.hitboxRadius*multiplier), (int) (2*p1.hitboxRadius*multiplier));
                // g2d.drawOval((int) ((p2.x)*multiplier), (int) ((p2.y)*multiplier), (int) (2*p2.hitboxRadius*multiplier), (int) (2*p2.hitboxRadius*multiplier));

                // Health bars
                if (p2.isSupering == Supers.DON_SUPER && p1.currentHP <= p1.bombs*13) {
                    g2d.setColor(Color.RED.darker());
                }
                g2d.fillRect((int)(20*multiplier),           (int)(20*multiplier), (int)((300*p1.currentHP/p1.maxHP)*multiplier), (int)(25*multiplier));
                if (p1.isSupering == Supers.DON_SUPER && p2.currentHP <= p2.bombs*13) {
                    g2d.setColor(Color.RED.darker());
                } else {
                    g2d.setColor(Color.BLACK.darker());
                }
                g2d.fillRect((int)((800-300-20)*multiplier), (int)(20*multiplier), (int)((300*p2.currentHP/p2.maxHP)*multiplier), (int)(25*multiplier));
                g2d.setColor(Color.BLACK.darker());

                // Super progress bars
                if (p1.currentHP > 0) {
                    g2d.drawRect((int)(20*multiplier), (int)(50*multiplier), (int)(300*multiplier), (int)(10*multiplier));
                    g2d.fillRect((int)(20*multiplier), (int)(50*multiplier), (int)(p1.superProgress*(3)*multiplier), (int)(10*multiplier));
                }
                if (p2.currentHP > 0) {
                    g2d.drawRect((int)((800-300-20)*multiplier), (int)(50*multiplier), (int)(300*multiplier), (int)(10*multiplier));
                    g2d.fillRect((int)((800-300-20)*multiplier), (int)(50*multiplier), (int)(p2.superProgress*(3)*multiplier), (int)(10*multiplier));
                }

                g2d.setColor(Color.WHITE);

                // Usernames
                g2d.setFont(new Font(Font.DIALOG, Font.PLAIN, (int) (8*multiplier)));
                g2d.drawString(p1.name, (int) (p1.x*multiplier), (int) (p1.y*multiplier));
                g2d.drawString(p2.name, (int) (p2.x*multiplier), (int) (p2.y*multiplier));

                // HP
                g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int) (15*multiplier)));
                g2d.drawString(String.valueOf(p1.currentHP), (int) (23*multiplier), (int) (37.6*multiplier));
                g2d.drawString(String.valueOf(p2.currentHP), (int) ((800-300-17)*multiplier), (int) (37.5*multiplier));

                g2d.dispose();
            }
        };
        InputMap im = gamePanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = gamePanel.getActionMap();

        // General inputs
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0, false), "pressed.plus");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0, false), "pressed.plus");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0, false), "pressed.minus");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UNDERSCORE, 0, false), "pressed.minus");

        // General actions
        am.put("pressed.plus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                multiplier += 0.1;
                loadImages();
                setSize((int) (800*multiplier), (int) (450*multiplier));
            }
        });
        am.put("pressed.minus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                multiplier -= 0.1;
                loadImages();
                setSize((int) (800*multiplier), (int) (450*multiplier));
            }
        });

        // Player 1 inputs
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "pressed.a");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "pressed.d");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "released.a");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "released.d");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "pressed.w");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0, false), "pressed.g");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0, false), "pressed.t");

        // Player 2 inputs
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "pressed.left");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "pressed.right");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "released.left");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "released.right");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "pressed.up");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0, false), "pressed.l");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0, false), "pressed.o");

        // Player 1 actions
        am.put("pressed.a", new MoveAction(1, -1, true));
        am.put("pressed.d", new MoveAction(1, +1, true));
        am.put("released.a", new MoveAction(1, -1, false));
        am.put("released.d", new MoveAction(1, +1, false));
        am.put("pressed.w", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (p1.isSupering == Supers.ANDREW_SUPER) {
                    p1.circularJump();
                } else if (!(p1.isShooting && p1.SUPER != Supers.KAIRO_SUPER) && !p1.isDisabled && !p1.circularJumping) {
                    p1.jump();
                }
            }
        });
        am.put("pressed.g", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!p1.isShooting && !p1.isDisabled && !(p1Cutscene || p2Cutscene)) {
                    p1.isShooting = true;
                    p1.shotsFired++;
                    if (p1.isSupering != Supers.ANDREW_SUPER && p1.SUPER != Supers.KAIRO_SUPER) {
                        p1Keys.put(+1, false);
                        p1Keys.put(-1, false);    
                    }
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

        // Player 2 actions
        am.put("pressed.left", new MoveAction(2, -1, true));
        am.put("pressed.right", new MoveAction(2, +1, true));
        am.put("released.left", new MoveAction(2, -1, false));
        am.put("released.right", new MoveAction(2, +1, false));
        am.put("pressed.up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (p2.isSupering == Supers.ANDREW_SUPER) {
                    p2.circularJump();
                } else if (!(p2.isShooting && p2.SUPER != Supers.KAIRO_SUPER) && !p2.isDisabled && !p2.circularJumping) {
                    p2.jump();
                }
            }
        });
        am.put("pressed.l", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!p2.isShooting && !p2.isDisabled && !(p1Cutscene || p2Cutscene)) {
                    p2.isShooting = true;
                    p2.shotsFired++;
                    if (p2.isSupering != Supers.ANDREW_SUPER && p2.SUPER != Supers.KAIRO_SUPER) {
                        p2Keys.put(+1, false);
                        p2Keys.put(-1, false);
                    }
                }
            }
        });
        am.put("pressed.o", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!p2.isShooting && !p2.isDisabled && !p2.isFingering) {
                    p2.isFingering = true;
                }
            }
        });

        addMouseListener(new MouseInputListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getX() > 780*multiplier && e.getY() < 7.5*multiplier) {
                    System.exit(0);
                }
            }

            // Useless required methods            
            public void mouseDragged(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseMoved(MouseEvent e) {}
        });

        add(gamePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(ImageIO.read(getClass().getResource("ryanpog.png")));
        setUndecorated(true);
        setVisible(true);
        repaintTimer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // long start = System.nanoTime();
                if (!p1Cutscene) {
                    if (p2.isSupering == Supers.DEEV_SUPER) {
                        p1.move(-p1Movement);
                        p1Direction *= -1;
                    } else {
                        p1.move(p1Movement);
                    }
                    for (Character c : p1Decoys) {
                        c.move(p1Movement);
                    }
                } else {
                    p1CutsceneVelocity -= 0.005;
                    if (p1CutsceneVelocity <= 0) {
                        p1Cutscene = false;
                        p1CutsceneVelocity = 0.2;
                        p1CutsceneScale = 1;
                    }
                }
                if (!p2Cutscene) {
                    if (p1.isSupering == Supers.DEEV_SUPER){
                        p2.move(-p2Movement);
                        p2Direction *= -1;
                    } else {
                        p2.move(p2Movement);
                    }
                    for (Character c : p2Decoys) {
                        c.move(p2Movement);
                    }
                } else {
                    p2CutsceneVelocity -= 0.005;
                    if (p2CutsceneVelocity <= 0) {
                        p2Cutscene = false;
                        p2CutsceneVelocity = 0.2;
                        p2CutsceneScale = 1;
                    }
                }

                // Explode
                if (explosion1Index > -1) {
                    explosion1Index++;
                    if (explosion1Index == 15) {
                        explosion1Index = -1;
                    }
                }
                if (explosion2Index > -1) {
                    explosion2Index++;
                    if (explosion2Index == 15) {
                        explosion2Index = -1;
                    }
                }

                // Shoot gun
                if (p1.isShooting) {
                    gun1Index++;
                    if (p1.isSupering == Supers.KAIRO_SUPER) gun1Index += 2;
                    if (gun1Index >= 39) {
                        gun1Index = 0;
                        p1.isShooting = false;
                        if (p1.isSupering != Supers.ANDREW_SUPER && p1.SUPER != Supers.KAIRO_SUPER) p1Movement = 0;
                    } else if (gun1Index == 24) {
                        if (p1.isSupering == Supers.ANDREW_SUPER) {
                            p2.hit(-10, 0);
                            p2Movement = 0;
                        } else if (p1Direction == -1) {
                            if ((p1Direction*((p2.x+35)-(p1.x-40+27)) > 0) && p1.y+20+20 > p2.y && p1.y+20+20 < p2.y+70) {
                                p2.hit(-10, 12);
                                p2Movement = 0;
                            } else if (p2.isSupering == Supers.DEEV_SUPER) {
                                // p1.hit(0, 10);
                                // p1Movement = 0;
                            }
                        } else {
                            if ((p1Direction*((p2.x+35)-(p1.x+50+27)) > 0) && p1.y+20+20 > p2.y && p1.y+20+20 < p2.y+70) {
                                p2.hit(-10, 12);
                                p2Movement = 0;
                            } else if (p2.isSupering == Supers.DEEV_SUPER) {
                                // p1.hit(0, 10);
                                // p1Movement = 0;
                            }
                        }
                    }
                }
                if (p2.isShooting) {
                    gun2Index++;
                    if (p2.isSupering == Supers.KAIRO_SUPER) gun2Index += 2;
                    if (gun2Index >= 39) {
                        gun2Index = 0;
                        p2.isShooting = false;
                        if (p2.isSupering != Supers.ANDREW_SUPER && p2.SUPER != Supers.KAIRO_SUPER) p2Movement = 0;
                    } else if (gun2Index == 24) {
                        if (p2.isSupering == Supers.ANDREW_SUPER) {
                            p1.hit(-10, 0);
                            p1Movement = 0;
                        } else if (p2Direction == +1) {
                            if ((p2Direction*((p1.x+35)-(p2.x+50+27)) > 0) && p2.y+20+20 > p1.y && p2.y+20+20 < p1.y+70) {
                                p1.hit(-10, 12);
                                p1Movement = 0;
                            } else if (p1.isSupering == Supers.DEEV_SUPER) {
                                // p2.hit(0, 10);
                                // p2Movement = 0;
                            }
                        } else {
                            if ((p2Direction*((p1.x+35)-(p2.x-40+27)) > 0) && p2.y+20+20 > p1.y && p2.y+20+20 < p1.y+70) {
                                p1.hit(-10, 12);
                                p1Movement = 0;
                            } else if (p1.isSupering == Supers.DEEV_SUPER) {
                                // p2.hit(0, 10);
                                // p2Movement = 0;
                            }
                        }
                    }
                }

                // Finger
                if (finger1Index == 20) {
                    finger1Index = 0;
                    p1.isFingering = false;
                }
                if (finger2Index == 20) {
                    finger2Index = 0;
                    p2.isFingering = false;
                }
                // Supers / Player-Player collisions
                if (p1.isSupering == Supers.RYAN_SUPER) {
                    if (Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2)) <= p1.hitboxRadius + p2.hitboxRadius) {
                        p2.hit((int)(-p1Velocitator*1.5), 7);
                        // Headbutt
                        if (p2.isSupering == Supers.RYAN_SUPER) {
                            p1.hit((int)(-p2Velocitator*1.5), 7);
                        } else {
                            p1.hit(0, 7);
                        }
                        p2Movement = 0;
                        p1.isSupering = 0;
                        p1.superProgress = 0;
                        p1Velocitator = 0;
                        p1Rotator = 0;
                    }
                } else if (p1.isSupering > Supers.RYAN_SUPER) {
                    if (p1.isSupering == Supers.DEEV_SUPER) {
                        p1Decoys.add(new Character("deev ai", randInt(p1.x-50, p1.x+50), p1.y));
                    }
                    p1.superProgress -= 0.15;
                    if (p1.superProgress <= 0) {
                        if (p1.isSupering == Supers.DON_SUPER) {
                            p2.hit(0, 40);
                            explosion2Index = 0;
                            playSound("explosion");
                        }
                        p1.isSupering = 0;
                        if (p1.SUPER == Supers.STEPH_SUPER) p1.velocity = 7;
                        if (p1.SUPER == Supers.ETHAN_SUPER) p1.velocity = 4.5;
                        p1Decoys.clear();
                        p1.yoshis.clear();
                    }
                } else if (!p1Cutscene && !p2Cutscene && p1.currentHP > 0) {
                    p1.superProgress += 0.05;
                    if (p1.superProgress >= 100) {
                        p1.isSupering = p1.SUPER;
                        if (p1.SUPER == Supers.STEPH_SUPER) p1.velocity = 4;
                        if (p1.SUPER == Supers.ETHAN_SUPER) p1.velocity = 4.5;
                        if (p2.isSupering == 0 && p2.superProgress >= 99.95) {
                            p2.isSupering = p2.SUPER;
                            if (p2.SUPER == Supers.STEPH_SUPER) p2.velocity = 4;
                            if (p2.SUPER == Supers.ETHAN_SUPER) p2.velocity = 4.5;
                            if (p2.isSupering == Supers.RYAN_SUPER) {
                                p2.superProgress = 0;
                            } else {
                                p2.superProgress = 100;
                            }
                        }
                        p1Cutscene = true;
                        playSound(p1.name);
                        p1.x = 100;
                        p2.x = 600;
                        p1.y = 250;
                        p2.y = 250;
                        p1.isDisabled = true;
                        p2.isDisabled = true;
                        if (p1.isSupering == Supers.RYAN_SUPER) {
                            p1.superProgress = 0;
                        }
                    }
                }
                if (p2.isSupering == Supers.RYAN_SUPER) {
                    if (Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2)) <= p1.hitboxRadius + p2.hitboxRadius) {
                        p1.hit((int)(-p2Velocitator*1.5), 7);
                        p2.hit(0, 7);
                        p1Movement = 0;
                        p2.isSupering = 0;
                        p2.superProgress = 0;
                        p2Velocitator = 0;
                        p2Rotator = 0;
                    }
                } else if (p2.isSupering > Supers.RYAN_SUPER) {
                    if (p2.isSupering == Supers.DEEV_SUPER) {
                        p2Decoys.add(new Character("deev ai", randInt(p2.x-50, p2.x+50), p2.y));
                    }
                    p2.superProgress -= 0.15;
                    if (p2.superProgress <= 0) {
                        if (p2.isSupering == Supers.DON_SUPER) {
                            p1.hit(0, 40);
                            explosion1Index = 0;
                            playSound("explosion");
                        }
                        p2.isSupering = 0;
                        if (p2.SUPER == Supers.STEPH_SUPER) p2.velocity = 7;
                        if (p2.SUPER == Supers.ETHAN_SUPER) p2.velocity = 4.5;
                        p2Decoys.clear();
                        p2.yoshis.clear();
                    }
                } else if (!p1Cutscene && !p2Cutscene && p2.currentHP > 0) {
                    p2.superProgress += 0.05;
                    if (p2.superProgress >= 100) {
                        p2.isSupering = p2.SUPER;
                        if (p2.SUPER == Supers.STEPH_SUPER) p2.velocity = 4;
                        if (p2.SUPER == Supers.ETHAN_SUPER) p2.velocity = 4.5;
                        p2Cutscene = true;
                        playSound(p2.name);
                        p1.x = 100;
                        p2.x = 600;
                        p1.y = 250;
                        p2.y = 250;
                        p1.isDisabled = true;
                        p2.isDisabled = true;
                        if (p2.isSupering == Supers.RYAN_SUPER) {
                            p2.superProgress = 0;
                        } else {
                            p2.superProgress = 100;
                        }
                    }
                }
                if (p1.isFingering) {
                    finger1Index++;
                    if (finger1Index == 1) {
                        if (Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2)) <= p1.hitboxRadius + p2.hitboxRadius) {
                            if (p1.isSupering == Supers.ETHAN_SUPER) {
                                p2.hit(-p2.currentHP+1, 7);
                                p2Movement = 0;
                                p1Movement = 0;
                                p2.isSupering = 0;
                                p2.superProgress = 99.9;
                                p1.isSupering = 0;
                                p1.superProgress = 0;
                                p1.velocity = 7;
                            } else {
                                p2.hit(-10, 7);
                                p2Movement = 0;    
                            }
                        } else if (p2.isSupering == Supers.DEEV_SUPER) {
                            // p1.hit(0, 10);
                            // p1Movement = 0;
                        }
                    }
                }
                if (p1.isSupering == Supers.STEPH_SUPER) {
                    if (Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2)) <= p1.hitboxRadius + p2.hitboxRadius && !p2.isDisabled) {
                        p2.hit(-p2.maxHP/8, 30);
                    }
                }
                if (p2.isFingering) {
                    finger2Index++;
                    if (finger2Index == 1) {
                        if (Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2)) <= p1.hitboxRadius + p2.hitboxRadius) {
                            if (p2.isSupering == Supers.ETHAN_SUPER) {
                                p1.hit(-p1.currentHP+1, 7);
                                p2Movement = 0;
                                p1Movement = 0;
                                p2.isSupering = 0;
                                p2.superProgress = 0;
                                p1.isSupering = 0;
                                p1.superProgress = 99.9;
                                p2.velocity = 7;
                            } else {
                                p1.hit(-10, 7);
                                p1Movement = 0;    
                            }
                        } else if (p1.isSupering == Supers.DEEV_SUPER) {
                            // p2.hit(0, 10);
                            // p2Movement = 0;
                        }
                    }
                }
                if (p2.isSupering == Supers.STEPH_SUPER) {
                    if (Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2)) <= p1.hitboxRadius + p2.hitboxRadius && !p1.isDisabled) {
                        p1.hit(-p1.maxHP/8, 30);
                    }
                }

                repaint();
                revalidate();

                // final long elapsed = System.nanoTime() - start;
                // long wait = 13 - elapsed / 1000000;

                // if (wait <= 0) {
                //     wait = 5;
                // }

                try {
                    Thread.sleep(1000/60);
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

    private class MoveAction extends AbstractAction {
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
                    if ((p1.isShooting && p1.isSupering != Supers.ANDREW_SUPER && p1.SUPER != Supers.KAIRO_SUPER) || (p1.isDisabled && p2.isSupering != Supers.STEPH_SUPER)) return;
                    p1Movement = direction;
                    p1Direction = direction;
                    p1Keys.put(direction, true);
                } else {
                    if ((p2.isShooting && p2.isSupering != Supers.ANDREW_SUPER && p2.SUPER != Supers.KAIRO_SUPER) || (p2.isDisabled && p1.isSupering != Supers.STEPH_SUPER)) return;
                    p2Movement = direction;
                    p2Direction = direction;
                    p2Keys.put(direction, true);
                }
            } else {
                if (playerID == 1) {
                    if ((p1.isShooting && p1.isSupering != Supers.ANDREW_SUPER && p1.SUPER != Supers.KAIRO_SUPER) || (p1.isDisabled && p2.isSupering != Supers.STEPH_SUPER)) return;
                    p1Keys.put(direction, false);
                    if (!p1Keys.get(-direction)) {
                        p1Movement = 0;
                    } else {
                        p1Movement = -direction;
                    }
                } else {
                    if ((p2.isShooting && p2.isSupering != Supers.ANDREW_SUPER && p2.SUPER != Supers.KAIRO_SUPER) || (p2.isDisabled && p1.isSupering != Supers.STEPH_SUPER)) return;
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

    public void drawCharacter(Graphics2D g2d, Character p, Image i) {
        if (p.isSupering == Supers.RYAN_SUPER) {
            if (p == p1) {
                // Ryan super
                AffineTransform currentTransform = g2d.getTransform();
                p1Velocitator += accelerator;
                p1Rotator += p1Velocitator;
                g2d.translate((int) ((p.x+35)*multiplier), (int) ((p.y+35)*multiplier));
                g2d.rotate(Math.toRadians(p1Rotator));
                g2d.drawImage(i, (int) -(35*multiplier), (int) -(35*multiplier), null);
                g2d.setTransform(currentTransform);
            } else {
                // Ryan super
                AffineTransform currentTransform = g2d.getTransform();
                p2Velocitator += accelerator;
                p2Rotator += p2Velocitator;
                g2d.translate((int) ((p.x+35)*multiplier), (int) ((p.y+35)*multiplier));
                g2d.rotate(Math.toRadians(p2Rotator));
                g2d.drawImage(i, (int) -(35*multiplier), (int) -(35*multiplier), null);
                g2d.setTransform(currentTransform);
            }
            if (p.velocity < 30) {
                p.velocity += (accelerator/10);
            }
        } else {
            g2d.drawImage(i, (int) (p.x*multiplier), (int) (p.y*multiplier), null);
            if (p.isSupering == Supers.DEEV_SUPER) {
                if (p == p1) {
                    for (Character c : p1Decoys) {
                        g2d.drawImage(i, (int) (c.x*multiplier), (int) (c.y*multiplier), null);
                    }
                } else {
                    for (Character c : p2Decoys) {
                        g2d.drawImage(i, (int) (c.x*multiplier), (int) (c.y*multiplier), null);
                    }
                }
            } else if (p.isSupering == Supers.STEPH_SUPER) {
                if (System.nanoTime() % 2 == 0) {
                    g2d.drawImage(hammerIMG, (int) ((p.x-30)*multiplier), (int) ((p.y+20)*multiplier), null);
                } else {
                    g2d.drawImage(hammerIMGF, (int) ((p.x+50)*multiplier), (int) ((p.y+20)*multiplier), null);
                }
            } else if (p.isSupering == Supers.JOSEPH_SUPER) {
                ArrayList<Character> yoshisToRemove = new ArrayList<>();
                for (Character c : p.yoshis) {
                    if (c.y == 250) {
                        yoshisToRemove.add(c);
                        continue;
                    }
                    if (c.x + 24 > p.opponent.x && c.x < p.opponent.x + 70 && p.opponent.y - c.y < 175 && c.y != p.y + 70) {
                        p.opponent.hit(-6, 0);
                        if (p.opponent.name.equals("ryanpog")) {
                            p.opponent.isSupering = 0;
                            p.opponent.superProgress = 0;
                        }
                    }
                    c.move(0);
                    g2d.drawImage(yoshiIMG, (int) (c.x*multiplier), (int) (c.y*multiplier), null); 
                }

                for (Character c : yoshisToRemove) {
                    p.yoshis.remove(c);
                }
            }
        }
    }

    public int randInt(int min, int max) {
        return((int) (Math.random()*max + min));
    }

    public void playSound(String name) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(getClass().getResource(name + ".wav")));
            clip.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (clip.isRunning()) {
                    }
                    clip.close();
                }
            }).start();
        } catch (NullPointerException e) {
            System.out.println("no audio file for " + name);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e1) {
            e1.printStackTrace();
        }
    }

    public void loadImages() {
        // Background image
        bgImage = bufferedBG.getScaledInstance((int) (800*multiplier), (int) (450*multiplier), BufferedImage.SCALE_SMOOTH);

        // Other images
        ryanpogIMG = bufferedRyanPog.getScaledInstance((int) (70*multiplier), (int) (70*multiplier), BufferedImage.SCALE_SMOOTH);
        michelleIMG = bufferedMichelle.getScaledInstance((int) (70*multiplier), (int) (70*multiplier), BufferedImage.SCALE_SMOOTH);
        ryanpogIMGF = bufferedRyanPogF.getScaledInstance((int) (70*multiplier), (int) (70*multiplier), BufferedImage.SCALE_SMOOTH);
        michelleIMGF = bufferedMichelleF.getScaledInstance((int) (70*multiplier), (int) (70*multiplier), BufferedImage.SCALE_SMOOTH);
        fingerIMG = bufferedFinger.getScaledInstance((int) (20*multiplier), (int) (20*multiplier), BufferedImage.SCALE_SMOOTH);
        fingerIMGF = bufferedFingerF.getScaledInstance((int) (20*multiplier), (int) (20*multiplier), BufferedImage.SCALE_SMOOTH);
        hammerIMG = bufferedHammer.getScaledInstance((int) (50*multiplier), (int) (50*multiplier), BufferedImage.SCALE_SMOOTH);
        hammerIMGF = bufferedHammerF.getScaledInstance((int) (50*multiplier), (int) (50*multiplier), BufferedImage.SCALE_SMOOTH);
        xIMG = bufferedX.getScaledInstance((int) (20*multiplier), (int) (7.5*multiplier), BufferedImage.SCALE_SMOOTH);
        yoshiIMG = bufferedYoshi.getScaledInstance((int) (24*multiplier), (int) (28*multiplier), BufferedImage.SCALE_SMOOTH);

        // Gun (322 x 263) x 13
        // Gun origin: (160, 130)
        for (int i = 0; i < 13; i++) {
            gunIMG[i] = bufferedGun.getSubimage(i*322, 0, 322, 263).getScaledInstance((int) (56*multiplier), (int) (46*multiplier), BufferedImage.SCALE_SMOOTH);
        }
        for (int i = 12; i >= 0; i--) {
            gunIMGF[12-i] = bufferedGunF.getSubimage(i*322, 0, 322, 263).getScaledInstance((int) (56*multiplier), (int) (46*multiplier), BufferedImage.SCALE_SMOOTH);
        }

        // Explosion (256 x 251) x 15
        for (int i = 0; i < 15; i++) {
            explosionIMG[i] = bufferedExplosion.getSubimage(i*256, 0, 256, 251).getScaledInstance((int) (100*multiplier), (int) (100*multiplier), BufferedImage.SCALE_SMOOTH);
        }
    }

    // this main method only exists so i can test/debug without having to go through the title screen
    public static void main(String[] args) throws Exception {
        new App("katie", "kairo");
    }
}