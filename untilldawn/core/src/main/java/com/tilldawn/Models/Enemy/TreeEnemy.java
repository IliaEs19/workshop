package com.tilldawn.Models.Enemy;

import com.badlogic.gdx.math.Vector2;

public class TreeEnemy extends Enemy {

    public TreeEnemy(float x, float y) {
        super(EnemyType.TREE, x, y, 50, 50);
        this.speed = 0; // درخت‌ها حرکت نمی‌کنند
    }

    @Override
    protected void updateBehavior(float delta, Vector2 playerPosition) {
        // درخت‌ها رفتار خاصی ندارند، فقط در مکان خود می‌مانند
    }
}
