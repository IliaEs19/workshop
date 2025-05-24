package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;

public class GameCursor {

    private static Cursor customCursor;
    private static boolean initialized = false;

        public static void initialize() {
        if (!initialized) {

            customCursor = createGameCursor();


            if (customCursor != null) {
                Gdx.graphics.setCursor(customCursor);
            }

            initialized = true;
        }
    }

        public static void dispose() {
        if (customCursor != null) {
            customCursor.dispose();
            customCursor = null;
        }
        initialized = false;
    }

        private static Cursor createGameCursor() {

        int cursorSize = 64;
        int centerPoint = cursorSize / 2;


        Pixmap pixmap = new Pixmap(cursorSize, cursorSize, Pixmap.Format.RGBA8888);


        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();





        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.drawCircle(centerPoint, centerPoint, 21);


        pixmap.setColor(0, 0, 0, 1);

        pixmap.fillRectangle(centerPoint - 15, centerPoint - 1, 30, 3);

        pixmap.fillRectangle(centerPoint - 1, centerPoint - 15, 3, 30);


        pixmap.setColor(0.7f, 0, 0, 1);
        pixmap.drawCircle(centerPoint, centerPoint, 12);
        pixmap.drawCircle(centerPoint, centerPoint, 11);


        pixmap.setColor(1, 0, 0, 1);
        pixmap.drawCircle(centerPoint, centerPoint, 6);
        pixmap.drawCircle(centerPoint, centerPoint, 5);


        pixmap.setColor(1, 1, 1, 1);
        pixmap.fillCircle(centerPoint, centerPoint, 2);


        Cursor cursor = Gdx.graphics.newCursor(pixmap, centerPoint, centerPoint);


        pixmap.dispose();

        return cursor;
    }
}
