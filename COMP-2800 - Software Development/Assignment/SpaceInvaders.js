const REGULAR_UFO = 1;
const ELITE_UFO = 2;

const GameState = {
    MENU: 'MENU',
    RUNNING: 'RUNNING',
    GAME_OVER: 'GAME_OVER',
    WIN: 'WIN'
};

const Balance = {
    FPS_DELAY_MS: 16,
    STARTING_LIVES: 3,
    MAX_LEVEL: 6,

    SHIP_SPEED: 6,
    PLAYER_BULLET_SPEED: -10,
    ENEMY_DEATH_SHOT_SPEED: 5,
    SMALL_METEOR_SPEED: 4,
    BIG_METEOR_SPEED: 3,

    ALIEN_BASE_SPEED: 0.9,
    ALIEN_SPEED_PER_LEVEL: 0.18,
    PLAYER_HIT_INVULNERABLE_TICKS: 60,

    SCORE_REGULAR: 10,
    SCORE_ELITE: 25,
    SCORE_WAVE_CLEAR: 20,

    alienSpeedForLevel(level) {
        if (level >= 6) return Balance.ALIEN_BASE_SPEED + (4 - 1) * Balance.ALIEN_SPEED_PER_LEVEL;
        if (level >= 5) return Balance.ALIEN_BASE_SPEED + (3 - 1) * Balance.ALIEN_SPEED_PER_LEVEL;
        return Balance.ALIEN_BASE_SPEED + (level - 1) * Balance.ALIEN_SPEED_PER_LEVEL;
    }
};

class MeteorProfile {
    constructor(smallSpawnChance, bigSpawnChance) {
        this.smallSpawnChance = smallSpawnChance;
        this.bigSpawnChance = bigSpawnChance;
    }
}

class Block {
    constructor(x, y, width, height, img) {
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
    constructor(x, y, width, height, velocityY, img, harmful = true, lifeTicks = -1) {
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
        this.harmful = harmful;
        this.lifeTicks = lifeTicks;
        this.maxLifeTicks = lifeTicks;
    }
}

class Meteor {
    constructor(x, y, width, height, velocityY, img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityY = velocityY;
        this.img = img;
        this.active = true;
    }
}

class SpaceInvaders {
    constructor(canvas) {
        this.canvas = canvas;
        this.ctx = canvas.getContext('2d');

        // Board dimensions
        this.tileSize = 32;
        this.rows = 16;
        this.cols = 16;

        this.boarderwidth = this.tileSize * this.cols;
        this.boarderheight = this.tileSize * this.rows;

        canvas.width = this.boarderwidth;
        canvas.height = this.boarderheight;

        // Images
        this.playerDefaultImg = null;
        this.playerDamagedImg = null;
        this.playerLeftImg = null;
        this.playerRightImg = null;
        this.lifeImg = null;
        this.laserRedImg = null;
        this.laserGreenImg = null;
        this.laserRedShotImg = null;
        this.laserGreenShotImg = null;
        this.explosionImg = null;
        this.meteorSmallImg = null;
        this.meteorBigImg = null;
        this.regularEnemyImg = null;
        this.eliteEnemyImg = null;

        // Player ship
        this.shipWidth = this.tileSize * 2;
        this.shipHeight = this.tileSize;
        this.shipX = this.tileSize * this.cols / 2 - this.tileSize;
        this.shipY = this.tileSize * this.rows - this.tileSize * 2;
        this.shipVelocity = Balance.SHIP_SPEED;
        this.ship = new Block(this.shipX, this.shipY, this.shipWidth, this.shipHeight, this.playerDefaultImg);
        this.movingLeft = false;
        this.movingRight = false;
        this.useRedPlayerShot = true;
        this.playerDamaged = false;

        // Aliens
        this.alienArray = [];
        this.alienWidth = this.tileSize * 2;
        this.alienHeight = this.tileSize;
        this.alienX = this.tileSize;
        this.alienY = this.tileSize;
        this.alienRows = 2;
        this.alienCols = 3;
        this.alienCount = 0;
        this.alienDirection = 1;
        this.alienSpeed = 1;
        this.level = 1;

        // Projectiles
        this.bulletArray = [];
        this.enemyShotArray = [];
        this.meteorArray = [];
        this.bulletWidth = Math.max(4, this.tileSize / 8);
        this.bulletHeight = this.tileSize / 2;
        this.bulletVelocity = Balance.PLAYER_BULLET_SPEED;

        // Game state
        this.gameOver = false;
        this.score = 0;
        this.lives = Balance.STARTING_LIVES;
        this.invulnerableTicks = 0;
        this.gameState = GameState.MENU;

        // Meteor profiles
        this.meteorProfiles = new Map([
            [1, new MeteorProfile(0.0, 0.0)],
            [2, new MeteorProfile(0.015, 0.0)],
            [3, new MeteorProfile(0.01, 0.004)],
            [4, new MeteorProfile(0.008, 0.008)],
            [5, new MeteorProfile(0.012, 0.012)],
            [6, new MeteorProfile(0.006, 0.003)]
        ]);

        this.loadImages();
        this.setupKeyBindings();
        this.startGameLoop();
    }

