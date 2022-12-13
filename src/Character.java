public class Character {
    String name;
    int maxHP;
    int currentHP;
    private int player;
    private boolean isJumping;
    private boolean cosJumping;
    private boolean circularJumping;
    boolean isShooting = false;
    boolean isDisabled = false;
    private final double GRAVITY = 0.7;
    private double yVelocity = 0;
    int x;
    int y;
    int velocity;


    public Character(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        init();
    }

    public void hit(int hp) {
        currentHP += hp;
        isJumping = false;
        isDisabled = true;
        yVelocity = 12;
    }

    public void move(int direction) {
        if (!isShooting) {
            x += velocity*direction;
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
            if (y >= 200) {
                y = 200;
                isDisabled = false;
            }     
        }
    }

    public void jump() {
        if (!isJumping) {
            isJumping = true;
            yVelocity = 15;    
        }
    }

    public void sinusoidalJump() {
        if (!cosJumping) {
            cosJumping = true;
            yVelocity = 0;    
        }
    }

    public void circularJump() {
        if (!circularJumping) {
            circularJumping = true;
            yVelocity = 0;
        }
    }

    public void init() {
        if (name.equals("michael")) {
            maxHP = 250;
            velocity = 5;
        } else if (name.equals("ryan")) {
            maxHP = 125;
            velocity = 7;
        } else if (name.equals("anita")) {
            maxHP = 125;
            velocity = 7;
        } else if (name.equals("andrew")) {
            maxHP = 125;
            velocity = 7;
        } else if (name.equals("danyal")) {
            maxHP = 175;
            velocity = 5;
        }
        currentHP = maxHP;
    }
}