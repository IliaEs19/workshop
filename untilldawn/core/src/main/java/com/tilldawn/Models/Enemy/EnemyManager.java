package com.tilldawn.Models.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tilldawn.Models.Bullet;
import com.tilldawn.Models.Item.Item;
import com.tilldawn.Models.Item.ItemType;

public class EnemyManager {
    private Array<Enemy> enemies;
    private Array<Item> items;
    private float gameTime;
    private float gameMaxTime;
    private float tentacleSpawnTimer;
    private float eyebatSpawnTimer;
    private boolean bossSpawned;

    // محدوده بازی
    private float worldWidth, worldHeight;

    // پارامترهای اسپاون دشمن‌ها
    private float baseSpawnInterval = 3.0f; // زمان پایه بین اسپاون‌ها
    private float minSpawnInterval = 0.5f; // حداقل زمان بین اسپاون‌ها

    public EnemyManager(float worldWidth, float worldHeight, float gameMaxTime) {
        this.enemies = new Array<>();
        this.items = new Array<>();
        this.gameTime = 0;
        this.gameMaxTime = gameMaxTime;
        this.tentacleSpawnTimer = 0;
        this.eyebatSpawnTimer = 0;
        this.bossSpawned = false;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        // بارگذاری تکسچرهای دشمن‌ها و آیتم‌ها
        EnemyType.loadTextures();
        ItemType.loadTextures();

        // ایجاد درخت‌ها در مکان‌های تصادفی
        spawnInitialTrees(20);
    }

    private void spawnInitialTrees(int count) {
        for (int i = 0; i < count; i++) {
            float x = MathUtils.random(100, worldWidth - 100);
            float y = MathUtils.random(100, worldHeight - 100);

            TreeEnemy tree = new TreeEnemy(x, y);
            enemies.add(tree);
        }
    }

    public void update(float delta, Vector2 playerPosition) {
        // بروزرسانی زمان بازی
        gameTime += delta;

        // بروزرسانی همه دشمن‌های موجود
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(delta, playerPosition);

            // حذف دشمن‌های مرده و دراپ آیتم
            if (!enemy.isAlive()) {
                // دراپ آیتم تجربه با احتمال 100%
                Item xpItem = new Item(ItemType.EXPERIENCE, enemy.getPosition().x, enemy.getPosition().y);
                items.add(xpItem);

                // دراپ آیتم دیگر با احتمال کمتر
                if (Math.random() < 0.3) { // 30% احتمال
                    ItemType randomItemType = getRandomItemType();
                    if (randomItemType != null && randomItemType != ItemType.EXPERIENCE) {
                        Item item = new Item(randomItemType,
                            enemy.getPosition().x + (float)(Math.random() * 20 - 10),
                            enemy.getPosition().y + (float)(Math.random() * 20 - 10));
                        items.add(item);
                    }
                }

                enemies.removeIndex(i);
            }
        }

        // بروزرسانی آیتم‌ها
        for (int i = items.size - 1; i >= 0; i--) {
            Item item = items.get(i);
            item.update(delta);

            if (item.isExpired()) {
                items.removeIndex(i);
            }
        }

