import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import javax.swing.*;

public class SpaceInvaders extends JPanel implements ActionListener {

    static final int REGULAR_UFO = 1;
    static final int ELITE_UFO = 2;

    enum GameState {
        MENU,
        RUNNING,
        GAME_OVER,
        WIN
    }

    static class Balance {
        static final int FPS_DELAY_MS = 16;
        static final int STARTING_LIVES = 3;
        static final int MAX_LEVEL = 6;

        static final int SHIP_SPEED = 6;
        static final int PLAYER_BULLET_SPEED = -10;
        static final int ENEMY_DEATH_SHOT_SPEED = 5;
        static final int SMALL_METEOR_SPEED = 4;
        static final int BIG_METEOR_SPEED = 3;

        static final double ALIEN_BASE_SPEED = 0.9;
        static final double ALIEN_SPEED_PER_LEVEL = 0.18;
        static final int PLAYER_HIT_INVULNERABLE_TICKS = 60;

        static final int SCORE_REGULAR = 10;
        static final int SCORE_ELITE = 25;
        static final int SCORE_WAVE_CLEAR = 20;

        static double alienSpeedForLevel(int level) {
            // Levels 5 and 6 are easier — cap speed increase
            if (level >= 6) return ALIEN_BASE_SPEED + 2 * ALIEN_SPEED_PER_LEVEL;
            if (level >= 5) return ALIEN_BASE_SPEED + 3 * ALIEN_SPEED_PER_LEVEL;
            return ALIEN_BASE_SPEED + (level - 1) * ALIEN_SPEED_PER_LEVEL;
        }
    }

    static class MeteorProfile {
        final double smallSpawnChance;
        final double bigSpawnChance;

        MeteorProfile(double smallSpawnChance, double bigSpawnChance) {
            this.smallSpawnChance = smallSpawnChance;
            this.bigSpawnChance = bigSpawnChance;
        }
    }

    // board dimensions
    int tileSize = 32;
    int rows = 16;
    int cols = 16;

    int boarderwidth = tileSize * cols;
    int boarderheight = tileSize * rows;

