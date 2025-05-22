package ru.samsung.multiscreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import static ru.samsung.multiscreen.Main.SCR_HEIGHT;
import static ru.samsung.multiscreen.Main.SCR_WIDTH;

public class ScreenMap implements Screen {
    Main main;
    SpriteBatch batch;
    Vector3 touch;
    BitmapFont font;

    Texture imgBG; // battle.jpg
    Texture imgKnight, imgEnt, imgDwarf, imgGolem, imgCircle;

    // Hero position and movement
    Vector2 heroPos;
    float heroSpeed = 300f; // Pixels per second
    float heroWidth = 150;
    float heroHeight = 150;

    // Enemy positions
    Vector2 entPos, DwarfPos, golemPos;
    float enemyWidth = 150;
    float enemyHeight = 150;

    // Back label position
    Vector2 backPos;
    float backWidth, backHeight;
    float collisionDistance = 100f; // Distance to trigger battle or back

    // Joystick
    Vector2 joystickCenter;
    float joystickRadius = 125f;
    boolean isJoystickActive = false;
    Vector2 joystickDirection;

    // Enemy states (to track if defeated)
    boolean isEntDefeated = false;
    boolean isDwarfDefeated = false;
    boolean isGolemDefeated = false;

    // Music
    Music mapMusic;

    public ScreenMap(Main main) {
        this.main = main;
        this.batch = main.batch;
        this.touch = main.touch;
        this.font = main.font;

        imgBG = new Texture("arena.jpg");
        imgKnight = new Texture("knight.png");
        imgCircle = new Texture("circle.png");
        imgEnt = new Texture("ent.png");
        imgDwarf = new Texture("dwarf.png");
        imgGolem = new Texture("golem.png");

        // Initialize positions
        heroPos = new Vector2(SCR_WIDTH / 2f, SCR_HEIGHT / 2f); // Start hero in the center
        entPos = new Vector2(200, 1200); // Top-left
        DwarfPos = new Vector2(600, 1200); // Top-right
        golemPos = new Vector2(400, 1200); // Middle
        backPos = new Vector2(400, 300); // Bottom center

        // Calculate back label bounds
        font.getData().setScale(1.0f);
        backWidth = font.getSpaceXadvance() * "Back".length();
        backHeight = font.getLineHeight();

        // Joystick setup
        joystickCenter = new Vector2(150, 150); // Bottom-left corner
        joystickDirection = new Vector2();

        // Load music
        mapMusic = Gdx.audio.newMusic(Gdx.files.internal("Map.mp3"));
        mapMusic.setLooping(true);
    }