        // بررسی ایجاد دشمن‌های جدید
        updateEnemySpawning(delta, playerPosition);
    }

    private void updateEnemySpawning(float delta, Vector2 playerPosition) {
        // محاسبه فاصله زمانی اسپاون با توجه به پیشرفت بازی
        float progress = Math.min(gameTime / gameMaxTime, 1.0f);
        float currentSpawnInterval = baseSpawnInterval - (baseSpawnInterval - minSpawnInterval) * progress;

        // ایجاد Tentacle Monster
        tentacleSpawnTimer += delta;
        if (gameTime >= 0 && tentacleSpawnTimer >= currentSpawnInterval) {
            spawnTentacleMonster(playerPosition);
            tentacleSpawnTimer = 0;
        }

        // ایجاد Eyebat با نرخ متفاوت
        eyebatSpawnTimer += delta;
        float eyebatSpawnInterval = currentSpawnInterval * 2; // دو برابر زمان اسپاون معمولی
        if (gameTime >= gameMaxTime / 4 && eyebatSpawnTimer >= eyebatSpawnInterval) {
            spawnEyebat(playerPosition);
            eyebatSpawnTimer = 0;
        }

        // ایجاد باس در نیمه بازی
        if (!bossSpawned && gameTime >= gameMaxTime / 2) {
            spawnElder(playerPosition);
            bossSpawned = true;
        }
    }

    private ItemType getRandomItemType() {
        ItemType[] types = ItemType.values();
        int index = (int)(Math.random() * types.length);
        return types[index];
    }

    private void spawnTentacleMonster(Vector2 playerPosition) {
        // ایجاد دشمن در فاصله مناسب از بازیکن
        Vector2 spawnPos = getSpawnPosition(playerPosition, 300, 500);
        TentacleMonsterEnemy enemy = new TentacleMonsterEnemy(spawnPos.x, spawnPos.y);
        enemies.add(enemy);
    }

    private void spawnEyebat(Vector2 playerPosition) {
        // ایجاد دشمن در فاصله مناسب از بازیکن
        Vector2 spawnPos = getSpawnPosition(playerPosition, 400, 600);
        EyebatEnemy enemy = new EyebatEnemy(spawnPos.x, spawnPos.y);
        enemies.add(enemy);
    }

    private void spawnElder(Vector2 playerPosition) {
        // ایجاد باس در فاصله مناسب از بازیکن
        Vector2 spawnPos = getSpawnPosition(playerPosition, 500, 700);
        ElderEnemy enemy = new ElderEnemy(spawnPos.x, spawnPos.y, gameMaxTime);
        enemies.add(enemy);
    }

    private Vector2 getSpawnPosition(Vector2 playerPosition, float minDistance, float maxDistance) {
        // انتخاب یک زاویه تصادفی
        float angle = MathUtils.random(360);
        float radians = (float) Math.toRadians(angle);

        // انتخاب یک فاصله تصادفی بین min و max
        float distance = MathUtils.random(minDistance, maxDistance);

        // محاسبه موقعیت ایجاد
        float x = playerPosition.x + distance * (float) Math.cos(radians);
        float y = playerPosition.y + distance * (float) Math.sin(radians);

        // محدود کردن به مرزهای دنیای بازی
        x = MathUtils.clamp(x, 50, worldWidth - 50);
        y = MathUtils.clamp(y, 50, worldHeight - 50);

        return new Vector2(x, y);
    }

    public void render(SpriteBatch batch) {
        // رسم آیتم‌ها
        for (Item item : items) {
            item.render(batch);
        }

        // رسم دشمن‌ها
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
    }

    public void checkBulletCollisions(Array<Bullet> bullets) {
        if (bullets == null) return;

        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            Rectangle bulletBounds = bullet.getBounds();

            for (Enemy enemy : enemies) {
                if (enemy.checkBulletCollision(bulletBounds)) {
                    enemy.takeDamage(bullet.getDamage());
                    bullets.removeIndex(i);
                    break;
                }
            }
        }
    }

    public boolean checkPlayerCollisions(Rectangle playerBounds) {
        for (Enemy enemy : enemies) {
            if (enemy.checkPlayerCollision(playerBounds)) {
                return true;
            }
        }
        return false;
    }

    public Array<Item> checkItemCollisions(Rectangle playerBounds) {
        Array<Item> collectedItems = new Array<>();

        for (int i = items.size - 1; i >= 0; i--) {
            Item item = items.get(i);
            if (item.checkCollision(playerBounds)) {
                collectedItems.add(item);
                items.removeIndex(i);
            }
        }

        return collectedItems;
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public Array<Item> getItems() {
        return items;
    }

    public void dispose() {
        EnemyType.disposeTextures();
        ItemType.disposeTextures();
    }
}