    Image playerDefaultImg;
    Image playerDamagedImg;
    Image playerLeftImg;
    Image playerRightImg;
    Image lifeImg;
    Image laserRedImg;
    Image laserGreenImg;
    Image laserRedShotImg;
    Image laserGreenShotImg;
    Image explosionImg;
    Image meteorSmallImg;
    Image meteorBigImg;
    Image regularEnemyImg;
    Image eliteEnemyImg;
    Image eliteEnemyDamagedImg;

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive;
        boolean used;
        int health;
        int maxHealth;
        int enemyType;
        int col;
        int row;
        double preciseX;
        double preciseY;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
            this.alive = true;
            this.used = false;
            this.health = 1;
            this.maxHealth = 1;
            this.enemyType = REGULAR_UFO;
            this.col = -1;
            this.row = -1;
            this.preciseX = x;
            this.preciseY = y;
        }
    }

    class Projectile {
        int x;
        int y;
        int width;
        int height;
        int baseX;
        int baseY;
        int baseWidth;
        int baseHeight;
        int velocityY;
        Image img;
        boolean active;
        boolean harmful;
        int lifeTicks;
        int maxLifeTicks;

        Projectile(int x, int y, int width, int height, int velocityY, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.baseX = x;
            this.baseY = y;
            this.baseWidth = width;
            this.baseHeight = height;
            this.velocityY = velocityY;
            this.img = img;
            this.active = true;
            this.harmful = true;
            this.lifeTicks = -1;
            this.maxLifeTicks = -1;
        }

        Projectile(int x, int y, int width, int height, int velocityY, Image img, boolean harmful, int lifeTicks) {
            this(x, y, width, height, velocityY, img);
            this.harmful = harmful;
            this.lifeTicks = lifeTicks;
            this.maxLifeTicks = lifeTicks;
        }
    }

    class Meteor {
        int x;
        int y;
        int width;
        int height;
        int velocityY;
        Image img;
        boolean active;

        Meteor(int x, int y, int width, int height, int velocityY, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.velocityY = velocityY;
            this.img = img;
            this.active = true;
        }
    }

    // player ship
    int shipWidth = tileSize * 2;
    int shipHeight = tileSize;
    int shipX = tileSize * cols / 2 - tileSize;
    int shipY = tileSize * rows - tileSize * 2;
    int shipVelocity = Balance.SHIP_SPEED;
    Block ship;
    boolean movingLeft = false;
    boolean movingRight = false;
    boolean useRedPlayerShot = true;
    boolean playerDamaged = false;

    // aliens
    ArrayList<Block> alienArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienCols = 3;
    int alienCount = 0;
    int alienDirection = 1;
    double alienSpeed = 1;
    int level = 1;

    // player bullets
    LinkedList<Projectile> bulletArray;
    ArrayList<Projectile> enemyShotArray;
    ArrayList<Meteor> meteorArray;
    int bulletWidth = Math.max(4, tileSize / 8);
    int bulletHeight = tileSize / 2;
    int bulletVelocity = Balance.PLAYER_BULLET_SPEED;

    Timer gameLoop;
    boolean gameOver = false;
    int score = 0;
    int lives = Balance.STARTING_LIVES;
    int invulnerableTicks = 0;
    GameState gameState = GameState.MENU;
    Random random = new Random();
    Map<Integer, MeteorProfile> meteorProfiles;

    public SpaceInvaders() {
        setPreferredSize(new Dimension(boarderwidth, boarderheight));
        setBackground(Color.BLACK);
        setFocusable(true);
        setupKeyBindings();

        playerDefaultImg = loadImage("player.png");
        playerDamagedImg = loadImage("playerDamaged.png");
        playerLeftImg = loadImage("playerLeft.png");
        playerRightImg = loadImage("playerRight.png");
        lifeImg = loadImage("life.png");
        laserRedImg = loadImage("laserRed.png");
        laserGreenImg = loadImage("laserGreen.png");
        laserRedShotImg = loadImage("laserRedShot.png");
        laserGreenShotImg = loadImage("laserGreenShot.png");
        explosionImg = loadImage("explosion.png");
        meteorSmallImg = loadImage("meteorSmall.png");
        meteorBigImg = loadImage("meteorBig.png");
        regularEnemyImg = loadImage("enemyUFO.png");
        eliteEnemyImg = loadImage("enemyShip.png");
        eliteEnemyDamagedImg = loadImage("enemy2_damaged.png");

        ship = new Block(shipX, shipY, shipWidth, shipHeight, playerDefaultImg);
        alienArray = new ArrayList<>();
        bulletArray = new LinkedList<>();
        enemyShotArray = new ArrayList<>();
        meteorArray = new ArrayList<>();

        meteorProfiles = new HashMap<>();
        meteorProfiles.put(1, new MeteorProfile(0.0, 0.0));
        meteorProfiles.put(2, new MeteorProfile(0.015, 0.0));
        meteorProfiles.put(3, new MeteorProfile(0.01, 0.004));
        meteorProfiles.put(4, new MeteorProfile(0.008, 0.008));
        meteorProfiles.put(5, new MeteorProfile(0.012, 0.012));
        meteorProfiles.put(6, new MeteorProfile(0.006, 0.003));

        gameLoop = new Timer(Balance.FPS_DELAY_MS, this);
        gameLoop.start();
        
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (gameState == GameState.MENU) {
            drawMenu(g);
            return;
        }

        drawShip(g);
        drawAliens(g);
        drawBullets(g);
        drawEnemyShots(g);
        drawMeteors(g);
        drawHud(g);

        if (gameState == GameState.GAME_OVER) {
            drawGameOver(g);
        } else if (gameState == GameState.WIN) {
            drawWin(g);
        }
    }

    private void drawMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setFont(new Font("Arial", Font.BOLD, 46));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "SPACE INVADERS";
        g2d.setColor(new Color(80, 200, 255));
        g2d.drawString(title, boarderwidth / 2 - fm.stringWidth(title) / 2, boarderheight / 2 - 80);

        g2d.setFont(new Font("Arial", Font.ITALIC, 16));
        fm = g2d.getFontMetrics();
        String sub = "6 Levels of Alien Mayhem";
        g2d.setColor(new Color(255, 210, 60));
        g2d.drawString(sub, boarderwidth / 2 - fm.stringWidth(sub) / 2, boarderheight / 2 - 42);

        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        fm = g2d.getFontMetrics();
        String start = "Press ENTER to Start";
        g2d.setColor(Color.WHITE);
        g2d.drawString(start, boarderwidth / 2 - fm.stringWidth(start) / 2, boarderheight / 2 + 10);

        g2d.setFont(new Font("Arial", Font.PLAIN, 15));
        fm = g2d.getFontMetrics();
        String controls = "LEFT / RIGHT to Move  |  SPACE to Shoot";
        g2d.setColor(new Color(160, 160, 160));
        g2d.drawString(controls, boarderwidth / 2 - fm.stringWidth(controls) / 2, boarderheight / 2 + 45);
    }

    private void drawShip(Graphics g) {
        if (ship.img != null) {
            g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);
            return;
        }

        g.setColor(Color.GREEN);
        g.fillRect(ship.x, ship.y, ship.width, ship.height);
    }

    private void drawAliens(Graphics g) {
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (!alien.alive) {
                continue;
            }

            if (alien.img != null) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            } else {
                g.setColor(getAlienFallbackColor(alien));
                g.fillRect(alien.x, alien.y, alien.width, alien.height);
            }
        }
    }

    private void drawBullets(Graphics g) {
        g.setColor(Color.WHITE);
        for (int i = 0; i < bulletArray.size(); i++) {
            Projectile bullet = bulletArray.get(i);
            if (!bullet.active) {
                continue;
            }

            if (bullet.img != null) {
                g.drawImage(bullet.img, bullet.x, bullet.y, bullet.width, bullet.height, null);
            } else {
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }
    }

    private void drawEnemyShots(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < enemyShotArray.size(); i++) {
            Projectile shot = enemyShotArray.get(i);
            if (!shot.active) {
                continue;
            }

            if (!shot.harmful && shot.maxLifeTicks > 0 && shot.img != null) {
                // Explosion: grow scale + fade out
                float alpha = Math.max(0.05f, (float) shot.lifeTicks / shot.maxLifeTicks);
                Composite orig = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.drawImage(shot.img, shot.x, shot.y, shot.width, shot.height, null);
                g2d.setComposite(orig);
            } else if (shot.img != null) {
                g.drawImage(shot.img, shot.x, shot.y, shot.width, shot.height, null);
            } else {
                g.setColor(Color.PINK);
                g.fillRect(shot.x, shot.y, shot.width, shot.height);
            }
        }
    }

    private void drawMeteors(Graphics g) {
        g.setColor(new Color(170, 120, 60));
        for (int i = 0; i < meteorArray.size(); i++) {
            Meteor meteor = meteorArray.get(i);
            if (!meteor.active) {
                continue;
            }

            if (meteor.img != null) {
                g.drawImage(meteor.img, meteor.x, meteor.y, meteor.width, meteor.height, null);
            } else {
                g.fillOval(meteor.x, meteor.y, meteor.width, meteor.height);
            }
        }
    }

    private void drawHud(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Score: " + score, 10, 28);
        g.drawString("Level: " + level, 10, 54);
        drawLives(g);
    }

    private void drawLives(Graphics g) {
        int iconWidth = tileSize;
        int iconHeight = tileSize;
        int spacing = 4;
        int totalWidth = lives * iconWidth + Math.max(0, lives - 1) * spacing;
        int startX = boarderwidth - totalWidth - 10;
        int y = 10;

        for (int i = 0; i < lives; i++) {
            int x = startX + i * (iconWidth + spacing);
            if (lifeImg != null) {
                g.drawImage(lifeImg, x, y, iconWidth, iconHeight, null);
            } else {
                g.setColor(Color.GREEN);
                g.fillRect(x, y, iconWidth, iconHeight);
            }
        }
    }

    private void drawGameOver(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(0, 0, 0, 175));
        g2d.fillRect(0, 0, boarderwidth, boarderheight);

        // Rounded card background
        int cardW = 360, cardH = 200;
        int cardX = boarderwidth / 2 - cardW / 2;
        int cardY = boarderheight / 2 - cardH / 2 - 20;
        g2d.setColor(new Color(25, 25, 40));
        g2d.fillRoundRect(cardX, cardY, cardW, cardH, 24, 24);
        g2d.setColor(new Color(220, 50, 50, 180));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(cardX, cardY, cardW, cardH, 24, 24);

        g2d.setFont(new Font("Arial", Font.BOLD, 46));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "GAME OVER";
        g2d.setColor(new Color(220, 50, 50));
        g2d.drawString(title, boarderwidth / 2 - fm.stringWidth(title) / 2, cardY + 62);

        g2d.setFont(new Font("Arial", Font.BOLD, 21));
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.WHITE);
        String scoreStr = "Score: " + score;
        g2d.drawString(scoreStr, boarderwidth / 2 - fm.stringWidth(scoreStr) / 2, cardY + 104);
        String levelStr = "Level Reached: " + level;
        g2d.drawString(levelStr, boarderwidth / 2 - fm.stringWidth(levelStr) / 2, cardY + 133);

        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        fm = g2d.getFontMetrics();
        g2d.setColor(new Color(170, 170, 170));
        String prompt = "SPACE to Restart  |  ENTER for Menu";
        g2d.drawString(prompt, boarderwidth / 2 - fm.stringWidth(prompt) / 2, cardY + 172);
    }

    private void drawWin(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(0, 0, 20, 200));
        g2d.fillRect(0, 0, boarderwidth, boarderheight);

        // Rounded card background
        int cardW = 380, cardH = 200;
        int cardX = boarderwidth / 2 - cardW / 2;
        int cardY = boarderheight / 2 - cardH / 2 - 20;
        g2d.setColor(new Color(20, 30, 20));
        g2d.fillRoundRect(cardX, cardY, cardW, cardH, 24, 24);
        g2d.setColor(new Color(255, 215, 0, 180));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(cardX, cardY, cardW, cardH, 24, 24);

        g2d.setFont(new Font("Arial", Font.BOLD, 52));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "YOU WIN!";
        g2d.setColor(new Color(255, 215, 0));
        g2d.drawString(title, boarderwidth / 2 - fm.stringWidth(title) / 2, cardY + 66);

        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.WHITE);
        String scoreStr = "Final Score: " + score;
        g2d.drawString(scoreStr, boarderwidth / 2 - fm.stringWidth(scoreStr) / 2, cardY + 112);

        g2d.setFont(new Font("Arial", Font.PLAIN, 15));
        fm = g2d.getFontMetrics();
        g2d.setColor(new Color(170, 170, 170));
        String prompt = "Press ENTER to return to Menu";
        g2d.drawString(prompt, boarderwidth / 2 - fm.stringWidth(prompt) / 2, cardY + 160);
    }

    public void move() {
        if (gameState != GameState.RUNNING) {
            return;
        }

        updateShipMovement();
        moveAliens();
        moveBullets();
        moveEnemyShots();
        moveMeteors();
        spawnMeteors();
        cleanupBullets();
        cleanupEnemyShots();
        cleanupMeteors();

        if (invulnerableTicks > 0) {
            invulnerableTicks--;
        }

        if (alienCount == 0 && !gameOver) {
            startNextLevel();
        }
    }

    private void updateShipMovement() {
        if (movingLeft && !movingRight) {
            ship.x = Math.max(0, ship.x - shipVelocity);
        } else if (movingRight && !movingLeft) {
            ship.x = Math.min(boarderwidth - ship.width, ship.x + shipVelocity);
        }

        if (playerDamaged) {
            ship.img = playerDamagedImg != null ? playerDamagedImg : playerDefaultImg;
        } else if (movingLeft && !movingRight) {
            ship.img = playerLeftImg != null ? playerLeftImg : playerDefaultImg;
        } else if (movingRight && !movingLeft) {
            ship.img = playerRightImg != null ? playerRightImg : playerDefaultImg;
        } else {
            ship.img = playerDefaultImg;
        }
    }

    private void moveAliens() {
        boolean hitEdge = false;

        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (!alien.alive) {
                continue;
            }

            alien.preciseX += alienDirection * alienSpeed;
            alien.x = (int) Math.round(alien.preciseX);
            if (alien.x + alien.width >= boarderwidth || alien.x <= 0) {
                hitEdge = true;
            }

            if (alien.y + alien.height >= ship.y) {
                onPlayerHit();
                resetWavePosition();
                return;
            }
        }

        if (hitEdge) {
            alienDirection *= -1;
            for (int i = 0; i < alienArray.size(); i++) {
                Block alien = alienArray.get(i);
                alien.preciseX += alienDirection;
                alien.preciseY += alienHeight;
                alien.x = (int) Math.round(alien.preciseX);
                alien.y = (int) Math.round(alien.preciseY);
            }
        }
    }

    private void moveBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            Projectile bullet = bulletArray.get(i);
            bullet.y += bullet.velocityY;

            for (int j = 0; j < alienArray.size(); j++) {
                Block alien = alienArray.get(j);
                if (bullet.active && alien.alive && detectCollision(bullet, alien)) {
                    applyBulletHit(alien, bullet);
                }
            }
        }
    }

    private void applyBulletHit(Block alien, Projectile bullet) {
        bullet.active = false;
        alien.health--;

        if (alien.health <= 0) {
            alien.alive = false;
            alienCount--;
            score += alien.enemyType == ELITE_UFO ? Balance.SCORE_ELITE : Balance.SCORE_REGULAR;
            spawnEnemyDeathBlast(alien);
            return;
        }

        if (alien.enemyType == ELITE_UFO && eliteEnemyDamagedImg != null) {
            alien.img = eliteEnemyDamagedImg;
        }
    }

    private void spawnEnemyDeathBlast(Block alien) {
        int centerX = alien.x + alien.width / 2;
        int centerY = alien.y + alien.height / 2;
        int blastSize = tileSize + 10;

        enemyShotArray.add(new Projectile(
            centerX - blastSize / 2,
            centerY - blastSize / 2,
            blastSize,
            blastSize,
            0,
            explosionImg,
            false,
            10
        ));
    }

    private void moveEnemyShots() {
        for (int i = 0; i < enemyShotArray.size(); i++) {
            Projectile shot = enemyShotArray.get(i);
            if (shot.lifeTicks > 0) {
                if (!shot.harmful) {
                    animateExplosionShot(shot);
                }
                shot.lifeTicks--;
                if (shot.lifeTicks == 0) {
                    shot.active = false;
                    continue;
                }
            }

            shot.y += shot.velocityY;

            if (shot.active && shot.harmful && detectCollision(shot, ship)) {
                shot.active = false;
                onPlayerHit();
            }
        }
    }

    private void animateExplosionShot(Projectile shot) {
        if (shot.maxLifeTicks <= 0) {
            return;
        }

        double progress = 1.0 - ((double) shot.lifeTicks / shot.maxLifeTicks);
        double scale = 1.0 + progress * 0.8;
        int scaledWidth = Math.max(shot.baseWidth, (int) Math.round(shot.baseWidth * scale));
        int scaledHeight = Math.max(shot.baseHeight, (int) Math.round(shot.baseHeight * scale));
        int centerX = shot.baseX + shot.baseWidth / 2;
        int centerY = shot.baseY + shot.baseHeight / 2;

        shot.width = scaledWidth;
        shot.height = scaledHeight;
        shot.x = centerX - scaledWidth / 2;
        shot.y = centerY - scaledHeight / 2;
    }

    private void moveMeteors() {
        for (int i = 0; i < meteorArray.size(); i++) {
            Meteor meteor = meteorArray.get(i);
            meteor.y += meteor.velocityY;

            if (meteor.active && detectCollision(meteor, ship)) {
                meteor.active = false;
                onPlayerHit();
            }
        }
    }

    private void spawnMeteors() {
        MeteorProfile profile = meteorProfiles.getOrDefault(level, meteorProfiles.get(5));
        if (profile == null) {
            return;
        }

        Map<Integer, Block> launchers = getBottomAliveEnemiesByColumn();
        if (launchers.isEmpty()) {
            return;
        }

        for (Block launcher : launchers.values()) {
            double spawnMultiplier = getObjectSpawnMultiplierForLevel();
            if (profile.smallSpawnChance > 0 && random.nextDouble() < profile.smallSpawnChance * spawnMultiplier) {
                meteorArray.add(createMeteorFromEnemy(launcher, false));
            }
            if (profile.bigSpawnChance > 0 && random.nextDouble() < profile.bigSpawnChance * spawnMultiplier) {
                meteorArray.add(createMeteorFromEnemy(launcher, true));
            }
        }
    }

    private double getObjectSpawnMultiplierForLevel() {
        if (level == 6) return 0.5;
        if (level == 5) return 0.45;
        if (level == 4) return 0.3;
        if (level == 3) return 0.4;
        return 1.0;
    }

    private Meteor createMeteorFromEnemy(Block enemy, boolean bigMeteor) {
        int meteorSize = bigMeteor ? tileSize : Math.max(tileSize / 2, 12);
        int spawnX = enemy.x + enemy.width / 2 - meteorSize / 2;
        int spawnY = enemy.y + enemy.height;
        int speed = bigMeteor ? Balance.BIG_METEOR_SPEED : Balance.SMALL_METEOR_SPEED;
        Image img = bigMeteor ? meteorBigImg : meteorSmallImg;
        return new Meteor(spawnX, spawnY, meteorSize, meteorSize, speed, img);
    }

    private Map<Integer, Block> getBottomAliveEnemiesByColumn() {
        Map<Integer, Block> launchers = new HashMap<>();
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (!alien.alive) {
                continue;
            }

            Block current = launchers.get(alien.col);
            if (current == null || alien.row > current.row) {
                launchers.put(alien.col, alien);
            }
        }
        return launchers;
    }

    private void cleanupBullets() {
        while (!bulletArray.isEmpty() && (!bulletArray.getFirst().active || bulletArray.getFirst().y < -bulletHeight)) {
            bulletArray.removeFirst();
        }
    }

    private void cleanupEnemyShots() {
        while (!enemyShotArray.isEmpty() && (!enemyShotArray.get(0).active || enemyShotArray.get(0).y > boarderheight)) {
            enemyShotArray.remove(0);
        }
    }

    private void cleanupMeteors() {
        while (!meteorArray.isEmpty() && (!meteorArray.get(0).active || meteorArray.get(0).y > boarderheight)) {
            meteorArray.remove(0);
        }
    }

    private void onPlayerHit() {
        if (invulnerableTicks > 0 || gameState != GameState.RUNNING) {
            return;
        }

        lives--;
        playerDamaged = true;
        ship.img = playerDamagedImg != null ? playerDamagedImg : playerDefaultImg;
        invulnerableTicks = Balance.PLAYER_HIT_INVULNERABLE_TICKS;

        if (lives <= 0) {
            gameOver = true;
            gameState = GameState.GAME_OVER;
            return;
        }

        ship.x = shipX;
        ship.y = shipY;
        bulletArray.clear();
        enemyShotArray.clear();
        meteorArray.clear();
    }

    private void resetWavePosition() {
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            alien.preciseY = alienY + alien.row * alienHeight;
            alien.y = (int) Math.round(alien.preciseY);
        }
    }

    private void startNextLevel() {
        score += alienCols * alienRows * Balance.SCORE_WAVE_CLEAR;
        int nextLevel = level + 1;
        if (nextLevel > Balance.MAX_LEVEL) {
            gameState = GameState.WIN;
            return;
        }

        level = nextLevel;
        lives++;
        playerDamaged = false;
        ship.img = playerDefaultImg;
        alienCols = Math.min(alienCols + 1, cols / 2 - 2);
        alienRows = Math.min(alienRows + 1, rows - 6);
        bulletArray.clear();
        enemyShotArray.clear();
        meteorArray.clear();
        createAliens();
    }

    private void startLevel(int targetLevel) {
        level = Math.max(1, Math.min(Balance.MAX_LEVEL, targetLevel));
        alienDirection = 1;
        alienSpeed = Balance.alienSpeedForLevel(level);
        bulletArray.clear();
        enemyShotArray.clear();
        meteorArray.clear();
        createAliens();
    }

    private void createAliens() {
        alienArray.clear();
        alienSpeed = Balance.alienSpeedForLevel(level);

        if (level == 6) {
            createLevel6Formation();
            return;
        }

        for (int c = 0; c < alienCols; c++) {
            for (int r = 0; r < alienRows; r++) {
                Block alien = createAlienForLevel(c, r);
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    private void createLevel6Formation() {
        alienCols = 7;
        alienRows = 5;

        // ELITE_UFO positions form a "W" shape; all other cells are REGULAR_UFO
        //  E R R R R R E
        //  E R R E R R E
        //  E R R E R R E
        //  E R E R E R E
        //  R E R R R E R
        boolean[][] wShape = {
            { true,  false, false, false, false, false, true  },
            { true,  false, false, true, false, false, true  },
            { true,  false, false, true,  false, false, true  },
            { true,  false, true, false,  true, false, true  },
            { false, true,  false,  false, false,  true,  false },
        };

        for (int c = 0; c < alienCols; c++) {
            for (int r = 0; r < alienRows; r++) {
                int enemyType = wShape[r][c] ? ELITE_UFO : REGULAR_UFO;
                Image enemyImage = enemyType == ELITE_UFO ? eliteEnemyImg : regularEnemyImg;
                Block alien = new Block(alienX + c * alienWidth, alienY + r * alienHeight, alienWidth, alienHeight, enemyImage);
                alien.enemyType = enemyType;
                alien.maxHealth = enemyType == ELITE_UFO ? 2 : 1;
                alien.health = alien.maxHealth;
                alien.col = c;
                alien.row = r;
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    private Block createAlienForLevel(int col, int row) {
        int enemyType = pickEnemyTypeForLevel();
        Image enemyImage = enemyType == ELITE_UFO ? eliteEnemyImg : regularEnemyImg;
        Block alien = new Block(alienX + col * alienWidth, alienY + row * alienHeight, alienWidth, alienHeight, enemyImage);
        alien.enemyType = enemyType;
        alien.maxHealth = enemyType == ELITE_UFO ? 2 : 1;
        alien.health = alien.maxHealth;
        alien.col = col;
        alien.row = row;
        return alien;
    }

    private int pickEnemyTypeForLevel() {
        if (level == 1 || level == 6) return REGULAR_UFO;
        if (level == 2 || level == 5) return random.nextBoolean() ? REGULAR_UFO : ELITE_UFO;
        return ELITE_UFO; // levels 3, 4
    }

    private boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    private boolean detectCollision(Projectile a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    private boolean detectCollision(Meteor a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    private Color getAlienFallbackColor(Block alien) {
        if (alien.enemyType == ELITE_UFO) {
            return alien.health == 1 ? Color.ORANGE : Color.RED;
        }
        return Color.CYAN;
    }

    private Image loadImage(String... fileNames) {
        for (String fileName : fileNames) {
            File directFile = new File(fileName);
            if (directFile.exists()) {
                return new ImageIcon(directFile.getPath()).getImage();
            }

            File imageDirFile = new File("images", fileName);
            if (imageDirFile.exists()) {
                return new ImageIcon(imageDirFile.getPath()).getImage();
            }
        }
        return null;
    }

    private void setupKeyBindings() {
        bindKey("ENTER", "startGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameState == GameState.MENU) {
                    startNewGame();
                } else if (gameState == GameState.WIN || gameState == GameState.GAME_OVER) {
                    gameState = GameState.MENU;
                    if (!gameLoop.isRunning()) gameLoop.start();
                }
            }
        });

        bindKey("pressed LEFT", "moveLeftPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameState == GameState.RUNNING) {
                    movingLeft = true;
                }
            }
        });

        bindKey("released LEFT", "moveLeftReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movingLeft = false;
            }
        });

        bindKey("pressed RIGHT", "moveRightPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameState == GameState.RUNNING) {
                    movingRight = true;
                }
            }
        });

        bindKey("released RIGHT", "moveRightReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movingRight = false;
            }
        });

        bindKey("SPACE", "fireOrRestart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameState == GameState.GAME_OVER) {
                    restartGame();
                    return;
                }
                if (gameState == GameState.RUNNING) {
                    fireBullet();
                }
            }
        });
    }

    private void bindKey(String keyStroke, String actionKey, Action action) {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(keyStroke), actionKey);
        actionMap.put(actionKey, action);
    }

    private void restartGame() {
        startNewGame();
    }

    private void startNewGame() {
        ship.x = shipX;
        ship.y = shipY;
        ship.img = playerDefaultImg;
        playerDamaged = false;
        movingLeft = false;
        movingRight = false;
        useRedPlayerShot = true;
        bulletArray.clear();
        enemyShotArray.clear();
        meteorArray.clear();
        gameOver = false;
        gameState = GameState.RUNNING;
        score = 0;
        lives = Balance.STARTING_LIVES;
        invulnerableTicks = 0;
        alienCols = 3;
        alienRows = 2;
        startLevel(1);
        gameLoop.start();
    }

    private void fireBullet() {
        Image bulletImg;
        if (level == 1) {
            bulletImg = laserRedImg;
        } else {
            bulletImg = useRedPlayerShot ? laserRedImg : laserGreenImg;
            useRedPlayerShot = !useRedPlayerShot;
        }

        int bulletX = ship.x + shipWidth / 2 - bulletWidth / 2;
        int bulletY = ship.y - bulletHeight;
        Projectile bullet = new Projectile(bulletX, bulletY, bulletWidth + 4, bulletHeight + 2, bulletVelocity, bulletImg);
        bulletArray.add(bullet);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameState == GameState.GAME_OVER || gameState == GameState.WIN) {
            gameLoop.stop();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Space Invaders");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            SpaceInvaders game = new SpaceInvaders();
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            game.requestFocusInWindow();
        });
    }
}
