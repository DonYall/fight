import java.util.ArrayList;

public class Character {
    protected Character opponent;
    protected String name;
    protected int maxHP;
    protected int currentHP;
    protected double superProgress = 0;
    private boolean isJumping;
    private boolean cosJumping;
    protected boolean circularJumping;
    private int doubleJumpFloor = 250;
    protected boolean isShooting = false;
    protected boolean isFingering = false;
    protected boolean isDisabled = false;
    protected boolean isLost = false;
    protected int isSupering = 0;
    private double GRAVITY = 0.7;
    protected double yVelocity = 0;
    protected int x;
    protected int y;
    protected double velocity;
    protected int hitboxRadius = 35;
    protected int bombs = 0;
    protected ArrayList<Character> yoshis = new ArrayList<>();
    protected int SUPER;

    public Character(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        init();
    }

    public void hit(int hp, int iVelocity) {
        if (opponent.isSupering == Supers.DON_SUPER) { // don super
            if (iVelocity == 40) {
                hp = -bombs*13;
                bombs = 0;
            } else {
                bombs++;
                return;
            }
        }
        currentHP += hp;
        isJumping = false;
        circularJumping = false;
        isDisabled = true;
        yVelocity = iVelocity;
        if (currentHP <= 0) {
            currentHP = 0;
            y = 0;
            GRAVITY = 0.01;
            isLost = true;
        } else if (currentHP > maxHP) {
            currentHP = maxHP;
        } else if (hp < 0) {
            if (opponent.isSupering == Supers.MK_SUPER) {
                opponent.currentHP += ((int)(-hp/1.4));
                if (opponent.currentHP <= 0) {
                    opponent.currentHP = 0;
                    opponent.y = 0;
                    opponent.GRAVITY = 0.01;
                    opponent.isLost = true;
                } else if (opponent.currentHP > opponent.maxHP) {
                    opponent.currentHP = opponent.maxHP;
                }
            }
            if (isSupering == 0) {
                superProgress -= hp/3;
            }
            if (opponent.isSupering == 0) {
                opponent.superProgress -= hp;
            }
            if (superProgress >= 100) {
                superProgress = 99.95;
            }
            if (opponent.superProgress >= 100) {
                opponent.superProgress = 99.95;
            }
        }
    }

    public void move(int direction) {
        if (!(isShooting && isSupering != Supers.ANDREW_SUPER)) {
            x += velocity*direction;
            if (x < 0) {
                x = 0;
            } else if (x > 800-70) {
                x = 800-70;
            }
        }
        if (isJumping) {
            yVelocity -= GRAVITY;
            y -= (int)yVelocity;
            if (y >= 250) {
                isJumping = false;
                y = 250;
            }
        } else if (cosJumping) {
            yVelocity++;
            y = doubleJumpFloor - (int) (-50 * (Math.cos(yVelocity/8)) + 50);
            if (y == doubleJumpFloor) {
                cosJumping = false;
                isJumping = true;
                yVelocity = 250 - 0.7 * Math.pow(y, 2);
            }
        } else if (circularJumping) {
            y = doubleJumpFloor - (int) (8*Math.sqrt(-Math.pow(yVelocity-30, 2)+900));
            yVelocity++;
            if (yVelocity >= 60) {
                circularJumping = false;
                y = 250;
            }
        } else if (isDisabled) {
            if (opponent.isSupering == Supers.MK_SUPER) {
                try {
                    int ddirection = (opponent.x - x) / (Math.abs(opponent.x - x));
                    x += 6*ddirection;    
                } catch (ArithmeticException e) { // Division by 0
                    // do nothing lol
                }
            }
            yVelocity -= 1.2;
            y -= (int)yVelocity;
            if (y >= 250 && !isLost) {
                y = 250;
                isDisabled = false;
            }     
        }
    }

    // Realistic quadratic jump
    public void jump() {
        if (!isJumping) {
            isJumping = true;
            yVelocity = 15;
        } else if (isSupering == Supers.JOSEPH_SUPER) {
            doubleJumpFloor = y;
            circularJump();
        }
    }

    // Sinusoidal jump
    public void sinusoidalJump() {
        if (!cosJumping) {
            doubleJumpFloor = y;
            cosJumping = true;
            yVelocity = 0;    
        }
    }

    // Circular jump
    public void circularJump() {
        if (!circularJumping && !isDisabled) {
            yoshis.add(new Character("yoshi", x+23, y+70));
            yoshis.get(yoshis.size()-1).circularJumping = true;
            yoshis.get(yoshis.size()-1).yVelocity = 30;
            circularJumping = true;
            isJumping = false;
            yVelocity = 0;
        }
    }

    public int[] getHitboxCenter(int width, int height) {
        return (new int[] {(int) (x + width/2), (int) (y + height/2)});
    }

    // Initialize character stats
    public void init() {
        if (name.equals("mk")) {
            maxHP = 250;
            velocity = 4;
            SUPER = Supers.MK_SUPER;
        } else if (name.equals("ryanpog")) {
            maxHP = 175;
            velocity = 7;
            SUPER = Supers.RYAN_SUPER;
        } else if (name.equals("anita")) {
            maxHP = 125;
            velocity = 7;
            SUPER = Supers.ANITA_SUPER;
        } else if (name.equals("andrew")) {
            maxHP = 175;
            velocity = 7;
            SUPER = Supers.ANDREW_SUPER;
        } else if (name.equals("don")) {
            maxHP = 175;
            velocity = 5;
            SUPER = Supers.DON_SUPER;
        } else if (name.equals("deev")) {
            maxHP = 175;
            velocity = 6;
            SUPER = Supers.DEEV_SUPER;
        } else if (name.equals("deev ai")) {
            maxHP = 2000;
            velocity = 5 + Math.random()*11;
        } else if (name.equals("steph")) {
            maxHP = 125;
            velocity = 7;
            SUPER = Supers.STEPH_SUPER;
        } else if (name.equals("joseph")) {
            maxHP = 150;
            velocity = 6;
            SUPER = Supers.JOSEPH_SUPER;
        } else {
            maxHP = 700;
            velocity = 5;
            name += " (bro is cheating)";
            SUPER = 100;
        }
        currentHP = maxHP;
    }
}