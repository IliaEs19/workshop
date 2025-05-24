package com.tilldawn.Models.Enemy;

import com.badlogic.gdx.math.Vector2;

public class TentacleMonsterEnemy extends Enemy {

    public TentacleMonsterEnemy(float x, float y) {
        super(EnemyType.TENTACLE_MONSTER, x, y, 40, 40);
        this.speed = 60;
    }

    @Override
    protected void updateBehavior(float delta, Vector2 playerPosition) {

        float dx = playerPosition.x - x;
        float dy = playerPosition.y - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length > 0) {
            dx /= length;
            dy /= length;

            x += dx * speed * delta;
            y += dy * speed * delta;
        }
    }
}
