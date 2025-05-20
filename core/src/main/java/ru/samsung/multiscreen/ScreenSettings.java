package ru.samsung.multiscreen;

import static ru.samsung.multiscreen.Main.SCR_HEIGHT;
import static ru.samsung.multiscreen.Main.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class ScreenSettings implements Screen {
    Main main;
    public OrthographicCamera camera;
    public SpriteBatch batch;
    public Vector3 touch;
    public BitmapFont font;

    Texture imgBG;

    PokeButton btnBack;
    PokeButton btnResetSave; // Добавлено для сброса сохранения

    public ScreenSettings(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font = main.font;

        imgBG = new Texture("bg5.jpg");

        btnBack = new PokeButton(font, "Back", 400, 300);
        btnBack.font.getData().setScale(1.0f);
        btnResetSave = new PokeButton(font, "Reset Save", 325, 900);
        btnResetSave.font.getData().setScale(1.0f);

        System.out.println("ScreenSettings initialized. Font: " + (font != null));
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            System.out.println("Touch at: x=" + touch.x + ", y=" + touch.y);

            if (btnBack.hit(touch.x, touch.y)) {
                System.out.println("Returning to ScreenMenu");
                main.setScreen(main.screenMenu);
            }

            if (btnResetSave.hit(touch.x, touch.y)) {
                System.out.println("Resetting save data");
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

                System.out.println("Save reset: Wood=" + main.totalWood + ", Iron=" + main.totalIron +
                    ", Stone=" + main.totalStone + ", Forge=" + main.forgeLevel +
                    ", Lazaret=" + main.lazaretLevel + ", Barracks=" + main.barracksLevel);
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);

        // Заголовок
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
        font.draw(batch, "Settings", 350, 1300);

        // Отрисовка кнопок
        btnBack.render(batch, Color.WHITE);
        btnResetSave.render(batch, Color.RED);

        // Отладка координат кнопок
        System.out.println("Back button at x=" + btnBack.x + ", y=" + btnBack.y + ", text: " + btnBack.text);
        System.out.println("Reset Save button at x=" + btnResetSave.x + ", y=" + btnResetSave.y + ", text: " + btnResetSave.text);

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
