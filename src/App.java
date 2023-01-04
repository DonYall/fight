import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.awt.image.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class App extends JFrame {
    private final double multiplier;
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
    private final JPanel gamePanel;
    private double accelerator = 0.1;
    private double p1Velocitator = 0;
    private double p1Rotator = 0;
    private double p2Velocitator = 0;
    private double p2Rotator = 0;
    private ArrayList<Character> p1Decoys = new ArrayList<>();
    private ArrayList<Character> p2Decoys = new ArrayList<>();
    private Timer repaintTimer;
    public App(String p1Name, String p2Name, double multiplier) throws IOException {
        this.multiplier = multiplier;
        p1Keys.put(1, false);
        p1Keys.put(-1, false);
        p2Keys.put(1, false);
        p2Keys.put(-1, false);

        p1 = new Character(p1Name, 100, 200);
        p2 = new Character(p2Name, 600, 200);
        p1.opponent = p2;
        p2.opponent = p1;

        // Background image
        BufferedImage bufferedBG = ImageIO.read(getClass().getResource("bg.jpg"));
        Image bgImage = bufferedBG.getScaledInstance((int) (800*multiplier), (int) (400*multiplier), BufferedImage.SCALE_SMOOTH);

        // Character images
        BufferedImage bufferedRyanPog = ImageIO.read(getClass().getResource(p1Name + ".png"));
        Image ryanpogIMG = bufferedRyanPog.getScaledInstance((int) (70*multiplier), (int) (70*multiplier), BufferedImage.SCALE_SMOOTH);

        BufferedImage bufferedMichelle = ImageIO.read(getClass().getResource(p2Name + ".png"));
        Image michelleIMG = bufferedMichelle.getScaledInstance((int) (70*multiplier), (int) (70*multiplier), BufferedImage.SCALE_SMOOTH);

        BufferedImage bufferedRyanPogF = ImageIO.read(getClass().getResource(p1Name + "F.png"));
        Image ryanpogIMGF = bufferedRyanPogF.getScaledInstance((int) (70*multiplier), (int) (70*multiplier), BufferedImage.SCALE_SMOOTH);

        BufferedImage bufferedMichelleF = ImageIO.read(getClass().getResource(p2Name + "F.png"));
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

                g2d.setColor(Color.BLACK.darker());
                if (p1Cutscene) {
                    p1CutsceneScale += p1CutsceneVelocity;
                    g2d.translate(68*multiplier, 217*multiplier);
                    g2d.scale(p1CutsceneScale, p1CutsceneScale);
                    g2d.translate(-68*multiplier, -217*multiplier);
                } else if (p2Cutscene) {
                    p2CutsceneScale += p2CutsceneVelocity;
                    g2d.translate(660*multiplier, 217*multiplier);
                    g2d.scale(p2CutsceneScale, p2CutsceneScale);
                    g2d.translate(-660*multiplier, -217*multiplier);
                }
                g2d.drawImage(bgImage, 0, 0, this);

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
                if (p1Direction == -1) {
                    drawCharacter(g2d, p1, ryanpogIMG);
                    if (p1.isShooting && p1.isSupering != Supers.DEEV_SUPER) {
                        g2d.drawImage(gunIMGF[gun1Index/3], (int) ((p1.x-40)*multiplier), (int) ((p1.y+20)*multiplier), null);
                    }
                    if (p1.isFingering) {
                        g2d.drawImage(fingerIMGF, (int) ((p1.x-10)*multiplier), (int) ((p1.y+40)*multiplier), null);
                    }
                } else {
                    drawCharacter(g2d, p1, ryanpogIMGF);
                    if (p1.isShooting && p1.isSupering != Supers.DEEV_SUPER) {
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

                // Show player hitboxes(int) (p1.x*multiplier), (int) (p1.y*multiplier), null
                // g2d.drawOval((int) ((p1.x)*multiplier), (int) ((p1.y)*multiplier), (int) (2*p1.hitboxRadius*multiplier), (int) (2*p1.hitboxRadius*multiplier));
                // g2d.drawOval((int) ((p2.x)*multiplier), (int) ((p2.y)*multiplier), (int) (2*p2.hitboxRadius*multiplier), (int) (2*p2.hitboxRadius*multiplier));

                // Health bars
                g2d.fillRect((int)(20*multiplier),           (int)(20*multiplier), (int)((300*p1.currentHP/p1.maxHP)*multiplier), (int)(25*multiplier));
                g2d.fillRect((int)((800-300-20)*multiplier), (int)(20*multiplier), (int)((300*p2.currentHP/p2.maxHP)*multiplier), (int)(25*multiplier));

                // Super progress bars
                if (p1.currentHP > 0) {
                    g2d.drawRect((int)(20*multiplier), (int)(50*multiplier), (int)(300*multiplier), (int)(10*multiplier));
                    g2d.fillRect((int)(20*multiplier), (int)(50*multiplier), (int)(p1.superProgress*(3)*multiplier), (int)(10*multiplier));
                }
                if (p2.currentHP > 0) {
                    g2d.drawRect((int)((800-300-20)*multiplier), (int)(50*multiplier), (int)(300*multiplier), (int)(10*multiplier));
                    g2d.fillRect((int)((800-300-20)*multiplier), (int)(50*multiplier), (int)(p2.superProgress*(3)*multiplier), (int)(10*multiplier));
                }

                g2d.dispose();
            }
        };
        InputMap im = gamePanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = gamePanel.getActionMap();

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
                } else if (!p1.isShooting && !p1.isDisabled) {
                    p1.jump();
                }
            }
        });
        am.put("pressed.g", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!p1.isShooting && !p1.isDisabled) {
                    p1.isShooting = true;
                    if (p1.isSupering != Supers.ANDREW_SUPER) {
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
                } else if (!p2.isShooting && !p2.isDisabled) {
                    p2.jump();
                }
            }
        });
        am.put("pressed.l", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!p2.isShooting && !p2.isDisabled) {
                    p2.isShooting = true;
                    if (p2.isSupering != Supers.ANDREW_SUPER) {
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

        add(gamePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize((int) (800*multiplier), (int) (400*multiplier));
        setUndecorated(true);
        setVisible(true);
        repaintTimer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long start = System.nanoTime();
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

                // Shoot gun
                if (p1.isShooting) {
                    gun1Index++;
                    if (gun1Index == 39) {
                        gun1Index = 0;
                        p1.isShooting = false;
                        if (p1.isSupering != Supers.ANDREW_SUPER) p1Movement = 0;
                    } else if (gun1Index == 24) {
                        if (p1.isSupering == Supers.ANDREW_SUPER) {
                            p2.hit(-10, 12);
                            p2Movement = 0;
                        } else if (p1Direction == -1) {
                            if ((p1Direction*((p2.x+35)-(p1.x-40+27)) > 0) && p1.y+20+20 > p2.y && p1.y+20+20 < p2.y+70) {
                                p2.hit(-10, 12);
                                p2Movement = 0;
                            } else if (p2.isSupering == Supers.DEEV_SUPER) {
                                p1.hit(0, 10);
                                p1Movement = 0;
                            }
                        } else {
                            if ((p1Direction*((p2.x+35)-(p1.x+50+27)) > 0) && p1.y+20+20 > p2.y && p1.y+20+20 < p2.y+70) {
                                p2.hit(-10, 12);
                                p2Movement = 0;
                            } else if (p2.isSupering == Supers.DEEV_SUPER) {
                                p1.hit(0, 10);
                                p1Movement = 0;
                            }
                        }        
                    }
                }
                if (p2.isShooting) {
                    gun2Index++;
                    if (gun2Index == 39) {
                        gun2Index = 0;
                        p2.isShooting = false;
                        if (p2.isSupering != Supers.ANDREW_SUPER) p2Movement = 0;
                    } else if (gun2Index == 24) {
                        if (p2.isSupering == Supers.ANDREW_SUPER) {
                            p1.hit(-10, 12);
                            p1Movement = 0;
                        } else if (p2Direction == +1) {
                            if ((p2Direction*((p1.x+35)-(p2.x+50+27)) > 0) && p2.y+20+20 > p1.y && p2.y+20+20 < p1.y+70) {
                                p1.hit(-10, 12);
                                p1Movement = 0;
                            } else if (p1.isSupering == Supers.DEEV_SUPER) {
                                p2.hit(0, 10);
                                p2Movement = 0;
                            }
                        } else {
                            if ((p2Direction*((p1.x+35)-(p2.x-40+27)) > 0) && p2.y+20+20 > p1.y && p2.y+20+20 < p1.y+70) {
                                p1.hit(-10, 12);
                                p1Movement = 0;
                            } else if (p1.isSupering == Supers.DEEV_SUPER) {
                                p2.hit(0, 10);
                                p2Movement = 0;
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
                        p2.hit((int)(-p1Velocitator*2), 7);
                        // Headbutt
                        if (p2.isSupering == Supers.RYAN_SUPER) {
                            p1.hit((int)(-p2Velocitator*2), 7);
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
                        p1.isSupering = 0;
                        p1Decoys.clear();
                    }
                } else if (!p1Cutscene && !p2Cutscene && p1.currentHP > 0) {
                    p1.superProgress += 0.05;
                    if (p1.superProgress >= 100) {
                        p1.isSupering = p1.SUPER;
                        if (p2.isSupering == 0 && p2.superProgress >= 99.95) {
                            p2.isSupering = p2.SUPER;
                            if (p2.isSupering == Supers.RYAN_SUPER) {
                                p2.superProgress = 0;
                            } else {
                                p2.superProgress = 100;
                            }
                        }
                        p1Cutscene = true;
                        p1.x = 100;
                        p2.x = 600;
                        p1.y = 200;
                        p2.y = 200;
                        p1.isDisabled = true;
                        p2.isDisabled = true;
                        if (p1.isSupering == Supers.RYAN_SUPER) {
                            p1.superProgress = 0;
                        }
                    }
                }
                if (p2.isSupering == Supers.RYAN_SUPER) {
                    if (Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2)) <= p1.hitboxRadius + p2.hitboxRadius) {
                        p1.hit((int)(-p2Velocitator*2), 7);
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
                        p2.isSupering = 0;
                        p2Decoys.clear();
                    }
                } else if (!p1Cutscene && !p2Cutscene && p2.currentHP > 0) {
                    p2.superProgress += 0.05;
                    if (p2.superProgress >= 100) {
                        p2.isSupering = p2.SUPER;
                        p2Cutscene = true;
                        p1.x = 100;
                        p2.x = 600;
                        p1.y = 200;
                        p2.y = 200;
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
                            p2.hit(-10, 7);
                            p2Movement = 0;
                        } else if (p2.isSupering == Supers.DEEV_SUPER) {
                            p1.hit(0, 10);
                            p1Movement = 0;
                        }
                    }
                }
                if (p2.isFingering) {
                    finger2Index++;
                    if (finger2Index == 1) {
                        if (Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2)) <= p1.hitboxRadius + p2.hitboxRadius) {
                            p1.hit(-10, 7);
                            p1Movement = 0;
                        } else if (p1.isSupering == Supers.DEEV_SUPER) {
                            p2.hit(0, 10);
                            p2Movement = 0;
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
                    if ((p1.isShooting && p1.isSupering != Supers.ANDREW_SUPER) || p1.isDisabled) return;
                    p1Movement = direction;
                    p1Direction = direction;
                    p1Keys.put(direction, true);
                } else {
                    if ((p2.isShooting && p2.isSupering != Supers.ANDREW_SUPER) || p2.isDisabled) return;
                    p2Movement = direction;
                    p2Direction = direction;
                    p2Keys.put(direction, true);
                }
            } else {
                if (playerID == 1) {
                    if ((p1.isShooting && p1.isSupering != Supers.ANDREW_SUPER) || p1.isDisabled) return;
                    p1Keys.put(direction, false);
                    if (!p1Keys.get(-direction)) {
                        p1Movement = 0;
                    } else {
                        p1Movement = -direction;
                    }
                } else {
                    if ((p2.isShooting && p2.isSupering != Supers.ANDREW_SUPER) || p2.isDisabled) return;
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
            }
        }
    }

    public int randInt(int min, int max) {
        return((int) (Math.random()*max)+min);
    }

    // this main method only exists so i can test/debug without having to go through the title screen
    public static void main(String[] args) throws Exception {
        new App("deev", "andrew", 1.7);
    }
}