    @Override
    public void show() {
        if (main.isMusicEnabled && mapMusic != null) {
            mapMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        // Handle joystick input
        joystickDirection.set(0, 0);
        if (Gdx.input.isTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            main.camera.unproject(touch);
            Vector2 touchPos = new Vector2(touch.x, touch.y);

            // Check if touch is within joystick area
            float distanceFromJoystick = touchPos.dst(joystickCenter);
            if (distanceFromJoystick <= joystickRadius * 2) { // Larger touch area for usability
                isJoystickActive = true;
                Vector2 direction = touchPos.sub(joystickCenter);
                if (direction.len() > joystickRadius) {
                    direction.nor().scl(joystickRadius);
                }
                joystickDirection.set(direction).nor(); // Normalize for consistent speed
            } else {
                // Check for Back label click
                if (touch.x > backPos.x - backWidth / 2 && touch.x < backPos.x + backWidth / 2 &&
                    touch.y > backPos.y - backHeight && touch.y < backPos.y) {
                    main.setScreen(main.screenGame);
                    return;
                }
            }
        } else {
            isJoystickActive = false;
        }

        // Move hero
        if (isJoystickActive) {
            heroPos.x += joystickDirection.x * heroSpeed * delta;
            heroPos.y += joystickDirection.y * heroSpeed * delta;

            // Keep hero within screen bounds
            heroPos.x = Math.max(0, Math.min(SCR_WIDTH - heroWidth, heroPos.x));
            heroPos.y = Math.max(0, Math.min(SCR_HEIGHT - heroHeight, heroPos.y));
        }

        // Check for collisions with enemies
        if (heroPos.dst(entPos) < collisionDistance) {
            main.screenBattle = new ScreenBattle(main, "Ent");
            main.setScreen(main.screenBattle);
            return;
        }
        if (heroPos.dst(DwarfPos) < collisionDistance) {
            main.screenBattle = new ScreenBattle(main, "Dwarf");
            main.setScreen(main.screenBattle);
            return;
        }
        if (heroPos.dst(golemPos) < collisionDistance) {
            main.screenBattle = new ScreenBattle(main, "Golem");
            main.setScreen(main.screenBattle);
            return;
        }

        // Render
        batch.setProjectionMatrix(main.camera.combined);
        batch.begin();
        batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);

        // Draw hero
        batch.draw(imgKnight, heroPos.x, heroPos.y, heroWidth, heroHeight);

        // Draw enemies if not defeated
        batch.draw(imgEnt, entPos.x, entPos.y, enemyWidth, enemyHeight);
        batch.draw(imgDwarf, DwarfPos.x, DwarfPos.y, enemyWidth, enemyHeight);
        batch.draw(imgGolem, golemPos.x, golemPos.y, enemyWidth, enemyHeight);

        // Draw Back label
        font.setColor(Color.BLACK);
        font.getData().setScale(1.0f);
        font.draw(batch, "Back", backPos.x - backWidth / 2, backPos.y);

        // Draw joystick
        batch.setColor(1, 1, 1, 0.5f); // Semi-transparent
        batch.draw(imgCircle, joystickCenter.x - joystickRadius, joystickCenter.y - joystickRadius, joystickRadius * 2, joystickRadius * 2); // Placeholder joystick base
        if (isJoystickActive) {
            batch.setColor(1, 1, 1, 0.8f);
            Vector2 knobPos = joystickCenter.cpy().add(joystickDirection.scl(joystickRadius));
            batch.draw(imgCircle, knobPos.x - joystickRadius / 2, knobPos.y - joystickRadius / 2, joystickRadius, joystickRadius); // Placeholder knob
        }
        batch.setColor(1, 1, 1, 1f); // Reset color

        batch.end();
    }

    // Method to update enemy state after battle
    public void onBattleEnd(String enemyName, boolean victory) {
        float enemyY = 0;
        if (victory) {
            switch (enemyName) {
                case "Ent":
                    isEntDefeated = true;
                    enemyY = entPos.y;
                    break;
                case "Dwarf":
                    isDwarfDefeated = true;
                    enemyY = DwarfPos.y;
                    break;
                case "Golem":
                    isGolemDefeated = true;
                    enemyY = golemPos.y;
                    break;
            }
        } else {
            // Even if not victorious, set the position based on the enemy fought
            switch (enemyName) {
                case "Ent":
                    enemyY = entPos.y;
                    break;
                case "Dwarf":
                    enemyY = DwarfPos.y;
                    break;
                case "Golem":
                    enemyY = golemPos.y;
                    break;
            }
        }
        // Set hero position 200 pixels below the enemy's y position
        heroPos.y = enemyY - 200;
        // Ensure hero stays within screen bounds after adjustment
        heroPos.y = Math.max(0, Math.min(SCR_HEIGHT - heroHeight, heroPos.y));
        // Return to map after battle
        main.setScreen(this);
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        if (mapMusic != null && mapMusic.isPlaying()) {
            mapMusic.stop();
        }
    }

    @Override
    public void dispose() {
        imgBG.dispose();
        imgKnight.dispose();
        imgEnt.dispose();
        imgDwarf.dispose();
        imgGolem.dispose();
        if (mapMusic != null) {
            mapMusic.dispose();
        }
    }
}
