package ru.samsung.multiscreen;

import static ru.samsung.multiscreen.Main.SCR_HEIGHT;
import static ru.samsung.multiscreen.Main.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class ScreenVillage implements Screen {
    Main main;
    SpriteBatch batch;
    BitmapFont font;
    Vector3 touch;
    Texture imgBG;
    Texture imgForge, imgBarracks, imgLazaret;
    PokeButton btnBack;
    Music villageMusic;

    private static final float ICON_WIDTH = 200;
    private static final float ICON_HEIGHT = 200;
    private static final float FORGE_X = SCR_WIDTH / 2f - ICON_WIDTH / 2;
    private static final float FORGE_Y = 1200;
    private static final float BARRACKS_X = SCR_WIDTH / 2f - ICON_WIDTH / 2;
    private static final float BARRACKS_Y = 1000;
    private static final float LAZARET_X = SCR_WIDTH / 2f - ICON_WIDTH / 2;
    private static final float LAZARET_Y = 800;

    public ScreenVillage(Main main) {
        this.main = main;
        this.batch = main.batch;
        this.font = main.font;
        this.touch = main.touch;

        imgBG = new Texture("village2.jpg");

        imgForge = new Texture("forge.png");
        imgBarracks = new Texture("barracks.png");
        imgLazaret = new Texture("lazaret.png");

        try {
            villageMusic = Gdx.audio.newMusic(Gdx.files.internal("village.mp3"));
            villageMusic.setLooping(true);
        } catch (Exception e) {
            villageMusic = null;
        }

        if (this.font == null) {
            this.font = new BitmapFont();
        }

        btnBack = new PokeButton(font, "Back", 375, 300);
        btnBack.font.getData().setScale(1.0f);
    }

    @Override
    public void show() {
        if (main.isMusicEnabled && villageMusic != null) {
            villageMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            main.camera.unproject(touch);

            if (touch.x > FORGE_X && touch.x < FORGE_X + ICON_WIDTH && touch.y > FORGE_Y && touch.y < FORGE_Y + ICON_HEIGHT) {
                if (main.totalWood >= 1 && main.totalIron >= 1) {
                    main.totalWood -= 1;
                    main.totalIron -= 1;
                    main.forgeLevel++;
                    main.saveGameState();
                }
            }
            if (touch.x > BARRACKS_X && touch.x < BARRACKS_X + ICON_WIDTH && touch.y > BARRACKS_Y && touch.y < BARRACKS_Y + ICON_HEIGHT) {
                if (main.totalWood >= 1 && main.totalStone >= 1) {
                    main.totalWood -= 1;
                    main.totalStone -= 1;
                    main.barracksLevel++;
                    main.saveGameState();
                }
            }
            if (touch.x > LAZARET_X && touch.x < LAZARET_X + ICON_WIDTH && touch.y > LAZARET_Y && touch.y < LAZARET_Y + ICON_HEIGHT) {
                if (main.totalIron >= 1 && main.totalStone >= 1) {
                    main.totalIron -= 1;
                    main.totalStone -= 1;
                    main.lazaretLevel++;
                    main.saveGameState();
                }
            }
            if (btnBack.hit(touch.x, touch.y)) {
                if (main.screenMap != null) {
                    main.setScreen(main.screenGame);
                }
            }
        }

        batch.setProjectionMatrix(main.camera.combined);
        batch.begin();

        batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);

        batch.draw(imgForge, FORGE_X, FORGE_Y, ICON_WIDTH, ICON_HEIGHT);
        batch.draw(imgBarracks, BARRACKS_X, BARRACKS_Y, ICON_WIDTH, ICON_HEIGHT);
        batch.draw(imgLazaret, LAZARET_X, LAZARET_Y, ICON_WIDTH, ICON_HEIGHT);

        btnBack.render(batch, Color.BLACK);

        font.setColor(Color.BROWN);
        font.getData().setScale(1.0f);
        font.draw(batch, "Wood: " + main.totalWood, 40, SCR_HEIGHT - 50);
        font.setColor(Color.DARK_GRAY);
        font.draw(batch, "Iron: " + main.totalIron, 40, SCR_HEIGHT - 100);
        font.setColor(Color.BLACK);
        font.draw(batch, "Stone: " + main.totalStone, 40, SCR_HEIGHT - 150);

        font.setColor(Color.YELLOW);
        font.draw(batch, "Forge Level: " + main.forgeLevel, SCR_WIDTH / 2f - 100, FORGE_Y + ICON_HEIGHT + 30);
        font.draw(batch, "+Dmg", SCR_WIDTH / 2f + 80, FORGE_Y + ICON_HEIGHT - 80);
        font.draw(batch, "Barracks Level: " + main.barracksLevel, SCR_WIDTH / 2f - 100, BARRACKS_Y + ICON_HEIGHT + 30);
        font.draw(batch, "+Speed", SCR_WIDTH / 2f + 80, BARRACKS_Y + ICON_HEIGHT - 80);
        font.draw(batch, "Lazaret Level: " + main.lazaretLevel, SCR_WIDTH / 2f - 100, LAZARET_Y + ICON_HEIGHT + 30);
        font.draw(batch, "+HP", SCR_WIDTH / 2f + 80, LAZARET_Y + ICON_HEIGHT - 80);

        batch.end();
    }

    @Override
    public void hide() {
        if (villageMusic != null && villageMusic.isPlaying()) {
            villageMusic.stop();
        }
    }

    @Override
    public void dispose() {
        imgBG.dispose();
        imgForge.dispose();
        imgBarracks.dispose();
        imgLazaret.dispose();
        if (villageMusic != null) {
            villageMusic.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
