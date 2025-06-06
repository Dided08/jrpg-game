package ru.samsung.multiscreen;

import static ru.samsung.multiscreen.Main.SCR_HEIGHT;
import static ru.samsung.multiscreen.Main.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class ScreenAbout implements Screen {
    Main main;
    public OrthographicCamera camera;
    public SpriteBatch batch;
    public Vector3 touch;
    public BitmapFont font;

    Texture imgBG;

    PokeButton btnBack;

    // Описание игры
    private static final String gameDescription =
        "Welcome to a thrilling JRPG adventure! Control a team of three unique heroes as they battle " +
            "three distinct enemies in a quest for glory. Defeat foes to gather resources and upgrade " +
            "your heroes’ skills and abilities.";

    public ScreenAbout(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font = main.font;

        imgBG = new Texture("bg5.jpg");

        btnBack = new PokeButton(font, "Back", 375, 300);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if (btnBack.hit(touch.x, touch.y)) {
                main.setScreen(main.screenMenu);
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        // Отображаем описание игры с переносом текста
        font.draw(batch, gameDescription, 100, 1100, SCR_WIDTH - 200, 1, true);
        font.draw(batch, "About", 100, 1300, SCR_WIDTH - 200, 1, true);
        btnBack.font.draw(batch, btnBack.text, btnBack.x, btnBack.y);

        batch.end();
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

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        imgBG.dispose();
    }
}
