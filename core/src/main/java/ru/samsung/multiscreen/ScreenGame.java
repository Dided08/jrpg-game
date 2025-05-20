package ru.samsung.multiscreen;

import static ru.samsung.multiscreen.Main.SCR_HEIGHT;
import static ru.samsung.multiscreen.Main.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class ScreenGame implements Screen {
    Main main;
    public OrthographicCamera camera;
    public SpriteBatch batch;
    public Vector3 touch;
    public BitmapFont font;

    Texture imgBG;

    PokeButton btnBack;
    PokeButton btnVillage;
    PokeButton btnFight;

    public ScreenGame(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font = main.font;

        imgBG = new Texture("game.jpg");

        btnBack = new PokeButton(font, "Back", 375, 300);
        btnBack.font.getData().setScale(1.0f);
        btnVillage = new PokeButton(font, "Village", 350, 800);
        btnVillage.font.getData().setScale(1.0f);
        btnFight = new PokeButton(font, "Fight", 375, 1000);
        btnFight.font.getData().setScale(1.0f);

        System.out.println("ScreenGame initialized. Font: " + (font != null));
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
            if (btnVillage.hit(touch.x, touch.y)) {
                System.out.println("Opening ScreenVillage");
                main.setScreen(main.screenVillage);
            }
            if (btnFight.hit(touch.x, touch.y)) {
                System.out.println("Opening ScreenFightSelect");
                main.setScreen(new ScreenFightSelect(main));
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);

        // Отрисовка кнопок
        btnBack.render(batch, Color.BLACK);
        btnVillage.render(batch, Color.RED);
        btnFight.render(batch, Color.RED);

        // Отладка координат кнопок
        System.out.println("Back button at x=" + btnBack.x + ", y=" + btnBack.y + ", text: " + btnBack.text);
        System.out.println("Village button at x=" + btnVillage.x + ", y=" + btnVillage.y + ", text: " + btnVillage.text);
        System.out.println("Fight button at x=" + btnFight.x + ", y=" + btnFight.y + ", text: " + btnFight.text);

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
