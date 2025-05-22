package ru.samsung.multiscreen;

import static ru.samsung.multiscreen.Main.SCR_HEIGHT;
import static ru.samsung.multiscreen.Main.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class ScreenSettings implements Screen {
    Main main;
    public SpriteBatch batch;
    public Vector3 touch;
    public BitmapFont font;

    Texture imgBG;

    PokeButton btnBack;
    PokeButton btnResetSave;
    PokeButton btnMusicToggle;

    public ScreenSettings(Main main) {
        this.main = main;
        batch = main.batch;
        touch = main.touch;
        font = main.font;

        imgBG = new Texture("bg5.jpg");

        btnBack = new PokeButton(font, "Back", 375, 300);
        btnBack.font.getData().setScale(1.0f);
        btnResetSave = new PokeButton(font, "Reset Save", 325, 800);
        btnResetSave.font.getData().setScale(1.0f);
        btnMusicToggle = new PokeButton(font, main.isMusicEnabled ? "Music: On" : "Music: Off", 325, 1000);
        btnMusicToggle.font.getData().setScale(1.0f);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            main.camera.unproject(touch);

            if (btnBack.hit(touch.x, touch.y)) {
                main.setScreen(main.screenMenu);
            }

            if (btnResetSave.hit(touch.x, touch.y)) {
                main.totalWood = 0;
                main.totalIron = 0;
                main.totalStone = 0;
                main.forgeLevel = 0;
                main.lazaretLevel = 0;
                main.barracksLevel = 0;

                Preferences prefs = Gdx.app.getPreferences("MultiScreenGame");
                prefs.putInteger("totalWood", 0);
                prefs.putInteger("totalIron", 0);
                prefs.putInteger("totalStone", 0);
                prefs.putInteger("forgeLevel", 0);
                prefs.putInteger("lazaretLevel", 0);
                prefs.putInteger("barracksLevel", 0);
                prefs.flush();
            }

            if (btnMusicToggle.hit(touch.x, touch.y)) {
                main.isMusicEnabled = !main.isMusicEnabled;
                btnMusicToggle.setText(main.isMusicEnabled ? "Music: On" : "Music: Off");

                // Stop music in all screens if toggled off
                if (!main.isMusicEnabled) {
                    if (main.screenVillage != null) {
                        ScreenVillage village = main.screenVillage;
                        if (village.villageMusic != null && village.villageMusic.isPlaying()) {
                            village.villageMusic.stop();
                        }
                    }
                    if (main.screenBattle != null) {
                        ScreenBattle battle = main.screenBattle;
                        if (battle.battleMusic != null && battle.battleMusic.isPlaying()) {
                            battle.battleMusic.stop();
                        }
                        if (battle.victoryMusic != null && battle.victoryMusic.isPlaying()) {
                            battle.victoryMusic.stop();
                        }
                        if (battle.loseMusic != null && battle.loseMusic.isPlaying()) {
                            battle.loseMusic.stop();
                        }
                    }
                    if (main.screenMap != null) { // Added to stop map music
                        ScreenMap map = main.screenMap;
                        if (map.mapMusic != null && map.mapMusic.isPlaying()) {
                            map.mapMusic.stop();
                        }
                    }
                }
            }
        }

        batch.setProjectionMatrix(main.camera.combined);
        batch.begin();
        batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);

        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
        font.draw(batch, "Settings", 350, 1300);

        btnBack.render(batch, Color.WHITE);
        btnResetSave.render(batch, Color.GREEN);
        btnMusicToggle.render(batch, Color.GREEN);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        imgBG.dispose();
    }
}