    loadImages() {
        // Load images from the images folder
        this.playerDefaultImg = this.loadImage('player.png');
        this.playerDamagedImg = this.loadImage('playerDamaged.png');
        this.playerLeftImg = this.loadImage('playerLeft.png');
        this.playerRightImg = this.loadImage('playerRight.png');
        this.lifeImg = this.loadImage('life.png');
        this.laserRedImg = this.loadImage('laserRed.png');
        this.laserGreenImg = this.loadImage('laserGreen.png');
        this.laserRedShotImg = this.loadImage('laserRedShot.png');
        this.laserGreenShotImg = this.loadImage('laserGreenShot.png');
        this.explosionImg = this.loadImage('explosion.png');
        this.meteorSmallImg = this.loadImage('meteorSmall.png');
        this.meteorBigImg = this.loadImage('meteorBig.png');
        this.regularEnemyImg = this.loadImage('enemyUFO.png');
        this.eliteEnemyImg = this.loadImage('enemyShip.png');
    }

    loadImage(filename) {
        const img = new Image();
        img.onerror = () => {
            console.warn(`Failed to load image: images/${filename}`);
            img.loaded = false;
        };
        img.onload = () => {
            img.loaded = true;
        };
        img.src = 'images/' + filename;
        return img;
    }

    isImageValid(img) {
        if (!img) return false;
        if (img.loaded === false) return false;
        if (!img.complete) return false;
        if (img.naturalWidth === 0 || img.naturalHeight === 0) return false;
        return true;
    }

    draw() {
        this.ctx.fillStyle = 'black';
        this.ctx.fillRect(0, 0, this.boarderwidth, this.boarderheight);

        if (this.gameState === GameState.MENU) {
            this.drawMenu();
            return;
        }

        this.drawShip();
        this.drawAliens();
        this.drawBullets();
        this.drawEnemyShots();
        this.drawMeteors();
        this.drawHud();

        if (this.gameState === GameState.GAME_OVER) {
            this.drawGameOver();
        } else if (this.gameState === GameState.WIN) {
            this.drawWin();
        }
    }

    drawMenu() {
        this.ctx.font = 'bold 46px Arial';
        this.ctx.fillStyle = 'rgb(80, 200, 255)';
        this.ctx.textAlign = 'center';
        this.ctx.fillText('SPACE INVADERS', this.boarderwidth / 2, this.boarderheight / 2 - 80);

        this.ctx.font = 'italic 16px Arial';
        this.ctx.fillStyle = 'rgb(255, 210, 60)';
        this.ctx.fillText('6 Levels of Alien Mayhem', this.boarderwidth / 2, this.boarderheight / 2 - 42);

        this.ctx.font = 'bold 22px Arial';
        this.ctx.fillStyle = 'white';
        this.ctx.fillText('Press ENTER to Start', this.boarderwidth / 2, this.boarderheight / 2 + 10);

        this.ctx.font = 'normal 15px Arial';
        this.ctx.fillStyle = 'rgb(160, 160, 160)';
        this.ctx.fillText('LEFT / RIGHT to Move  |  SPACE to Shoot', this.boarderwidth / 2, this.boarderheight / 2 + 45);
    }

