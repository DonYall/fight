public class Character {
    Character opponent;
    String name;
    int maxHP;
    int currentHP;
    double superProgress = 0;
    private int player;
    private boolean isJumping;
    private boolean cosJumping;
    private boolean circularJumping;
    boolean isShooting = false;
    boolean isFingering = false;
    boolean isDisabled = false;
    boolean isLost = false;
    boolean isSupering = false;
    private double GRAVITY = 0.7;
    private double yVelocity = 0;
    int x;
    int y;
    double velocity;
    int hitboxRadius = 35;

    public Character(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        init();
    }

    public void hit(int hp, int iVelocity) {
        currentHP += hp;
        isJumping = false;
        isDisabled = true;
        yVelocity = iVelocity;
        if (currentHP <= 0) {
            y = 0;
            GRAVITY = 0.01;
            isDisabled = true;
            isLost = true;
        } else {
            superProgress -= hp/3;
            opponent.superProgress -= hp;
            if (superProgress >= 100) {
                isSupering = true;
                superProgress = 0;
            }
            if (opponent.superProgress >= 100) {
                opponent.isSupering = true;
                opponent.superProgress = 0;
            }
        }
    }

    public void move(int direction) {
        if (!isShooting) {
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
            y = 200 - (int) (8*Math.sqrt(-Math.pow(yVelocity-10, 2)+100));
            yVelocity++;
            if (yVelocity >= 20) {
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
        } else if (name.equals("ryanpog")) {
            maxHP = 175;
            velocity = 7;
        } else if (name.equals("anita")) {
            maxHP = 125;
            velocity = 7;
        } else if (name.equals("andrew")) {
            maxHP = 175;
            velocity = 7;
        } else if (name.equals("danyal")) {
            maxHP = 175;
            velocity = 5;
        }
        currentHP = maxHP;
    }
}