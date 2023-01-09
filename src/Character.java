public class Character {
    protected Character opponent;
    protected String name;
    protected int maxHP;
    protected int currentHP;
    protected double superProgress = 0;
    private boolean isJumping;
    private boolean cosJumping;
    private boolean circularJumping;
    protected boolean isShooting = false;
    protected boolean isFingering = false;
    protected boolean isDisabled = false;
    protected boolean isLost = false;
    protected int isSupering = 0;
    private double GRAVITY = 0.7;
    private double yVelocity = 0;
    protected int x;
    protected int y;
    protected double velocity;
    protected int hitboxRadius = 35;

    // Super attacks
    protected int SUPER;

    public Character(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        init();
    }

    public void hit(int hp, int iVelocity) {
        if (opponent.isSupering == Supers.ANDREW_SUPER) hp /= 1.7;
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
            if (opponent.isSupering == Supers.MK_SUPER) opponent.hit(-hp/2, 12);
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
            if (y >= 200) {
                isJumping = false;
                y = 200;
            }    
        } else if (cosJumping) {
            y = 200 - (int) (-100 * (Math.cos(yVelocity/8)) + 100);
            yVelocity++;
            if (yVelocity >= Math.PI * 16) {
                cosJumping = false;
                y = 200;
            }
        } else if (circularJumping) {
            y = 200 - (int) (8*Math.sqrt(-Math.pow(yVelocity-30, 2)+900));
            yVelocity++;
            if (yVelocity >= 60) {
                circularJumping = false;
                y = 200;
            }
        } else if (isDisabled) {
            yVelocity -= 1.2;
            y -= (int)yVelocity;
            if (y >= 200 && !isLost) {
                y = 200;
                isDisabled = false;
            }     
        }
    }

    // Realistic quadratic jump
    public void jump() {
        if (!isJumping) {
            isJumping = true;
            yVelocity = 15;
        }
    }

    // Sinusoidal jump
    public void sinusoidalJump() {
        if (!cosJumping) {
            cosJumping = true;
            yVelocity = 0;    
        }
    }

    // Circular jump
    public void circularJump() {
        if (!circularJumping) {
            circularJumping = true;
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
            velocity = 3;
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
        } else if (name.equals("danyal")) {
            maxHP = 175;
            velocity = 5;
            SUPER = Supers.DON_SUPER;
        } else if (name.equals("deev")) {
            maxHP = 150;
            velocity = 6;
            SUPER = Supers.DEEV_SUPER;
        } else if (name.equals("deev ai")) {
            maxHP = 2000;
            velocity = 5 + Math.random()*11;
        } else if (name.equals("steph")) {
            maxHP = 125;
            velocity = 7;
            SUPER = Supers.STEPH_SUPER;
        }
        currentHP = maxHP;
    }
}