    drawShip() {
        if (this.isImageValid(this.ship.img)) {
            this.ctx.drawImage(this.ship.img, this.ship.x, this.ship.y, this.ship.width, this.ship.height);
            return;
        }

        this.ctx.fillStyle = 'lime';
        this.ctx.fillRect(this.ship.x, this.ship.y, this.ship.width, this.ship.height);
    }

    drawAliens() {
        for (let i = 0; i < this.alienArray.length; i++) {
            const alien = this.alienArray[i];
            if (!alien.alive) continue;

            if (this.isImageValid(alien.img)) {
                this.ctx.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height);
            } else {
                this.ctx.fillStyle = this.getAlienFallbackColor(alien);
                this.ctx.fillRect(alien.x, alien.y, alien.width, alien.height);
            }
        }
    }

    drawBullets() {
        this.ctx.fillStyle = 'white';
        for (let i = 0; i < this.bulletArray.length; i++) {
            const bullet = this.bulletArray[i];
            if (!bullet.active) continue;

            if (this.isImageValid(bullet.img)) {
                this.ctx.drawImage(bullet.img, bullet.x, bullet.y, bullet.width, bullet.height);
            } else {
                this.ctx.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }
    }

    drawEnemyShots() {
        for (let i = 0; i < this.enemyShotArray.length; i++) {
            const shot = this.enemyShotArray[i];
            if (!shot.active) continue;

            if (!shot.harmful && shot.maxLifeTicks > 0 && this.isImageValid(shot.img)) {
                this.ctx.globalAlpha = Math.max(0, shot.lifeTicks / shot.maxLifeTicks);
                this.ctx.drawImage(shot.img, shot.x, shot.y, shot.width, shot.height);
                this.ctx.globalAlpha = 1.0;
            } else if (this.isImageValid(shot.img)) {
                this.ctx.drawImage(shot.img, shot.x, shot.y, shot.width, shot.height);
            } else {
                this.ctx.fillStyle = 'red';
                this.ctx.fillRect(shot.x, shot.y, shot.width, shot.height);
            }
        }
    }

    drawMeteors() {
        this.ctx.fillStyle = 'rgb(170, 120, 60)';
        for (let i = 0; i < this.meteorArray.length; i++) {
            const meteor = this.meteorArray[i];
            if (!meteor.active) continue;

            if (this.isImageValid(meteor.img)) {
                this.ctx.drawImage(meteor.img, meteor.x, meteor.y, meteor.width, meteor.height);
            } else {
                this.ctx.fillRect(meteor.x, meteor.y, meteor.width, meteor.height);
            }
        }
    }

    drawHud() {
        this.ctx.fillStyle = 'white';
        this.ctx.font = 'bold 22px Arial';
        this.ctx.textAlign = 'left';
        this.ctx.fillText('Score: ' + this.score, 10, 28);
        this.ctx.fillText('Level: ' + this.level, 10, 54);
        this.drawLives();
    }

    drawLives() {
        const iconWidth = this.tileSize;
        const iconHeight = this.tileSize;
        const spacing = 4;
        const totalWidth = this.lives * iconWidth + Math.max(0, this.lives - 1) * spacing;
        const startX = this.boarderwidth - totalWidth - 10;
        const y = 10;

        for (let i = 0; i < this.lives; i++) {
            const x = startX + i * (iconWidth + spacing);
            if (this.isImageValid(this.lifeImg)) {
                this.ctx.drawImage(this.lifeImg, x, y, iconWidth, iconHeight);
            } else {
                this.ctx.fillStyle = 'yellow';
                this.ctx.fillRect(x, y, iconWidth, iconHeight);
            }
        }
    }

    drawGameOver() {
        this.ctx.fillStyle = 'rgba(0, 0, 0, 0.7)';
        this.ctx.fillRect(0, 0, this.boarderwidth, this.boarderheight);

        const cardW = 360, cardH = 200;
        const cardX = this.boarderwidth / 2 - cardW / 2;
        const cardY = this.boarderheight / 2 - cardH / 2 - 20;

        this.ctx.fillStyle = 'rgb(25, 25, 40)';
        this.roundRect(cardX, cardY, cardW, cardH, 24);
        this.ctx.fill();

        this.ctx.strokeStyle = 'rgba(220, 50, 50, 0.7)';
        this.ctx.lineWidth = 2.5;
        this.roundRect(cardX, cardY, cardW, cardH, 24);
        this.ctx.stroke();

        this.ctx.font = 'bold 46px Arial';
        this.ctx.fillStyle = 'rgb(220, 50, 50)';
        this.ctx.textAlign = 'center';
        this.ctx.fillText('GAME OVER', this.boarderwidth / 2, cardY + 62);

        this.ctx.font = 'bold 21px Arial';
        this.ctx.fillStyle = 'white';
        this.ctx.fillText('Score: ' + this.score, this.boarderwidth / 2, cardY + 104);
        this.ctx.fillText('Level Reached: ' + this.level, this.boarderwidth / 2, cardY + 133);

        this.ctx.font = 'normal 14px Arial';
        this.ctx.fillStyle = 'rgb(170, 170, 170)';
        this.ctx.fillText('SPACE to Restart  |  ENTER for Menu', this.boarderwidth / 2, cardY + 172);
    }

    drawWin() {
        this.ctx.fillStyle = 'rgba(0, 0, 20, 0.8)';
        this.ctx.fillRect(0, 0, this.boarderwidth, this.boarderheight);

        const cardW = 380, cardH = 200;
        const cardX = this.boarderwidth / 2 - cardW / 2;
        const cardY = this.boarderheight / 2 - cardH / 2 - 20;

        this.ctx.fillStyle = 'rgb(20, 30, 20)';
        this.roundRect(cardX, cardY, cardW, cardH, 24);
        this.ctx.fill();

        this.ctx.strokeStyle = 'rgba(255, 215, 0, 0.7)';
        this.ctx.lineWidth = 2.5;
        this.roundRect(cardX, cardY, cardW, cardH, 24);
        this.ctx.stroke();

        this.ctx.font = 'bold 52px Arial';
        this.ctx.fillStyle = 'rgb(255, 215, 0)';
        this.ctx.textAlign = 'center';
        this.ctx.fillText('YOU WIN!', this.boarderwidth / 2, cardY + 66);

        this.ctx.font = 'bold 24px Arial';
        this.ctx.fillStyle = 'white';
        this.ctx.fillText('Final Score: ' + this.score, this.boarderwidth / 2, cardY + 112);

        this.ctx.font = 'normal 15px Arial';
        this.ctx.fillStyle = 'rgb(170, 170, 170)';
        this.ctx.fillText('Press ENTER to return to Menu', this.boarderwidth / 2, cardY + 160);
    }

    roundRect(x, y, width, height, radius) {
        this.ctx.beginPath();
        this.ctx.moveTo(x + radius, y);
        this.ctx.lineTo(x + width - radius, y);
        this.ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
        this.ctx.lineTo(x + width, y + height - radius);
        this.ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
        this.ctx.lineTo(x + radius, y + height);
        this.ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
        this.ctx.lineTo(x, y + radius);
        this.ctx.quadraticCurveTo(x, y, x + radius, y);
        this.ctx.closePath();
    }

    move() {
        if (this.gameState !== GameState.RUNNING) {
            return;
        }

        this.updateShipMovement();
        this.moveAliens();
        this.moveBullets();
        this.moveEnemyShots();
        this.moveMeteors();
        this.spawnMeteors();
        this.cleanupBullets();
        this.cleanupEnemyShots();
        this.cleanupMeteors();

        if (this.invulnerableTicks > 0) {
            this.invulnerableTicks--;
        }

        if (this.alienCount === 0 && !this.gameOver) {
            this.startNextLevel();
        }
    }

    updateShipMovement() {
        if (this.movingLeft && !this.movingRight) {
            this.ship.x = Math.max(0, this.ship.x - this.shipVelocity);
        } else if (this.movingRight && !this.movingLeft) {
            this.ship.x = Math.min(this.boarderwidth - this.shipWidth, this.ship.x + this.shipVelocity);
        }

        if (this.playerDamaged) {
            this.ship.img = this.playerDamagedImg !== null ? this.playerDamagedImg : this.playerDefaultImg;
        } else if (this.movingLeft && !this.movingRight) {
            this.ship.img = this.playerLeftImg !== null ? this.playerLeftImg : this.playerDefaultImg;
        } else if (this.movingRight && !this.movingLeft) {
            this.ship.img = this.playerRightImg !== null ? this.playerRightImg : this.playerDefaultImg;
        } else {
            this.ship.img = this.playerDefaultImg;
        }
    }

    moveAliens() {
        let hitEdge = false;

        for (let i = 0; i < this.alienArray.length; i++) {
            const alien = this.alienArray[i];
            if (!alien.alive) continue;

            alien.preciseX += this.alienDirection * this.alienSpeed;
            alien.x = Math.round(alien.preciseX);
            if (alien.x + alien.width >= this.boarderwidth || alien.x <= 0) {
                hitEdge = true;
            }

            if (alien.y + alien.height >= this.ship.y) {
                this.gameOver = true;
                this.gameState = GameState.GAME_OVER;
            }
        }

        if (hitEdge) {
            this.alienDirection *= -1;
            for (let i = 0; i < this.alienArray.length; i++) {
                const alien = this.alienArray[i];
                alien.preciseX += this.alienDirection * this.alienSpeed * 2;
                alien.preciseY += this.alienHeight;
                alien.x = Math.round(alien.preciseX);
                alien.y = Math.round(alien.preciseY);
            }
        }
    }

    moveBullets() {
        for (let i = 0; i < this.bulletArray.length; i++) {
            const bullet = this.bulletArray[i];
            bullet.y += bullet.velocityY;

            for (let j = 0; j < this.alienArray.length; j++) {
                const alien = this.alienArray[j];
                if (alien.alive && this.detectCollision(bullet, alien)) {
                    this.applyBulletHit(alien, bullet);
                    break;
                }
            }
        }
    }

    applyBulletHit(alien, bullet) {
        bullet.active = false;

        alien.health--;

        if (alien.health <= 0) {
            alien.alive = false;
            this.alienCount--;
            this.score += alien.enemyType === ELITE_UFO ? Balance.SCORE_ELITE : Balance.SCORE_REGULAR;
            this.spawnEnemyDeathBlast(alien);
            return;
        }
    }

    spawnEnemyDeathBlast(alien) {
        const centerX = alien.x + alien.width / 2;
        const centerY = alien.y + alien.height / 2;
        const blastSize = this.tileSize + 10;

        this.enemyShotArray.push(new Projectile(
            centerX - blastSize / 2,
            centerY - blastSize / 2,
            blastSize,
            blastSize,
            0,
            this.explosionImg,
            false,
            10
        ));
    }

    moveEnemyShots() {
        for (let i = 0; i < this.enemyShotArray.length; i++) {
            const shot = this.enemyShotArray[i];
            if (shot.lifeTicks > 0) {
                shot.lifeTicks--;
                this.animateExplosionShot(shot);
                if (shot.lifeTicks === 0) {
                    shot.active = false;
                }
            }

            shot.y += shot.velocityY;

            if (shot.active && shot.harmful && this.detectCollision(shot, this.ship)) {
                this.onPlayerHit();
            }
        }
    }

    animateExplosionShot(shot) {
        if (shot.maxLifeTicks <= 0) {
            return;
        }

        const progress = 1.0 - (shot.lifeTicks / shot.maxLifeTicks);
        const scale = 1.0 + progress * 0.8;
        const scaledWidth = Math.max(shot.baseWidth, Math.round(shot.baseWidth * scale));
        const scaledHeight = Math.max(shot.baseHeight, Math.round(shot.baseHeight * scale));
        const centerX = shot.baseX + shot.baseWidth / 2;
        const centerY = shot.baseY + shot.baseHeight / 2;

        shot.width = scaledWidth;
        shot.height = scaledHeight;
        shot.x = centerX - scaledWidth / 2;
        shot.y = centerY - scaledHeight / 2;
    }

    moveMeteors() {
        for (let i = 0; i < this.meteorArray.length; i++) {
            const meteor = this.meteorArray[i];
            meteor.y += meteor.velocityY;

            if (meteor.active && this.detectCollision(meteor, this.ship)) {
                this.onPlayerHit();
            }
        }
    }

    spawnMeteors() {
        const profile = this.meteorProfiles.get(this.level) || this.meteorProfiles.get(5);
        if (!profile) {
            return;
        }

        const launchers = this.getBottomAliveEnemiesByColumn();
        if (launchers.size === 0) {
            return;
        }

        for (const launcher of launchers.values()) {
            const spawnMultiplier = this.getObjectSpawnMultiplierForLevel();
            if (profile.smallSpawnChance > 0 && Math.random() < profile.smallSpawnChance * spawnMultiplier) {
                this.meteorArray.push(this.createMeteorFromEnemy(launcher, false));
            }
            if (profile.bigSpawnChance > 0 && Math.random() < profile.bigSpawnChance * spawnMultiplier) {
                this.meteorArray.push(this.createMeteorFromEnemy(launcher, true));
            }
        }
    }

    getObjectSpawnMultiplierForLevel() {
        if (this.level === 6) return 0.5;
        if (this.level === 5) return 0.45;
        if (this.level === 4) return 0.3;
        if (this.level === 3) return 0.4;
        return 1.0;
    }

    createMeteorFromEnemy(enemy, bigMeteor) {
        const meteorSize = bigMeteor ? this.tileSize : Math.max(this.tileSize / 2, 12);
        const spawnX = enemy.x + enemy.width / 2 - meteorSize / 2;
        const spawnY = enemy.y + enemy.height;
        const speed = bigMeteor ? Balance.BIG_METEOR_SPEED : Balance.SMALL_METEOR_SPEED;
        const img = bigMeteor ? this.meteorBigImg : this.meteorSmallImg;
        return new Meteor(spawnX, spawnY, meteorSize, meteorSize, speed, img);
    }

    getBottomAliveEnemiesByColumn() {
        const launchers = new Map();
        for (let i = 0; i < this.alienArray.length; i++) {
            const alien = this.alienArray[i];
            if (!alien.alive) continue;

            const current = launchers.get(alien.col);
            if (current === undefined || alien.row > current.row) {
                launchers.set(alien.col, alien);
            }
        }
        return launchers;
    }

    cleanupBullets() {
        while (this.bulletArray.length > 0 && (!this.bulletArray[0].active || this.bulletArray[0].y < -this.bulletHeight)) {
            this.bulletArray.shift();
        }
    }

    cleanupEnemyShots() {
        while (this.enemyShotArray.length > 0 && (!this.enemyShotArray[0].active || this.enemyShotArray[0].y > this.boarderheight)) {
            this.enemyShotArray.shift();
        }
    }

    cleanupMeteors() {
        while (this.meteorArray.length > 0 && (!this.meteorArray[0].active || this.meteorArray[0].y > this.boarderheight)) {
            this.meteorArray.shift();
        }
    }

    onPlayerHit() {
        if (this.invulnerableTicks > 0 || this.gameState !== GameState.RUNNING) {
            return;
        }

        this.lives--;
        this.playerDamaged = true;
        this.ship.img = this.playerDamagedImg !== null ? this.playerDamagedImg : this.playerDefaultImg;
        this.invulnerableTicks = Balance.PLAYER_HIT_INVULNERABLE_TICKS;

        if (this.lives <= 0) {
            this.gameOver = true;
            this.gameState = GameState.GAME_OVER;
            return;
        }

        this.ship.x = this.shipX;
        this.ship.y = this.shipY;
        this.bulletArray = [];
        this.enemyShotArray = [];
        this.meteorArray = [];
    }

    resetWavePosition() {
        for (let i = 0; i < this.alienArray.length; i++) {
            const alien = this.alienArray[i];
            alien.preciseY = this.alienY + alien.row * this.alienHeight;
            alien.y = Math.round(alien.preciseY);
        }
    }

    startNextLevel() {
        this.score += this.alienCols * this.alienRows * Balance.SCORE_WAVE_CLEAR;
        const nextLevel = this.level + 1;
        if (nextLevel > Balance.MAX_LEVEL) {
            this.gameState = GameState.WIN;
            return;
        }

        this.level = nextLevel;
        this.lives++;
        this.playerDamaged = false;
        this.ship.img = this.playerDefaultImg;
        this.alienCols = Math.min(this.alienCols + 1, this.cols / 2 - 2);
        this.alienRows = Math.min(this.alienRows + 1, this.rows - 6);
        this.bulletArray = [];
        this.enemyShotArray = [];
        this.meteorArray = [];
        this.createAliens();
    }

    startLevel(targetLevel) {
        this.level = Math.max(1, Math.min(Balance.MAX_LEVEL, targetLevel));
        this.alienDirection = 1;
        this.alienSpeed = Balance.alienSpeedForLevel(this.level);
        this.bulletArray = [];
        this.enemyShotArray = [];
        this.meteorArray = [];
        this.createAliens();
    }

    createAliens() {
        this.alienArray = [];
        this.alienSpeed = Balance.alienSpeedForLevel(this.level);

        if (this.level === 6) {
            this.createLevel6Formation();
            return;
        }

        for (let c = 0; c < this.alienCols; c++) {
            for (let r = 0; r < this.alienRows; r++) {
                this.alienArray.push(this.createAlienForLevel(c, r));
            }
        }
        this.alienCount = this.alienArray.length;
    }

    createLevel6Formation() {
        this.alienCols = 7;
        this.alienRows = 5;

        const wShape = [
            [true, false, false, false, false, false, true],
            [true, false, false, true, false, false, true],
            [true, false, false, true, false, false, true],
            [true, false, true, false, true, false, true],
            [false, true, false, false, false, true, false]
        ];

        for (let c = 0; c < this.alienCols; c++) {
            for (let r = 0; r < this.alienRows; r++) {
                const alien = this.createAlienForLevel(c, r);
                if (wShape[r][c]) {
                    alien.enemyType = ELITE_UFO;
                    alien.maxHealth = 2;
                    alien.health = 2;
                    alien.img = this.eliteEnemyImg;
                }
                this.alienArray.push(alien);
            }
        }
        this.alienCount = this.alienArray.length;
    }

    createAlienForLevel(col, row) {
        const enemyType = this.pickEnemyTypeForLevel();
        const enemyImage = enemyType === ELITE_UFO ? this.eliteEnemyImg : this.regularEnemyImg;
        const alien = new Block(
            this.alienX + col * this.alienWidth,
            this.alienY + row * this.alienHeight,
            this.alienWidth,
            this.alienHeight,
            enemyImage
        );
        alien.enemyType = enemyType;
        alien.maxHealth = enemyType === ELITE_UFO ? 2 : 1;
        alien.health = alien.maxHealth;
        alien.col = col;
        alien.row = row;
        return alien;
    }

    pickEnemyTypeForLevel() {
        if (this.level === 1 || this.level === 6) return REGULAR_UFO;
        if (this.level === 2 || this.level === 5) return Math.random() < 0.5 ? REGULAR_UFO : ELITE_UFO;
        return ELITE_UFO; // levels 3, 4
    }

    detectCollision(a, b) {
        return a.x < b.x + b.width &&
            a.x + a.width > b.x &&
            a.y < b.y + b.height &&
            a.y + a.height > b.y;
    }

    getAlienFallbackColor(alien) {
        if (alien.enemyType === ELITE_UFO) {
            return alien.health === 1 ? 'orange' : 'red';
        }
        return 'cyan';
    }

    setupKeyBindings() {
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                if (this.gameState === GameState.MENU) {
                    this.startNewGame();
                } else if (this.gameState === GameState.GAME_OVER || this.gameState === GameState.WIN) {
                    this.gameState = GameState.MENU;
                }
            } else if (e.key === 'ArrowLeft') {
                e.preventDefault();
                this.movingLeft = true;
            } else if (e.key === 'ArrowRight') {
                e.preventDefault();
                this.movingRight = true;
            } else if (e.key === ' ') {
                e.preventDefault();
                if (this.gameState === GameState.RUNNING) {
                    this.fireBullet();
                } else if (this.gameState === GameState.GAME_OVER) {
                    this.restartGame();
                }
            }
        });

        document.addEventListener('keyup', (e) => {
            if (e.key === 'ArrowLeft') {
                e.preventDefault();
                this.movingLeft = false;
            } else if (e.key === 'ArrowRight') {
                e.preventDefault();
                this.movingRight = false;
            }
        });
    }

    restartGame() {
        this.startNewGame();
    }

    startNewGame() {
        this.ship.x = this.shipX;
        this.ship.y = this.shipY;
        this.ship.img = this.playerDefaultImg;
        this.playerDamaged = false;
        this.movingLeft = false;
        this.movingRight = false;
        this.useRedPlayerShot = true;
        this.bulletArray = [];
        this.enemyShotArray = [];
        this.meteorArray = [];
        this.gameOver = false;
        this.gameState = GameState.RUNNING;
        this.score = 0;
        this.lives = Balance.STARTING_LIVES;
        this.invulnerableTicks = 0;
        this.alienCols = 3;
        this.alienRows = 2;
        this.startLevel(1);
    }

    fireBullet() {
        let bulletImg;
        if (this.level === 1 || this.level === 2 || this.level === 3) {
            // Levels 1-3: only red
            bulletImg = this.laserRedImg;
        } else if (this.level === 4 || this.level === 5) {
            // Levels 4-5: only green
            bulletImg = this.laserGreenImg;
        } else {
            // Level 6: alternating red and green
            bulletImg = this.useRedPlayerShot ? this.laserRedImg : this.laserGreenImg;
            this.useRedPlayerShot = !this.useRedPlayerShot;
        }

        const bulletX = this.ship.x + this.shipWidth / 2 - this.bulletWidth / 2;
        const bulletY = this.ship.y - this.bulletHeight;
        const bullet = new Projectile(bulletX, bulletY, this.bulletWidth + 4, this.bulletHeight + 2, this.bulletVelocity, bulletImg);
        this.bulletArray.push(bullet);
    }

    startGameLoop() {
        setInterval(() => {
            this.move();
            this.draw();
        }, Balance.FPS_DELAY_MS);
    }
}

// Initialize the game when the page loads
window.addEventListener('DOMContentLoaded', () => {
    const canvas = document.getElementById('gameCanvas');
    if (canvas) {
        const game = new SpaceInvaders(canvas);
    }
});
